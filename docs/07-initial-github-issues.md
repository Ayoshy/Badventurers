# Initial GitHub Issues

Last updated: 2026-06-21

These tickets should be created in GitHub Issues once repository issue access is available to the tooling.

## 1. [P0] Scaffold Android app with Kotlin and Jetpack Compose

Goal: create the initial Android project structure for Badventurers.

Acceptance criteria:

- [ ] Android app module exists.
- [ ] Kotlin and Jetpack Compose are configured.
- [ ] App launches to a placeholder Badventurers screen.
- [ ] Portrait orientation is enforced for the MVP.
- [ ] `./gradlew test` and `./gradlew assembleDebug` run locally.

Notes: see `docs/decisions/0001-stack-android-native-compose.md` and `docs/05-setup-test.md`.

## 2. [P0] Implement core domain models

Goal: model the first version of the game state outside UI code.

Acceptance criteria:

- [ ] Hero model includes class, level, stats, traits, and equipment slots.
- [ ] Quest model includes duration, difficulty, risk, tags, and reward table.
- [ ] Item model includes rarity, slot, stat modifiers, sell value, and localization keys.
- [ ] Game state model can represent a fresh player and an active expedition.
- [ ] Models are covered by focused unit tests where logic exists.

Notes: keep domain logic plain Kotlin so balancing can evolve without Compose dependencies.

## 3. [P0] Build expedition resolution engine

Goal: resolve an idle expedition into rewards, outcome, and journal events.

Acceptance criteria:

- [ ] Party power is calculated from heroes, gear, traits, and guild bonuses.
- [ ] Quest difficulty and risk influence the result.
- [ ] Result bands include great success, success, partial success, failure, and ridiculous failure.
- [ ] Failure still grants a small compensation reward.
- [ ] Formula behavior is covered by unit tests.

Notes: start deterministic enough for tests, then add seeded randomness.

## 4. [P0] Build Guild Home screen

Goal: create the first playable home screen for the guild.

Acceptance criteria:

- [ ] Screen shows guild name, main resources, active expedition state, and recent journal entries.
- [ ] Player can start the first expedition from this screen.
- [ ] Player can collect completed expedition rewards.
- [ ] Layout is readable on common phone sizes in portrait.
- [ ] All visible strings use localization resources.

Notes: prioritize clarity over decoration for the first vertical slice.

## 5. [P0] Implement local save and offline progress

Goal: persist player progress and calculate completed idle work after app close.

Acceptance criteria:

- [ ] Fresh game state is created on first launch.
- [ ] Player resources, heroes, items, and active expeditions are saved locally.
- [ ] Closing and reopening the app preserves state.
- [ ] Completed expeditions are detected from timestamps.
- [ ] Offline progress summary can be shown after return.

Notes: choose DataStore or Room once the first state shape is clear.

## 6. [P1] Add bilingual localization structure

Goal: support English and French from day one.

Acceptance criteria:

- [ ] Android string resources exist for English and French.
- [ ] UI text is not hardcoded in Compose components.
- [ ] Initial item, quest, trait, and journal strings have EN/FR variants.
- [ ] Content keys are stable enough for generated game content.
- [ ] Fallback behavior is documented.

Notes: humor can be adapted per language rather than translated literally.

## 7. [P1] Add loot generation and inventory basics

Goal: generate and display early loot rewards.

Acceptance criteria:

- [ ] MVP rarity tiers exist: Common, Uncommon, Rare, Epic.
- [ ] MVP equipment slots exist: Weapon, Armor, Trinket.
- [ ] Quest reward tables can produce items.
- [ ] Player inventory stores collected loot.
- [ ] Player can equip at least one item on a hero.

Notes: early loot should feel frequent, readable, and funny.

## 8. [P1] Add journal event generator

Goal: generate short comedic logs for expedition outcomes.

Acceptance criteria:

- [ ] Journal templates support variables such as hero, trap, spell, item, and quest.
- [ ] Templates exist for success, partial success, failure, and neutral events.
- [ ] Generated logs are stored in recent history.
- [ ] English and French variants exist for the initial template set.
- [ ] Repetition is reduced with a simple selection strategy.

Notes: logs are a key retention hook, so keep them short and punchy.

## 9. [P1] Add fake rewarded ad reward flow

Goal: prototype rewarded-ad UX before integrating AdMob.

Acceptance criteria:

- [ ] A fake ad service can grant a reward instantly in debug/prototype builds.
- [ ] Player can double loot from a completed expedition through the fake flow.
- [ ] Player can speed up or finish a timer through the fake flow if enabled.
- [ ] The UI clearly separates optional reward prompts from normal progression.
- [ ] Code is structured so real AdMob can replace the fake service later.

Notes: do not integrate real ads until the core loop is worth testing.

## 10. [P1] Create GitHub Project board and labels

Goal: set up the visible Kanban board and project labels on GitHub.

Acceptance criteria:

- [ ] GitHub Project exists for Badventurers.
- [ ] Columns exist: Backlog, Ready, In Progress, Review, Done.
- [ ] Initial issues are added to the board.
- [ ] Labels from `docs/06-outils-workflow.md` are created or adjusted.
- [ ] This issue is moved to Done when the board is usable.

Notes: Codex can create issues once repo access is granted through the GitHub connector or GitHub CLI is installed.

