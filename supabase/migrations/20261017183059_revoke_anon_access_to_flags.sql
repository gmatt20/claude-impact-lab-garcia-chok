-- Supabase default privileges grant anon/authenticated SELECT on new public tables,
-- so flags was readable (RLS returned zero rows, but the table was reachable).
-- Make it unreachable too: defense in depth, not RLS alone.
revoke all on public.flags from anon, authenticated;

-- Same for the identity sequence behind flags.id.
revoke all on all sequences in schema public from anon, authenticated;
