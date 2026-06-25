# Progression

Last updated: 2026-06-25

This page is the compact active progression map. Older exploration notes live in `../archive/progression-planning/`.

## Implemented Layers

| Layer | Current source | Notes |
| --- | --- | --- |
| Quest ladder | `SeedGame.kt` | 12 quests are live: Palier 1 plus Palier 2 Licensed Trouble. |
| Hero XP and levels | `HeroProgression.kt`, `PlaySessionState.kt` | Expedition participants gain XP; level-ups increase stats and can unlock recovery bonuses. |
| Loot | `Loot.kt` | Generated items roll rarity, slot, stats, and sell value. |
| Facilities | `GuildFacilities.kt`, `PlaySessionState.kt` | Notice Board, Training Yard, and Bunk Room have real effects. Later facilities are cataloged but not fully implemented. |
| Achievements | `Achievements.kt` | Badges, claim state, rewards, charter milestones, and unlock features exist. |
| Recruitment tickets | `RecruitmentTickets.kt` | Ticket inventory, rarity floors, specialist pools, veteran tickets, and blank-contract placeholder exist. |
| Passive guild economy | `PlaySessionState.kt`, `PassiveIncidents.kt` | Core Crew generates passive gold; incidents add flavor and small rewards. |

## Planned Layers

- Hero promotion rank storage and hero-specific contracts.
- Blank contract spending.
- Functional Scout Table, Armory/Forge, Infirmary, Tavern/Kitchen, and Accountant Office.
- Palier 3 quests and Epic reward gates.
- Optional long-term charter/prestige layer only after the 16-quest ladder feels good.

## Guardrails

- Expedition XP remains the main hero-power path.
- Paid or ad-adjacent rewards must not become the best route to power.
- New progression currencies need a clear sink and a visible reason to care.
- Snapshot changes need compatibility tests.
