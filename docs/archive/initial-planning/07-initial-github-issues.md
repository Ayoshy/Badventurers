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

## MVP Gameplay Screen Issues

These tickets break down `docs/12-gameplay-screen-loop.md` into implementation-ready units for the next GitHub issue pass.

## 11. [P0] Add dedicated Expedition Prep screen

Goal: create a prep step between Quest selection and Expedition Active.

Acceptance criteria:

- [ ] Quests screen opens a dedicated prep screen instead of launching immediately.
- [ ] Prep shows the selected quest art, title, duration, risk, and reward preview.
- [ ] Prep shows current party slots and per-hero power.
- [ ] Prep shows party power, target power, and estimated success chance.
- [ ] Launching from prep starts the expedition and returns to Guild Home.

Notes: local prototype implementation exists; keep future party editing compatible with this screen.

## 12. [P0] Add dedicated Quest Result and Reward Choice flow

Goal: move result payoff out of Guild Home into a focused return flow.

Acceptance criteria:

- [ ] Result-ready expeditions open a dedicated Quest Result screen.
- [ ] Result screen shows outcome, score tone, gold, loot rolls, and journal incident.
- [ ] Reward Choice lets the player collect, inspect loot, or continue to the next action.
- [ ] Normal collect flow remains available without optional ads.
- [ ] Result and reward strings are localized in EN/FR.

Notes: preserve the current Guild result state as a fallback route.

## 13. [P0] Add Offline Summary return flow

Goal: make returning after absence feel intentional and funny.

Acceptance criteria:

- [ ] App detects when an active expedition completed while closed.
- [ ] Return flow shows elapsed time, quest result, and rewards earned offline.
- [ ] Summary includes one short localized journal-style line.
- [ ] Player can collect and continue to Guild Home in one tap.
- [ ] Unit tests cover timestamp completion and no-double-collect behavior.

Notes: build on the snapshot persistence already in place.

## 14. [P1] Add Item Detail with equip, sell, and keep actions

Goal: make individual loot decisions clear after rewards and from inventory.

Acceptance criteria:

- [ ] Tapping an item opens a detail view with icon, rarity, slot, bonus, and flavor.
- [ ] Detail suggests the best hero target based on slot and power gain.
- [ ] Player can equip to the selected/suggested hero.
- [ ] Player can keep the item without changing inventory state.
- [ ] Sell action is stubbed or implemented with clear gold value rules.

Notes: current inventory/equip basics can be reused.

## 15. [P1] Add Hero Detail screen

Goal: give each hero a readable identity and progression surface.

Acceptance criteria:

- [ ] Tapping a hero opens a detail screen with portrait, rarity, class, trait, stats, and gear.
- [ ] Detail shows base power, gear bonus, and total contribution.
- [ ] Equipment actions remain reachable from the detail screen.
- [ ] Recruitment result can deep-link to the recruited hero detail.
- [ ] Layout remains readable on portrait phone sizes.

Notes: this can replace the dense roster rows once stable.

## 16. [P1] Document monetization guardrails before fake rewarded ads

Goal: lock the ethical monetization rules before prototyping ad prompts.

Acceptance criteria:

- [ ] Rewarded ads are optional and never required for core progression.
- [ ] Random paid rewards and pay-to-win escalation are explicitly avoided.
- [ ] No-ads purchase, cosmetics, and starter-pack boundaries are documented.
- [ ] Fake rewarded ad placements are listed with normal non-ad alternatives.
- [ ] The docs are linked from the Kanban and future ad issue.

Notes: this should land before any rewarded prompt UI.

## 17. [P1] Add fake Rewarded Ad Prompt flow

Goal: prototype optional reward UX without integrating AdMob.

Acceptance criteria:

- [ ] Fake ad service grants rewards instantly in debug/prototype builds.
- [ ] Reward Choice can offer a clearly optional double-reward action.
- [ ] Timer speed-up or instant-finish remains gated to debug/prototype rules.
- [ ] UI copy makes the normal collect path equally visible.
- [ ] Code is shaped so real AdMob can replace the fake service later.

Notes: depends on monetization guardrails.

## 18. [P2] Add Settings screen with language and audio hooks

Goal: add a support screen for player preferences and future platform needs.

Acceptance criteria:

- [ ] Settings is reachable from Guild Home or bottom navigation.
- [ ] Screen shows language preference entry point and audio toggles.
- [ ] Placeholder toggles persist locally if they affect current behavior.
- [ ] Legal/support placeholders are present but not overbuilt.
- [ ] EN/FR strings exist for all visible labels.

Notes: language switching can remain a platform-level placeholder until the app needs runtime locale switching.
