-- Bridge (Claude Impact Lab OC, Challenge 2)
-- Resource directory seeded from the event's oc-resources.seed.json, plus
-- human-reviewed AI copy and staff-reported data problems.

create table public.resources (
  id                text primary key,
  name              text not null,
  organization      text,
  category          text not null,
  tags              text[] not null default '{}',
  description       text,
  services          text[] not null default '{}',
  eligibility       text,
  population        text[] not null default '{}',
  how_to_access     text,
  referral_required boolean,
  phone             text,
  address           text,
  city              text,
  hours             text,
  languages         text[] not null default '{}',
  cost              text,
  website           text,
  source_url        text,
  source_quote      text,
  verified_date     date,
  confidence        text check (confidence in ('high','medium','low')),
  notes             text
);

comment on table public.resources is
  'Community resources, verbatim from the event seed file. Never invent a field: null means the source did not say.';
comment on column public.resources.referral_required is
  'Nullable on purpose: null means unknown, which is not the same as false.';

-- AI-written client-facing copy. A row is only shippable once a human has reviewed it.
create table public.explanations (
  resource_id text not null references public.resources(id) on delete cascade,
  need        text not null,
  language    text not null check (language in ('en','es')),
  kind        text not null check (kind in ('fit','eligibility_plain')),
  text        text not null,
  model       text not null,
  reviewed_by text,
  reviewed_at timestamptz,
  created_at  timestamptz not null default now(),
  primary key (resource_id, need, language, kind)
);

comment on table public.explanations is
  'Model-authored copy about a resource. Only rows with reviewed_at set are readable by clients.';

-- Staff-reported problems with a record (dead link, wrong number, closed).
create table public.flags (
  id          bigint generated always as identity primary key,
  resource_id text not null references public.resources(id) on delete cascade,
  kind        text not null check (kind in ('dead_link','wrong_phone','closed','other')),
  note        text,
  created_at  timestamptz not null default now()
);

comment on table public.flags is
  'Written only by the server (service role). No client-facing writes, no client input stored.';

create index flags_resource_id_idx on public.flags (resource_id);

-- Row level security: read-only public directory, no public writes anywhere.
alter table public.resources    enable row level security;
alter table public.explanations enable row level security;
alter table public.flags        enable row level security;

grant select on public.resources    to anon, authenticated;
grant select on public.explanations to anon, authenticated;
-- flags: no grants. Server-side (service role) only.

create policy resources_public_read on public.resources
  for select to anon, authenticated using (true);

-- Unreviewed model copy is invisible to clients by policy, not by convention.
create policy explanations_reviewed_read on public.explanations
  for select to anon, authenticated using (reviewed_at is not null);
