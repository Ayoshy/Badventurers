# Live-Lite Asset Contract

Last updated: 2026-06-27

BV-011 and BV-012 share this key contract. The game domain emits simple `LiveLiteAssetRef` keys from `LiveLiteExpedition.kt`; the UI maps those keys through `LiveLiteArt.kt`. The current prototype uses code-native paintable placeholders so watch mode is visible before final PNG/WebP production.

## Key Families

- `stage.*`: compact quest-stage background by biome/theme.
- `pose.*`: shared hero action pose column.
- `hazard.*`: route event or hazard card.
- `plan.*`: expedition plan or special contract clause card.
- `cause.*`: result-cause card family.
- `passive.*`: passive income or offline report illustration.
- `intervention.*`: optional active nudge icon.

## Stage Backgrounds

Recommended source size: 768 x 288 px, exported at integer scale. Keep the lower third readable for side-view party markers.

| Key | Use | Brief |
| --- | --- | --- |
| `stage.cave` | Cave or trap-heavy local jobs | Damp tunnel, nervous torch, apology paperwork. |
| `stage.forest` | Wilderness, hunt, exploration | Crooked path, contradictory signposts, moss green shadows. |
| `stage.swamp` | Swamp, poison, rot | Briny platform, cracked basin, salt rings. |
| `stage.crypt` | Undead, curse, debt | Ledger-lined crypt with payment-slot wall. |
| `stage.siege_camp` | Siege or camp jobs | Bread-crate barricade under tired camp light. |
| `stage.ruins` | Magic or ancient jobs | Old ruin with glowing hinges and survey chalk. |
| `stage.city` | Urban, paperwork, bandit, contract | Guild counter, hostile forms, coin sacks. |
| `stage.fortress` | Wall, breach, collapse, obstacle | Locked gate, brace beams, serious latch. |
| `stage.road` | Fallback | Guild road, cart rut, bad map. |

## Hero Pose Sheet

Recommended sheet: 5 columns x hero rows, 96 x 96 px cells. Column order is stable:

1. `pose.idle`
2. `pose.effort`
3. `pose.success`
4. `pose.mistake`
5. `pose.loot`

Rows may be keyed by hero ID at first, then upgraded to class-specific variants when animation production starts.

## Card Icons

Hazards currently emitted by domain helpers:

- `hazard.trap`
- `hazard.paperwork`
- `hazard.curse`
- `hazard.bandit`
- `hazard.undead`
- `hazard.magic`
- `hazard.siege`
- `hazard.heist`
- `hazard.obstacle`
- `hazard.route`
- `hazard.swamp`
- `hazard.guard`

Plan cards use `plan.<ExpeditionPlanCatalog id>`, for example `plan.rush_the_job`, `plan.safety_first`, and `plan.loot_priority`.

Result-cause cards currently use:

- `cause.plan`
- `cause.hero_special`
- `cause.facility`
- `cause.achievement`

Passive report cards currently use:

- `passive.guild_income`
- `passive.core_crew`
- `passive.facility`
- `passive.quest_region`

Intervention icons currently use:

- `intervention.spend_supply`
- `intervention.safer_route`
- `intervention.push_for_loot`
- `intervention.encourage_hero`
- `intervention.hero_trick`

## Production Notes

- Keep silhouettes chunky; the screen is a portrait phone surface, not a map.
- Keep palettes varied by biome so Live-Lite does not become another brown panel.
- Future final art should preserve the same keys and swap the renderer from `LiveLiteArt.kt` placeholders to drawable resources.
- No asset key should become player-facing copy; labels stay in Android string resources.