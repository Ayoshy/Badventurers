# Hero Animation Spec

Last updated: 2026-06-27

This spec defines the minimum accepted animation pack for every new hero, hero skin, or major hero artwork replacement. It keeps Live-Lite readable while preserving the polished 16-bit-inspired Badventurers art direction.

This is the production rule behind the `Animation Pack` column in `docs/content/hero-item-catalog.md` and `animation_pack_status` in `docs/data/heroes.csv`. Only `accepted` counts as production per-hero animation coverage. `candidate_bv048` is review/runtime-test art, and `prototype_rejected_bv048` is retained plumbing art that must not count as final coverage. Placeholder `pose.*` assets from `docs/design/live-lite-assets.md` may keep the prototype visible, but they do not count as accepted per-hero animation coverage.

## Required State Set

Each accepted hero animation pack must cover these six semantic states. The canonical file key should use the first name unless the alternate better describes the motion.

| State | Use | Frame guidance | Playback |
| --- | --- | --- | --- |
| `idle` | Roster, prep, paused Live-Lite beats | 4 frames | Seamless loop with subtle breathing, fidgeting, or prop motion. |
| `walk` | Route progress and travel beats | 6 frames | Seamless side-view loop with stable feet and readable direction. |
| `fight` | Hazards, effort, and intervention beats | 4-6 frames | Short impact loop or one-shot that returns cleanly to idle. |
| `hurt` / `dead` | Bad outcomes, wipeouts, or downed moments | 2-4 frames, or 1 held dead pose after a settle frame | One-shot, then hold the clearest final pose. |
| `celebrate` | Success and great-success beats | 4-6 frames | Loop or ping-pong with an obvious victory/readiness gesture. |
| `loot` / `interact` | Rewards, searching, paperwork, facility or object beats | 4-6 frames | Loop or one-shot that can hold on the clearest interaction frame. |

A pack may include both `hurt` and `dead`, or both `loot` and `interact`, when the hero needs that distinction. The minimum acceptance bar is one readable state from each slash pair.

## Naming And Keys

- Hero IDs must match the stable lower_snake_case IDs from `HeroCatalog`, `docs/content/hero-item-catalog.md`, and `docs/data/heroes.csv`. Use `brugg`, not `Brugg` or display-name text.
- State names are lower_snake_case and must stay stable: `idle`, `walk`, `fight`, `hurt`, `dead`, `celebrate`, `loot`, `interact`.
- Runtime export names should be `hero_anim_<hero_id>_<state>.png` or `hero_anim_<hero_id>_<state>.webp`.
- Accepted runtime exports belong under `app/src/main/res-heroes/drawable-nodpi/` unless the animation manifest later defines a stricter resource folder.
- Source or review art, when committed, should live under `docs/Extra Art/hero-animation/<hero_id>/` with the same hero/state naming. Do not bury accepted production inputs in screenshots or chat-only artifacts.
- Do not bake labels, UI frames, rarity borders, backgrounds, shadows, or Live-Lite lane elements into hero animation files.

## Frame And Sheet Format

- Preferred cell size is `96 x 96 px`, matching the current Live-Lite hero pose contract. Use `128 x 128 px` only for heroes whose silhouette genuinely cannot read at 96 px, and keep every state for that hero at the same cell size.
- Export each state as a horizontal strip: equal-size frames, transparent background, left-to-right playback order.
- Keep the hero facing right by default for Live-Lite travel. Mirroring belongs in runtime rendering, not in separate left-facing art unless the manifest explicitly asks for it.
- Keep a stable foot baseline and center of mass across frames. Idle-to-walk-to-fight swaps should not appear to teleport or resize the hero.
- Leave safe padding for weapons, hats, effects, and bounce frames. Avoid cropping at the top edge or weapon tip.
- Author and export at integer pixel scale only. No non-integer resizing, blurred interpolation, or mixed pixel densities inside the same pack.

## Phone Readability Bar

Every accepted pack must still read on a portrait phone surface at `64 x 64 px` and remain identifiable at `48 x 48 px`.

Acceptance checks:

- The hero silhouette, class prop, and personality gag are visible before reading the name.
- Motion is clear from the body shape, not from tiny particles or text.
- Props are chunky enough to survive downscaling: oversized weapons, ledgers, tools, bottles, hats, or suspicious paperwork should remain recognizable.
- The palette fits Badventurers: polished 16-bit-inspired pixel art, warm guild compatibility, moss/brass/parchment friendliness, and varied accents. Avoid muddy scaling, flat placeholder colors, one-note beige/brown, or dominant purple-blue treatment.
- Effects are modest. Sparkles, dust, impact pops, and bounce can support the state, but the base pose must work without them.
- Comedy comes from posture, bad equipment, overconfidence, paperwork, or awkward competence. Do not rely on readable text inside the art.

## Live-Lite Mapping

Live-Lite should use hero states this way:

- `idle`: staging, waiting, prep pauses, and fallback from one-shots.
- `walk`: route progress between beats.
- `fight`: hazards, effort checks, and active interventions.
- `hurt` / `dead`: partial failure, wipeout, or ridiculous failure moments.
- `celebrate`: success, great success, level-up, or safe return moments.
- `loot` / `interact`: reward recovery, searching, paperwork, facility, and object beats.

If a hero has no accepted pack, Live-Lite may fall back to portraits or shared placeholders. Once a hero is marked accepted, all required semantic states must exist so the renderer does not need hero-specific gameplay exceptions.

## Review Evidence

Before marking a hero pack accepted, capture lightweight evidence with the asset batch:

- A contact sheet showing every state at native scale with frame counts visible in the filename or notes.
- A phone-size preview proving each state reads at `64 x 64 px` and `48 x 48 px`.
- The final export path for every accepted state.
- The source/review path for the original production sheet or batch artifact.
- A short note listing any accepted aliases, such as using `dead` instead of `hurt` or `interact` instead of `loot`.
- Emulator or rendered Live-Lite evidence once runtime wiring exists. Until then, contact sheets and phone-size previews are enough for art acceptance.

Update `docs/content/hero-item-catalog.md` and `docs/data/heroes.csv` when a hero moves between missing, planned, candidate, prototype/rejected, and accepted animation coverage.