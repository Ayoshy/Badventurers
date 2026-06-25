# Badventurers Docs

Last updated: 2026-06-25

This folder is now split by use. Do not add new numbered top-level docs. Put new information in the smallest active home below, or archive it if it is historical context.

## Start Here

| Need | File |
| --- | --- |
| Current product board | `product/kanban.md` |
| Agent/workflow orientation | `../AGENTS.md` |
| Build, test, and smoke checks | `engineering/setup-test.md` |
| Quality bar and repo workflow | `engineering/workflow.md` |
| Current screen ownership | `design/ux-screen-map.md` |
| Current game loop | `design/gameplay-loop.md` |
| Runtime content catalogs | `content/hero-item-catalog.md`, `content/quest-catalog.md`, `data/*.csv` |

## Active Docs

### Product

- `product/vision.md`: durable one-page game direction.
- `product/requirements.md`: release-facing requirements and constraints.
- `product/kanban.md`: current product board. Verify against code when it looks stale.
- `product/monetization-guardrails.md`: rules for ads, purchases, and ethical monetization.

### Engineering

- `engineering/setup-test.md`: local setup, commands, emulator smoke test.
- `engineering/workflow.md`: repo workflow, documentation rules, quality gate.
- `decisions/0001-stack-android-native-compose.md`: accepted stack ADR.

### Design

- `design/gameplay-loop.md`: current playable loop and return-session design.
- `design/ux-screen-map.md`: current screens and Compose file ownership.
- `design/art-direction.md`: visual direction; runtime assets still live under `app/src/main/res-*`.
- `design/achievements.md`: achievement design intent.
- `design/progression.md`: current/future progression layers in one place.
- `design/hero-promotion.md`: planned promotion/duplicate contract rules.
- `design/long-term-plan.md`: broader product direction.

### Content And Data

- `content/hero-item-catalog.md`: narrative notes and update checklist for heroes/items.
- `content/quest-catalog.md`: quest-content rules and source-of-truth notes.
- `data/*.csv`: spreadsheet-friendly mirrors for heroes, items, quests, and expedition plans.

## Archive

`archive/` contains old planning, prototype, issue-seed, progression, and quest-agent-spec documents. These files are useful context, but they are not active source of truth. If an archived idea becomes current again, copy the relevant part into an active doc instead of linking agents back into the archive.

## Data And Excel Rule

Use CSV for repo-canonical spreadsheet data. CSV files are easy to diff, review, and merge. It is fine to open them in Excel or export a temporary `.xlsx` workbook for balancing, but do not make `.xlsx` the only source of truth.

When runtime content changes in Kotlin, update the matching CSV in the same change until the project has a true data-driven import pipeline.

## Cleanup Rules

- Keep active docs short and current.
- Archive historical documents instead of silently deleting them.
- Avoid duplicate roadmaps. `product/kanban.md` is the active board.
- Avoid duplicate catalogs. Use `content/` for narrative notes and `data/` for sortable tables.
- Fix links when moving docs.
