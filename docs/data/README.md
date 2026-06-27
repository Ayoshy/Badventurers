# Spreadsheet Data

Last updated: 2026-06-27

These CSV files are spreadsheet-friendly mirrors of current runtime content. They are meant for sorting, filtering, and balance review in Excel or another spreadsheet tool.

## Files

| File | Mirrors | Runtime source |
| --- | --- | --- |
| `heroes.csv` | Hero catalog, stats, rarity, trait, special, animation coverage | `HeroGacha.kt`, `HeroSpecialCatalog.kt` |
| `items.csv` | Loot definitions | `Loot.kt` |
| `quests.csv` | Quest tuning, tags, unlocks | `SeedGame.kt` |
| `expedition-plans.csv` | Generic and quest-specific contract clauses | `ExpeditionPlans.kt` |

## Excel Rule

CSV is canonical for repo review because it diffs cleanly. You can open these files in Excel, but save changes back to CSV. A generated `.xlsx` workbook is fine as a temporary review artifact, not as the only source of truth.

## Update Rule

When runtime content changes in Kotlin, update the matching CSV in the same change. If the game later becomes data-driven, this folder can become the import source instead of a mirror.
