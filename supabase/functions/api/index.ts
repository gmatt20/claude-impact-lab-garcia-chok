// Bridge API. Two routes, matching the contract in BRIDGE_SHARED_CONTEXT.md:
//   GET  /api/bundle  -> everything the client needs to run offline from then on
//   POST /api/intake  -> Claude turns a typed sentence into need + constraints + safety route
//
// Claude is used for language understanding only. Resource facts come from the database.
import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "jsr:@supabase/supabase-js@2";
import Anthropic from "npm:@anthropic-ai/sdk@0.69.0";

const MODEL = Deno.env.get("CLAUDE_MODEL") ?? "claude-haiku-4-5";
// Only needed when the API key is not already scoped to a workspace.
const WORKSPACE_ID = Deno.env.get("ANTHROPIC_WORKSPACE_ID");
const MAX_MESSAGE_CHARS = 500;

// The 16 categories in the seed file. Claude may not invent one.
const CATEGORIES = [
  "shelter", "employment", "microenterprise", "family-reunification-travel",
  "housing-navigation", "id-documents", "transportation", "phone-internet",
  "benefits", "food", "health-coverage", "behavioral-health", "legal",
  "veterans", "hygiene-mail-storage", "other",
];

const CONSTRAINTS = [
  "no-phone", "phone-hard", "no-id", "no-transport", "needs-accessible",
  "no-internet", "spanish-only", "veteran", "has-pets", "leaving-recuperative-care",
];

// Read with the anon key so row level security still applies server-side:
// unreviewed model copy must stay invisible even here.
const db = () =>
  createClient(Deno.env.get("SUPABASE_URL")!, Deno.env.get("SUPABASE_ANON_KEY")!);

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
  "Access-Control-Allow-Headers": "content-type, authorization, apikey, x-client-info",
  "Access-Control-Max-Age": "86400",
};

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), {
    status,
    headers: { ...CORS, "Content-Type": "application/json", "Cache-Control": "no-store" },
  });

// A 204 must have a null body - building one with a body throws, which turned every
// browser preflight into a 500 and blocked the POST that followed.
const preflight = () => new Response(null, { status: 204, headers: CORS });

// ---------------------------------------------------------------- bundle

async function bundle() {
  const client = db();
  const [resources, explanations, prep] = await Promise.all([
    client.from("resources").select("*").order("id"),
    client.from("explanations").select("resource_id, need, language, text").eq("kind", "fit"),
    client.from("explanations").select("resource_id, language, kind, text")
      .in("kind", ["fits", "bring", "heads_up"]),
  ]);
  if (resources.error) return json({ error: resources.error.message }, 500);

  // resourceId -> need -> { en|es: text }. Only reviewed rows get this far (RLS).
  const nested: Record<string, Record<string, Record<string, string>>> = {};
  for (const row of explanations.data ?? []) {
    ((nested[row.resource_id] ??= {})[row.need] ??= {})[row.language] = row.text;
  }

  // "Before you go": resourceId -> language -> { fits[], bring[], headsUp }.
  // Lists are stored newline-separated. Unreviewed rows are filtered out by RLS, so a
  // record can legitimately have some languages or sections missing - render what is there.
  type Prep = { fits: string[]; bring: string[]; headsUp: string | null };
  const preparation: Record<string, Record<string, Prep>> = {};
  for (const row of prep.data ?? []) {
    const byLang = (preparation[row.resource_id] ??= {});
    const entry = (byLang[row.language] ??= { fits: [], bring: [], headsUp: null });
    if (row.kind === "heads_up") entry.headsUp = row.text;
    else if (row.kind === "fits") entry.fits = row.text.split("\n");
    else if (row.kind === "bring") entry.bring = row.text.split("\n");
  }

  return json({
    generated: new Date().toISOString().slice(0, 10),
    disclaimer:
      "Every entry was read from a public web page and may already be wrong. " +
      "Confirm eligibility, hours and availability directly with the organization.",
    resources: resources.data ?? [],
    explanations: nested,
    preparation,
  });
}

// ---------------------------------------------------------------- intake

// Deterministic safety net. It may only ESCALATE a route, never relax one:
// a missed danger or medical cue is the worst failure this endpoint can have.
const DANGER = [
  "not safe", "he'll find me", "he will find me", "she'll find me", "find me",
  "hurt me", "hit me", "beat me", "abuse", "abusive", "violent", "violence",
  "afraid of him", "afraid of her", "threaten", "stalk", "kill",
  "no estoy segur", "me va a encontrar", "me golpe", "me pega", "violencia",
  "abuso", "amenaz", "miedo de él", "miedo de ella",
];

const CLINICAL = [
  "my pills", "these pills", "water pills", "medication", "medicine", "dosage",
  "dose", "stop taking", "should i take", "side effect", "prescription",
  "insulin", "blood pressure", "my meds", "diagnos",
  "pastilla", "medicina", "medicamento", "dosis", "dejar de tomar",
  "receta", "efecto secundario",
];

const hits = (text: string, needles: string[]) => needles.some((n) => text.includes(n));

const CLASSIFY_TOOL = {
  name: "classify_need",
  description:
    "Record what the person is asking for, in their own terms. Use null when they did not say.",
  strict: true,
  input_schema: {
    type: "object" as const,
    properties: {
      // Strict mode rejects an enum on a ["string","null"] union, so use a sentinel instead of null.
      need: { type: "string", enum: [...CATEGORIES, "unclear"], description: "The single closest category, or \"unclear\" if you cannot tell." },
      city: { type: ["string", "null"], description: "An Orange County city they named, exactly as they said it. Null if they did not name one." },
      constraints: { type: "array", items: { type: "string", enum: CONSTRAINTS }, description: "Only practical constraints they actually stated." },
      route: { type: "string", enum: ["normal", "danger", "clinical", "unknown"], description: "danger: they say they are unsafe or someone may harm them. clinical: a medical, legal or immigration question. unknown: you cannot tell what they need." },
      followUpQuestion: { type: ["string", "null"], description: "At most one short, plain-language question that would narrow the search. Null if nothing is needed." },
    },
    required: ["need", "city", "constraints", "route", "followUpQuestion"],
    additionalProperties: false,
  },
};

const SYSTEM = [
  "You help someone leaving recuperative care in Orange County describe what they need.",
  "You do not know of any services and must never name, invent or suggest one.",
  "Your only job is to classify the need, note stated constraints, and spot safety situations.",
  "Be warm and plain. Never moralize, never diagnose, never give medical, legal or immigration advice.",
  "If the person says they are unsafe or that someone may harm them, set route to danger.",
  "If they ask a medical, legal or immigration question, set route to clinical.",
  "Ask at most one short question, and only when it would genuinely narrow the search.",
  "Answer in the language the person wrote in.",
].join(" ");

async function intake(req: Request) {
  const body = await req.json().catch(() => null);
  const message: unknown = body?.message;
  if (typeof message !== "string" || message.trim() === "") {
    return json({ error: "message is required" }, 400);
  }

  const apiKey = Deno.env.get("ANTHROPIC_API_KEY");
  // No key configured: tell the client to fall back to picking a category. Not an error state.
  if (!apiKey) {
    console.error("intake: ANTHROPIC_API_KEY is not set");
    return json({ error: "intake_unconfigured" }, 503);
  }

  const text = message.slice(0, MAX_MESSAGE_CHARS);
  const language = body?.language === "es" ? "es" : "en";
  const context = body?.context ?? {};

  let result = { need: null, city: null, constraints: [], route: "unknown", followUpQuestion: null } as {
    need: string | null; city: string | null; constraints: string[]; route: string; followUpQuestion: string | null;
  };

  try {
    const anthropic = new Anthropic({
      apiKey,
      ...(WORKSPACE_ID ? { defaultHeaders: { "anthropic-workspace-id": WORKSPACE_ID } } : {}),
    });
    const response = await anthropic.messages.create({
      model: MODEL,
      max_tokens: 1024,
      system: SYSTEM,
      tools: [CLASSIFY_TOOL],
      tool_choice: { type: "tool", name: "classify_need" },
      messages: [{
        role: "user",
        content: [
          context.need ? `They already chose the category: ${context.need}.` : "",
          context.city ? `They already chose the city: ${context.city}.` : "",
          `They wrote (language ${language}): ${text}`,
        ].filter(Boolean).join("\n"),
      }],
    });
    const call = response.content.find((b) => b.type === "tool_use");
    if (call && call.type === "tool_use") result = { ...result, ...(call.input as typeof result) };
  } catch (e) {
    // Claude unreachable or invalid output: the client falls back to category selection.
    // Log the cause - a silent catch here is why a key problem looked like a code problem.
    console.error("intake: upstream call failed:", (e as Error)?.message ?? String(e));
    return json({ error: "intake_unavailable" }, 503);
  }

  // Validate everything the model returned; drop anything not in our closed sets.
  const lower = text.toLowerCase();
  // "unclear" is the sentinel for "model could not tell"; everything outside the closed set is dropped.
  const need = typeof result.need === "string" && CATEGORIES.includes(result.need) ? result.need : null;
  const constraints = Array.isArray(result.constraints)
    ? [...new Set(result.constraints.filter((c) => CONSTRAINTS.includes(c)))]
    : [];

  let route = ["normal", "danger", "clinical", "unknown"].includes(result.route) ? result.route : "unknown";
  if (hits(lower, CLINICAL)) route = "clinical";
  if (hits(lower, DANGER)) route = "danger"; // danger outranks everything
  if (route === "normal" && !need) route = "unknown";

  return json({
    need: route === "normal" ? need : null,
    city: typeof result.city === "string" && result.city.length <= 60 ? result.city : null,
    constraints,
    followUpQuestion:
      route === "normal" && typeof result.followUpQuestion === "string" && result.followUpQuestion.length <= 160
        ? result.followUpQuestion
        : null,
    route,
    needsHumanHelp: route === "danger" || route === "clinical",
  });
}

// ---------------------------------------------------------------- router

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return preflight();
  const path = new URL(req.url).pathname.replace(/\/+$/, "");

  if (path.endsWith("/bundle") && req.method === "GET") return bundle();
  if (path.endsWith("/intake") && req.method === "POST") return intake(req);
  if (path.endsWith("/health")) {
    return json({
      ok: true,
      model: MODEL,
      intakeConfigured: Boolean(Deno.env.get("ANTHROPIC_API_KEY")),
      workspaceHeader: Boolean(WORKSPACE_ID),
    });
  }

  return json({ error: "not found" }, 404);
});
