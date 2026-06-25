# Quest Catalog

Last updated: 2026-06-25

The runtime quest source is `app/src/main/java/com/ayoshy/badventurers/game/SeedGame.kt`.

The spreadsheet-friendly mirror is `../data/quests.csv`. Update it when quest tuning changes until the project has a data import pipeline.

## Source Map

Keep new quest work aligned across these files:

| Need | Source |
| --- | --- |
| Runtime quest fields | `app/src/main/java/com/ayoshy/badventurers/game/SeedGame.kt` |
| Quest-specific contract clauses | `app/src/main/java/com/ayoshy/badventurers/game/ExpeditionPlans.kt` |
| Hero spotlight mechanics | `app/src/main/java/com/ayoshy/badventurers/game/HeroSpecialCatalog.kt` |
| Generated journal hooks | `app/src/main/java/com/ayoshy/badventurers/game/Journal.kt` |
| Passive return-session hooks | `app/src/main/java/com/ayoshy/badventurers/game/PassiveIncidents.kt` |
| UI localization mapping | `app/src/main/java/com/ayoshy/badventurers/ui/BadventurersUiText.kt` |
| Quest banner mapping | `app/src/main/java/com/ayoshy/badventurers/ui/BadventurersUiArt.kt` |
| Sortable data mirror | `../data/quests.csv`, `../data/expedition-plans.csv` |

## Current State

- 16 quests are implemented.
- Palier 1: Local Disasters, 8 quests.
- Palier 2: Licensed Trouble, quests 9 to 12.
- Palier 3: Regional Liability, quests 13 to 16.
- Every implemented quest must have UI localization coverage in `BadventurersUiText.kt` and test coverage through `BadventurersUiLocalizationTest`.
- Quests 1 to 8 have explicit banner mappings in `BadventurersUiArt.kt`; quests 9 to 16 currently use the fallback banner with temporary art briefs tracked in `BadventurersUiArt.kt` and `../data/quests.csv`.

## New Quest Checklist

- Add runtime quest data in `SeedGame.kt`.
- Fill every required runtime field: `id`, `title`, `summary`, `durationSeconds`, `difficulty`, `risk`, `baseGold`, `pityGold`, `partySlots`, `tags`, `recommendedHeroIds`, and `unlockRequirement`.
- Record the reward focus in `../data/quests.csv`: repeat reward intent, first-clear reward intent, and whether this quest introduces gold, loot quality, ticket progress, reward choice, XP catch-up, or facility pressure.
- Add at least two plan hooks per quest. A hook can be a quest-specific contract clause, a generic plan that becomes especially interesting on that quest, or a deliberately bad plan choice that teaches risk. Quest-specific clauses live in `ExpeditionPlans.kt` and `../data/expedition-plans.csv`.
- Add at least two hero spotlight interactions per quest. Prefer heroes whose specials activate through the quest tags, then list those heroes in `recommendedHeroIds` and `../data/quests.csv`.
- Add one passive incident hook that can appear after first clear or once the quest's region/theme is unlocked. If the runtime still uses generic incidents, record the intended dedicated hook as `TBD` in `../data/quests.csv` instead of leaving the design implicit.
- Add first-clear and repeat reward notes. If first-clear rewards are not implemented yet, state the planned reward in `../data/quests.csv` and link the follow-up ticket.
- Add EN/FR title and summary string resources, localized tag coverage, and `localizedQuestTextIds` coverage.
- Add generated journal lines in `Journal.kt` and `BadventurersUiText.kt` if the line is surfaced through resources.
- Add banner art or a temporary art brief. If the quest uses fallback art, record the fallback and brief status in `../data/quests.csv`.
- Add or update unlock, balance, plan, localization, and art-reference tests/checks.
- Update `../data/quests.csv` and `../data/expedition-plans.csv` in the same change.

## New Quest Template

Use this template before adding the Kotlin object. It is intentionally stricter than the current implementation so Palier 3 and later batches do not ship as only a title plus tuning row.

| Field | Required Content |
| --- | --- |
| Identity | Stable snake_case id, palier/track, quest number, English title, English summary, French title, French summary |
| Runtime tuning | Duration seconds, difficulty, risk tier, base gold, pity gold, party slots, tags, recommended hero ids, unlock conditions |
| Reward plan | Repeat reward focus, first-clear reward, loot tier target, ticket/progression reward if any, failure/pity role |
| Plan hooks | At least two hooks with player tradeoff, expected modifier shape, and whether each is generic or quest-specific |
| Hero spotlights | At least two heroes or hero specials that change real estimate levers for this quest |
| Return-session flavor | Generated quest journal line plus one passive incident hook after first clear or regional unlock |
| Localization | EN/FR title and summary resources, tag label coverage, `localizedQuestTextIds` entry |
| Art | Dedicated `quest_banner_*` resource or a temporary art brief that can be turned into one |
| Tests/checks | Unlock ladder, balance odds, recommended hero ids, plan availability, localization mapping, banner mapping or fallback audit |

## Palier 2 Temporary Banner Briefs

Until dedicated Palier 2 banner files exist, quests 9 to 12 intentionally resolve to `quest_banner_03` with these briefs:

| Quest | Temporary Brief |
| --- | --- |
| `paperwork_toll_of_chaos` | Border checkpoint booth stacked with stamped receipts, chaos toll signs, and nervous adventurers. |
| `licensed_guild_caravan_haunt` | Licensed caravan at dusk with spectral cargo straps, official seals, and triplicate manifests. |
| `notary_night_patrol` | Lantern-lit notary patrol in identical robes, oath scrolls, and suspiciously legal shadows. |
| `inspectorate_cove_banquet` | Coastal banquet table with inspector seals, emergency audit papers, and anxious serving covers. |

## Palier 3 Temporary Banner Briefs

Until dedicated Palier 3 banner files exist, quests 13 to 16 intentionally resolve to `quest_banner_03` with these briefs:

| Quest | Temporary Brief |
| --- | --- |
| `wedding_with_too_many_oaths` | Overbooked ceremonial hall with vow ledgers, witness stamps, and emergency seating charts. |
| `the_sunken_toll_booth` | Half-submerged toll booth with floating coins, soaked ledgers, and a queue line disappearing into swamp water. |
| `the_crowns_missing_receipt` | Royal archive desk with missing receipt notices, crown seal wax, and guards pretending not to panic. |
| `the_tower_built_sideways` | Impossible tower leaning sideways with braced stairs, panic permits, and ancient stones arguing about gravity. |

## CSV Mirror Fields

`../data/quests.csv` mirrors runtime fields and also carries design-control columns for unfinished systems:

- `reward_focus`: short balancing purpose for the quest.
- `first_clear_reward`: the intended one-time payoff. Use `TBD` with a ticket id when the system is not implemented yet.
- `repeat_reward`: the normal repeat payoff shape.
- `plan_hooks`: semicolon-separated plan ids or planned hook names; every new quest should list at least two.
- `hero_spotlights`: semicolon-separated hero ids or special names that should matter on this quest.
- `passive_incident_after_first_clear`: implemented incident id or planned hook.
- `localization_keys`: title, summary, tag, and mapping coverage notes.
- `banner_art`: concrete drawable resource or temporary art brief status.

## Archived Quest Drafts

Old quest-agent drafts were moved to `../archive/quest-agent-specs/quest-agent-specs/`. Treat them as flavor/reference only. Their tuning may not match code.
