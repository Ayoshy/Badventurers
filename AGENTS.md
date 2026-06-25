# Badventurers Agent Context

Last updated: 2026-06-25.

This file is the first stop for future Codex work on this repo. Read it before doing a broad scan. Then inspect only the specific docs/code touched by the user's request.

## Critical Windows ACL Editing Rule

This workspace repeatedly hits Windows sandbox/ACL failures when Codex uses `apply_patch`, `git diff`, or normal file reads on otherwise valid repo files. Typical errors mention `windows sandbox`, `helper_unknown_error`, `apply deny-read ACLs`, or `orchestrator_helper_exit_nonzero`.

- For text/code edits in this workspace, skip `apply_patch` and use a narrow guarded PowerShell edit directly. The ACL failure is recurring enough that trying `apply_patch` first usually just wastes time and tokens.
- Use this fallback shape:
  - read with `[System.IO.File]::ReadAllText($p)`;
  - replace exact known text blocks only;
  - check `Contains(...)` or an exact match count before writing;
  - write with `[System.IO.File]::WriteAllText($p, $text, [System.Text.UTF8Encoding]::new($false))`;
  - re-read or `git diff` the edited file afterwards.
- If `git diff`, `Get-Content`, or `rg` fails with the same ACL issue, rerun once with `sandbox_permissions: "require_escalated"` and a narrow justification.
- Keep these fallback edits small. Avoid broad rewrites, formatting churn, generated rewrites, or touching unrelated files.
- Do not narrate every ACL failure at length. State the switch once, then continue.

## How To Start A New Thread

- Run `git status --short` before assuming the repo state. This project often has useful in-progress work in the working tree.
- Prefer this read order: `AGENTS.md`, `docs/work/tasks.json`, then `docs/product/kanban.md` only for product context, then the targeted code files.
- Do not claim access to past chat history unless it is saved in this repo or exposed through an installed connector. No local `.codex` or `.agents` memory folder exists in this workspace.
- GitHub source of truth is `Ayoshy/Badventurers`. Verify live GitHub state when connector/CLI access is available; otherwise rely on repo docs and local Git history.
- Keep progress updates concise. Avoid generic "I will read the project first" preambles when this file already answers the orientation question.

## Active Task Tracking

The daily todo list is no longer `docs/product/kanban.md`. The active source of truth for implementation tasks is `docs/work/tasks.json`, viewed through the local Workboard.

Always make the Workboard available on the fixed local URL before using or updating the task list:

```powershell
node tools/task-board/server.mjs 4173
```

Then use:

```text
http://127.0.0.1:4173
```

Keep the port stable at `127.0.0.1:4173` unless that port is genuinely occupied. Codex may update `docs/work/tasks.json` directly or through the Workboard API. Use `docs/product/kanban.md` as product context and historical backlog only.

## Product Snapshot

Badventurers is an Android-first portrait idle/incremental RPG about managing a fantasy guild full of underqualified adventurers. The player sends parties on automated expeditions, returns for absurd logs and loot, upgrades the guild, recruits heroes, and repeats.

Product pillars:

- Fast idle loop: useful progress in short sessions, with offline rewards later.
- Absurd RPG comedy: bureaucracy, cowardice, bad equipment, accidental competence.
- Loot joy: frequent, readable rewards with funny names and obvious upgrades.
- Ethical monetization: rewarded ads and purchases must stay optional.
- Bilingual by design: English and French are first-class targets; jokes may be adapted, not literally translated.

Core loop to protect:

1. Open Guild.
2. Collect expedition result or offline rewards.
3. Read short journal payoff.
4. Equip loot, improve heroes, or buy a guild upgrade.
5. Start the next expedition.
6. Leave with a visible timer/reason to return.

## Stack And Architecture

- Android native, Kotlin, Jetpack Compose. ADR: `docs/decisions/0001-stack-android-native-compose.md`.
- Portrait-only MVP.
- Domain/gameplay logic belongs in plain Kotlin under `app/src/main/java/com/ayoshy/badventurers/game`.
- Compose UI is split by responsibility under `app/src/main/java/com/ayoshy/badventurers/ui`.
- `BadventurersApp.kt` is the app shell only: theme, top-level remembered state, tab routing, and session update wiring. Keep it below 1,000 lines.
- Screen-level UI belongs in focused files: `GuildScreen.kt`, `QuestsPrepScreens.kt`, `ResultsScreens.kt`, `HeroesScreen.kt`, `LootScreen.kt`, and `UpgradesAchievementsScreens.kt`.
- Shared UI concerns belong in `BadventurersUiArt.kt`, `BadventurersUiText.kt`, `BadventurersUiLogic.kt`, and `BadventurersUiComponents.kt`.
- Local persistence is snapshot-based. `PlaySessionSnapshot` plus `storage/PlaySessionStore.kt` store SharedPreferences JSON; move to DataStore or Room only if save complexity grows.
- SharedPreferences save state is explicitly excluded from Android backup with `android:allowBackup="false"`; keep backup policy explicit if storage changes.
- Visible UI strings should use Android resources in `values/strings.xml` and `values-fr/strings.xml`. Generated journal/loot/passive incident text is still partly English/hardcoded and needs localization/template work.

## Current App State

Implemented in the Android prototype:

- `MainActivity` loads an initial session from `PlaySessionStore` and passes `onSessionChanged` into `BadventurersApp`.
- `BadventurersApp` has five bottom tabs: Guild, Quests, Heroes, Loot, Upgrades, plus internal Offline Summary, Quest Result, Reward Loot, Achievements, Hero Detail, and Expedition Prep states.
- Guild tab supports idle, running, result-ready, offline-return, core crew, passive income, passive incidents, achievements, facilities, and recommendation states.
- Quests tab exposes the 12 current quests, unlock requirements, recommended heroes, party selection, and contract plan selection.
- Running expeditions tick on a coroutine, persist their selected party and plan, can complete offline, and can be force-finished in debug builds.
- Collecting results grants gold, XP, pending loot recovery choices, generated journal entries, achievement progress, deterministic result causes, and ticket rewards where applicable.
- Loot tab supports inventory, item detail, sell/equip flow, and reward recovery capacity breakdown.
- Heroes tab supports roster details, recruitment, recruitment tickets, duplicate reputation, release, equipment, XP progression, and authored level-up recovery rewards.
- Upgrades tab can buy Notice Board, Training Yard, and Bunk Room upgrades; later facilities are cataloged but not implemented.
- Batch 01 and later art batches are integrated as Android drawable resources where available.

Current domain systems:

- `Models.kt`: hero class, hero rarity, traits, quest risk/tags, outcomes, stats, hero, quest, reward, result.
- `HeroGacha.kt`: hero catalog, starter heroes, rarity pools, deterministic weighted summons, duplicate handling.
- `HeroProgression.kt`: XP curve, stat growth, preview data, and authored level-up rewards.
- `HeroSpecialCatalog.kt`: hero special modifiers and shared loot-recovery specialist definitions.
- `ExpeditionPlans.kt`: generic and quest-specific contract clauses with risk/reward modifiers.
- `ExpeditionEngine.kt`: party power, risk penalty, outcome bands, plan/special/facility modifiers, rewards.
- `ResultCauses.kt`: deterministic result cause cards for plans, specials, facilities, and achievement features.
- `Loot.kt`: explicit loot catalog, rarities, slots, icons, deterministic generator, sell economy.
- `GuildFacilities.kt`: implemented and future facility catalog, costs, unlocks, and upgrade state.
- `Achievements.kt`: achievement definitions, rewards, charter milestones, event tracker, feature unlocks.
- `ProgressionAdvisor.kt`: next-action recommendations for reports, loot, upgrades, recruitment, and quests.
- `PassiveIncidents.kt`: deterministic offline passive incident generation for core crew/facility states.
- `RecruitmentTickets.kt`: ticket metadata, inventory normalization, deterministic ticket recruitment, and blank-contract handling.
- `Journal.kt`: short generated English journal lines.
- `PlaySessionState.kt`: resources, heroes, loot, journal, tickets, passive incidents, active expedition, progress, start/tick/finish/collect/upgrade/recruit/achievement state.
- `PlaySessionSnapshot.kt`: snapshot version 13 with heroes, loot, tickets, passive incidents, pending recovery breakdown, journal, achievements, and expedition snapshots.
- `PlaySessionStore.kt`: SharedPreferences JSON load/save wrapper with observable decode failures.

## Screen Map

Current Compose tabs/screens:

- Guild Home: current hub, resources, expedition state, recent journal, core crew, facilities, achievement ledger preview, and progression advice.
- Quests: 12-quest catalogue with unlock gates, quest art, recommended heroes, and prepare flow.
- Expedition Prep: party selection, success estimate, best-fit hero reasons, and contract plan selection.
- Quest Result: result causality, plan-specific copy, level-up reveals, rewards, fake rewarded-ad debug hook, and next action.
- Offline Summary: combined return report with completed expedition, away time, rewards, passive incidents, charter progress, and projected next action.
- Reward Loot: limited recovery choice with base/Bunk Room/veteran/specialist capacity breakdown.
- Heroes: roster, gacha recruitment, recruitment tickets, duplicate reputation, hero detail, equipment picker, release, and level progression.
- Loot: inventory, item detail, sell/equip flow, and generated icon art.
- Upgrades: Notice Board, Training Yard, Bunk Room, and achievement ledger access; later facilities are visible/cataloged as future work.
- Achievements: Trophy Ledger, claim all/single claim, charter milestones, and reward summaries.

Future MVP screens/states from `docs/design/ux-screen-map.md`, `docs/design/gameplay-loop.md`, and `docs/product/kanban.md`:

- First launch, language select, guild naming, tutorial first quest.
- Deeper facilities with active/passive gameplay effects beyond Notice Board, Training Yard, and Bunk Room.
- Settings.
- Better localization/template coverage for generated journal, loot, and passive incident text.
- Beta analytics/crash reporting and store-readiness surfaces.

Future long-term:

- Shop, rewarded ad prompts, seasonal events, prestige/charter bonuses.

## Visual Direction

Use the approved Batch 01 direction:

- Polished 16-bit-inspired 2D pixel art with modern mobile readability.
- Cozy low-budget fantasy guild, warm tavern lighting, moss green and brass identity, parchment panels.
- Humor from posture, props, mismatched gear, suspicious paperwork, and compact copy.
- Avoid a flat text-app feel, direct copyrighted references, muddy scaling, tiny pixel fonts, heavy decorative borders, and one-note brown/beige or purple-blue palettes.
- Batch 01 assets are approved for direction testing, not final production. Sheet slicing/normalization is still part of the asset pipeline.

Important files:

- `docs/design/art-direction.md`
- `docs/README.md`
- `docs/design/ux-screen-map.md`
- `app/src/main/res/drawable-nodpi/*`

## Local History

Local Git commit timeline:

- `76120bd`: project foundation docs, README, issue templates, ADR.
- `85fcafb`: initial GitHub issue backlog.
- `50ef251`: UX flow and low-fidelity mobile mockup.
- `b696acb`: polished pixel art direction.
- `c8b9160`: Batch 01 concept art.
- `1b51ac0`: art-integrated HTML visual prototype.
- `91103c2`: Android visual slice scaffold.
- `0552190`: scaffold made buildable with Gradle wrapper.
- `b23ddef`: playable loop and emulator capture workflow.
- `28a602d`: save snapshot foundation, loot, journal systems.
- `14289af`: debug instant quest action.
- `fac00ad`: generated loot icons in UI.
- `e537669`: loot icon crop mapping fixes.
- `f22b9a2`: explicit loot catalog.

Current working-tree caveat:

- `docs/content/hero-item-catalog.md` and `docs/data/*.csv` are active content references. Update them with hero, item, quest, and plan data changes.

## Current Priorities

Follow the Workboard in `docs/work/tasks.json` first. Keep it mounted at `http://127.0.0.1:4173` when working on tasks. Use `docs/product/kanban.md` only as product context, and verify any context against code before treating it as current. Highest-value priorities now are:

1. Keep `main` releasable: unit tests, debug build, lint, and CI should stay green.
2. Continue behavior-preserving UI decomposition when touching large surfaces; `HeroesScreen.kt` is the next likely split candidate.
3. Continue bilingual cleanup for generated journal, loot, passive incident, and ticket-adjacent copy.
4. Make Scout Table, Armory/Forge, Infirmary, Tavern/Kitchen, and Accountant Office affect real decisions.
5. Add settings, smoke-test checklist, beta analytics/crash reporting, and store assets after loop polish.
6. Create/update GitHub issues/project when connector or CLI access works.

## Quality Bar And Commands

From repo root:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
.\gradlew.bat installDebug
.\tools\capture-emulator.ps1
```

CI parity:

- `.github/workflows/android-ci.yml` runs `test`, `assembleDebug`, and `lintDebug` on Windows.
- If local work changes code, resources, build config, or UI mappings, run the same three verification commands when feasible.

Testing expectations:

- Formula/domain changes need unit tests.
- Passive incident, recruitment ticket, quest/plan, and facility changes need focused regression coverage.
- Persistence changes need snapshot/store tests, decode-failure observability, and an emulator smoke check if possible.
- UI-visible strings need English and French resources unless explicitly generated content is being staged for later localization.
- New quests and expedition plans must update UI localization mapping coverage through `localizedQuestTextIds` and `localizedExpeditionPlanTextIds`.
- Android resource string literals that include `%` must escape it as `%%` unless they are format placeholders.

Current snapshot test intent:

- `PlaySessionSnapshotTest` expects the current snapshot version to round-trip heroes, loot, recruitment tickets, passive incidents, pending recovery breakdown, journal entries, achievements, running expeditions, and ready results, with a starter-hero fallback for unknown saved rosters.

## Editing Notes

- Follow the Critical Windows ACL Editing Rule near the top of this file. The short version: guarded PowerShell edits or elevated read fallback for ACL failures, with narrow exact replacements.
- Do not use broad rewrites, formatting churn, or cleanup unrelated to the request.
- Keep `BadventurersApp.kt` as a small shell. New feature UI should land in focused screen/helper files, not back in the app shell.
- Preserve save compatibility. If snapshot fields change, add legacy decode coverage and keep corrupted-save fallback observable.
- Keep `main` releasable. Use small branches/PRs once GitHub tooling is available.
- Update docs when decisions, quality gates, or screen scope change.
