# Quest Catalog

Last updated: 2026-06-25

The runtime quest source is `app/src/main/java/com/ayoshy/badventurers/game/SeedGame.kt`.

The spreadsheet-friendly mirror is `../data/quests.csv`. Update it when quest tuning changes until the project has a data import pipeline.

## Current State

- 12 quests are implemented.
- Palier 1: Local Disasters, 8 quests.
- Palier 2: Licensed Trouble, quests 9 to 12.
- Every implemented quest must have UI localization coverage in `BadventurersUiText.kt` and test coverage through `BadventurersUiLocalizationTest`.

## New Quest Checklist

- Add runtime quest data in `SeedGame.kt`.
- Add or update quest-specific expedition plan data in `ExpeditionPlans.kt` when needed.
- Add EN/FR title and summary string resources.
- Add UI localization mapping coverage.
- Add unlock/balance tests.
- Add or select temporary banner art.
- Add at least one passive incident hook if the quest opens a new region/theme.
- Update `../data/quests.csv` and `../data/expedition-plans.csv`.

## Archived Quest Drafts

Old quest-agent drafts were moved to `../archive/quest-agent-specs/quest-agent-specs/`. Treat them as flavor/reference only. Their tuning may not match code.
