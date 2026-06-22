# Kanban

Last updated: 2026-06-22

This file is the repo-local board. GitHub Issues should mirror these items so the work can be tracked outside the codebase too.

## Columns

- Backlog: idea is useful, not ready.
- Ready: clear enough to implement.
- In Progress: actively being worked.
- Review: implemented, needs validation.
- Done: merged or accepted.

## Planning Notes

- Gameplay screens and future screen dependencies are mapped in [Gameplay screen loop](12-gameplay-screen-loop.md).
- Competitor patterns and monetization warnings are captured in [Competitive research](13-competitive-research.md).
- Concrete monetization guardrails live in [Monetization guardrails](14-monetization-guardrails.md).
- Current core screens are Guild, Quests, Heroes, Loot, and Upgrades.
- Future MVP screens to preserve in planning: Expedition Prep, Offline Summary, Quest Result, Reward Choice, Item Detail, Equip Item, Hero Detail, Settings, and a fake Rewarded Ad Prompt.
- Longer-term future screens: Shop, Events / seasonal quests, and Prestige / charter bonuses.

## Now

- [x] Create repository.
- [x] Define product direction.
- [x] Add project documentation.
- [x] Validate low-fidelity UX flow.
- [x] Validate Batch 01 art direction.
- [x] Add art-integrated HTML visual prototype.
- [x] Scaffold Android project.
- [x] Verify local Android test/build.
- [x] Add first in-memory playable loop.
- [x] Add ADB emulator capture script.
- [x] Install current build on emulator.
- [x] Capture current emulator screen through ADB.
- [x] Add save snapshot foundation for `PlaySessionState`.
- [x] Add basic loot item generation.
- [x] Add item rarity tiers to generated loot.
- [x] Add hero rarity and gacha generator foundation.
- [x] Persist local session state for resources, roster, loot, journal, and active expedition.
- [x] Replace Loot screen with a selectable inventory list.
- [x] Remove unused top-bar placeholder actions.
- [x] Add journal event generation from expedition results.
- [x] Smoke install and capture the integrated build on emulator.
- [x] Commit and push the save/loot/journal slice.
- [x] Add hero gacha recruitment UI with rarity tiers, pull costs, and result reveal.
- [x] Add duplicate handling for hero gacha pulls.
- [x] Localize generated journal/loot display text through resource templates.
- [x] Slice Batch 01 sheets into Android-ready hero, quest, loot, and resource assets.
- [x] Map current and future screens in the gameplay screen-loop diagram.
- [x] Add hero roster interactions and equipment UI.
- [x] Limit Expedition Prep to quest party slots and persist selected expedition party.
- [x] Add pending reward loot choices with real Keep/Sell outcomes.
- [x] Show Gold/Rep/Guild resource banner only on Guild Home.
- [x] Add dedicated Quest Result screen with report, incident, loot reveal, and collect-to-reward flow.
- [x] Split quest reward Keep/Sell flow from Loot inventory management.
- [x] Add 10-stat hero and loot roll foundation.

## Next

- [x] Add a repeatable emulator smoke-test checklist.
- [x] Break the gameplay screen-loop diagram into implementation-ready GitHub issues.
- [x] Add dedicated Expedition Prep screen with party slots, success estimate, risk tags, and reward preview.
- [x] Add Offline Summary return flow as a funny return-session report.
- [x] Rework Heroes tab into roster list plus full hero sheet with large art, stats, and equipment layout.
- [x] Add Item Detail with stat-based hero suggestions and explicit equip flow.
- [x] Add perfect-item visual treatment examples and implementation.
- [x] Add explicit monetization guardrails before fake rewarded ad implementation.
- [ ] Create initial GitHub issues once GitHub CLI/connector access works.

## Blocked

- [ ] Create initial GitHub issues from this board. Blocked: `gh`/GitHub connector unavailable in the agent environment.

## Later

- [x] Add Hero Detail and Equip Item flows.
- [ ] Add deeper guild upgrade effects and visible Guild Home facility changes.
- [ ] Add fake rewarded ad service.
- [ ] Add Settings screen with language and audio controls.
- [ ] Add AdMob rewarded ads.
- [ ] Add Google Play Billing no-ads product.
- [ ] Add Shop screen for no-ads, cosmetics, and starter-pack offers.
- [ ] Add Events / seasonal quests screen.
- [ ] Add Prestige / guild charter bonus screen.
- [ ] Add store listing assets.
- [ ] Add beta analytics and crash reporting.
- [ ] Investigate Codex `view_image` ACL failure on workspace screenshots.

## Initial Issue Candidates

1. Install and smoke-test current Android build on emulator.
2. Create initial GitHub issues from the repo-local Kanban.
3. Add local save for `PlaySessionState`.
4. Replace raw loot rolls with generated loot item basics.
5. Generate journal entries from expedition outcomes.
6. Slice Batch 01 artwork into production Android assets.
7. Add emulator screenshot capture to the regular QA loop.
8. Add hero roster interactions and equipment basics.
9. Add hero gacha recruitment with rarity tiers and pull-result UX.
10. Implement dedicated Expedition Prep screen with slot-limited party selection, success estimate, and risk tags.
11. Implement dedicated Quest Result screen and richer reward reveal flow.
12. Implement Offline Summary return report.
13. Add Item Detail with best-hero suggestion and explicit equip flow.
14. Document monetization guardrails for rewarded ads, no-ads, cosmetics, and random rewards.
15. Add fake rewarded ad reward flow.
16. Prepare closed-test Play Store checklist.


