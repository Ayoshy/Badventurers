# Badventurers Agent Context

Last updated: 2026-06-24.

This file is the first stop for future Codex work on this repo. Read it before doing a broad scan. Then inspect only the specific docs/code touched by the user's request.

## Critical Windows ACL Editing Rule

This workspace repeatedly hits Windows sandbox/ACL failures when Codex uses `apply_patch`, `git diff`, or normal file reads on otherwise valid repo files. Typical errors mention `windows sandbox`, `helper_unknown_error`, `apply deny-read ACLs`, or `orchestrator_helper_exit_nonzero`.

- Do not spend repeated attempts on the same failing operation. One normal attempt is enough.
- If `apply_patch` fails with one of those ACL errors, switch immediately to a narrow guarded PowerShell edit:
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
- Prefer this read order: `AGENTS.md`, `docs/04-kanban.md`, `docs/12-gameplay-screen-loop.md` if present, then the targeted code files.
- Do not claim access to past chat history unless it is saved in this repo or exposed through an installed connector. No local `.codex` or `.agents` memory folder exists in this workspace.
- GitHub source of truth is `Ayoshy/Badventurers`, but the GitHub connector/CLI was unavailable from the agent environment during this context pass. Use repo docs and local Git history unless GitHub access is explicitly restored.
- Keep progress updates concise. Avoid generic "I will read the project first" preambles when this file already answers the orientation question.

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
- Compose UI lives mostly in `app/src/main/java/com/ayoshy/badventurers/ui/BadventurersApp.kt` for the current prototype.
- Local persistence is snapshot-based. Current WIP uses `PlaySessionSnapshot` plus `storage/PlaySessionStore.kt` with SharedPreferences JSON.
- Visible UI strings should use Android resources in `values/strings.xml` and `values-fr/strings.xml`. Generated journal/loot text is still English/hardcoded and needs localization/template work.

## Current App State

Implemented in the Android prototype:

- `MainActivity` loads an initial session from `PlaySessionStore` and passes `onSessionChanged` into `BadventurersApp`.
- `BadventurersApp` has five bottom tabs: Guild, Quests, Heroes, Loot, Upgrades.
- Guild tab supports idle, running, and result-ready states.
- Quests tab starts `SeedGame.firstQuest`.
- Running expeditions tick on a coroutine and can be force-finished in debug builds.
- Collecting results grants gold, generated loot items, and generated journal entries.
- Loot tab shows the latest generated item with sliced icon assets.
- Heroes tab shows the current session hero roster and party power.
- Upgrades tab can buy Notice Board upgrades affecting quest gold.
- Batch 01 art is integrated as Android drawable resources.

Current domain systems:

- `Models.kt`: hero class, hero rarity, traits, quest risk, outcomes, stats, hero, quest, reward, result.
- `HeroGacha.kt`: hero catalog, starter heroes, rarity pools, deterministic weighted summons.
- `ExpeditionEngine.kt`: party power, risk penalty, outcome bands, rewards.
- `Loot.kt`: explicit loot catalog, rarities, slots, icons, deterministic generator.
- `Journal.kt`: short generated English journal lines.
- `PlaySessionState.kt`: resources, heroes, loot, journal, active expedition, progress, start/tick/finish/collect/upgrade.
- `PlaySessionSnapshot.kt`: snapshot version 2 with heroes, loot items, journal entries, and expedition snapshots.
- `PlaySessionStore.kt`: SharedPreferences JSON load/save wrapper; currently untracked at the time this file was created.

## Screen Map

Current Compose tabs/screens:

- Guild Home: current hub, resources, expedition state, recent journal, recommended upgrade.
- Quests: current quest choice surface, still only one seeded quest.
- Heroes: current roster readout, no detail/recruit interactions yet.
- Loot: current latest-item view, not a real inventory list yet.
- Upgrades: current Notice Board purchase plus placeholder facility rows.

Current states inside Guild:

- Expedition Active: represented as Guild running state.
- Result Ready: represented as Guild result state.

Future MVP screens/states from `docs/08-ux-flow.md` and `docs/12-gameplay-screen-loop.md`:

- First launch, language select, guild naming, tutorial first quest.
- Expedition Prep with party selection and success estimate.
- Offline Summary.
- Dedicated Quest Result and Reward Choice.
- Item Detail and Equip Item.
- Hero Detail.
- Deeper Facilities/Upgrades.
- Settings.

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

- `docs/09-art-direction.md`
- `docs/art/batch-01/README.md`
- `docs/mockups/mobile-flow-art.html`
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

In-progress working tree observed while creating this file:

- Hero rarity and gacha foundation in `Models.kt`, `HeroGacha.kt`, `SeedGame.kt`, `BadventurersApp.kt`, and `HeroGachaTest.kt`.
- Snapshot/persistence expansion in `PlaySessionState.kt`, `PlaySessionSnapshot.kt`, `PlaySessionSnapshotTest.kt`, and untracked `storage/PlaySessionStore.kt`.
- Updated kanban items for gacha and persistence.
- Untracked gameplay screen-loop doc and SVG: `docs/12-gameplay-screen-loop.md`, `docs/diagrams/gameplay-screen-loop.svg`.

Treat those as user/agent work in progress. Inspect before editing; do not revert.

## Next Build Order

Follow `docs/04-kanban.md`, adjusted for the persistence WIP:

1. Finish and verify local persistence: snapshot v2 tests, store behavior, app save/load, active expedition/offline handling.
2. Add hero gacha recruitment UI: costs, rarity reveal, result screen, duplicate handling.
3. Persist recruited roster and duplicate compensation/merge behavior.
4. Replace latest-item/raw loot display with a small inventory list.
5. Localize generated journal and loot display through stable content keys/templates.
6. Slice/normalize Batch 01 sheets into Android-ready assets.
7. Add repeatable emulator smoke-test checklist.
8. Create GitHub issues/project once connector or CLI access works.

## Testing And Commands

From repo root:

```powershell
.\gradlew test
.\gradlew assembleDebug
.\gradlew installDebug
.\tools\capture-emulator.ps1
```

Testing expectations:

- Formula/domain changes need unit tests.
- Persistence changes need snapshot/store tests and an emulator smoke check if possible.
- UI-visible strings need English and French resources unless explicitly generated content is being staged for later localization.
- After code changes, run at least `.\gradlew test`; run `assembleDebug` for Android/resource/UI changes when feasible.

Current snapshot test intent:

- `PlaySessionSnapshotTest` expects snapshot v2 to round-trip heroes, loot, journal entries, running expeditions, and ready results, with a starter-hero fallback for unknown saved rosters.

## Editing Notes

- Follow the Critical Windows ACL Editing Rule near the top of this file. The short version: one normal patch/read attempt, then guarded PowerShell or elevated read fallback for ACL failures.
- Do not use broad rewrites, formatting churn, or cleanup unrelated to the request.
- Keep `main` releasable. Use small branches/PRs once GitHub tooling is available.
- Update docs when decisions or screen scope change.
