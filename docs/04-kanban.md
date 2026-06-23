# Kanban

Last updated: 2026-06-23

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
- Hero XP curve candidates are captured in [Hero XP progression curves](16-hero-xp-progression.md).
- Progression resource roles and reward-to-action rules are captured in [Progression economy matrix](17-progression-economy-matrix.md).
- Current core screens are Guild, Quests, Heroes, Loot, and Upgrades.
- Future MVP screens to preserve in planning: Expedition Prep, Offline Summary, Quest Result, Reward Choice, Item Detail, Equip Item, Hero Detail, Settings, and a fake Rewarded Ad Prompt.
- Longer-term future screens: Shop, Events / seasonal quests, and Prestige / charter bonuses.
- Current guild upgrades are only a basic facility-effect pass. A fuller guild/hero progression system is tracked separately under Progression / Upgrade V2 below.

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
- [x] Add quest tags and difficulty-tier metadata.
- [x] Add per-hero specials that affect expedition score, risk, rolls, and rewards.
- [x] Add multi-quest catalogue with seven new playable quests.
- [x] Update Quests and Expedition Prep screens for quest selection and active special previews.
- [x] Generate Android-ready 4:1 banner artwork for the seven new quests.
- [x] Capture agent-authored quest specs and banner contact sheet under `docs/quest-agent-specs/`.

## Next

- [x] Add a repeatable emulator smoke-test checklist.
- [x] Break the gameplay screen-loop diagram into implementation-ready GitHub issues.
- [x] Add dedicated Expedition Prep screen with party slots, success estimate, risk tags, and reward preview.
- [x] Add Offline Summary return flow as a funny return-session report.
- [x] Rework Heroes tab into roster list plus full hero sheet with large art, stats, and equipment layout.
- [x] Add Item Detail with stat-based hero suggestions and explicit equip flow.
- [x] Add perfect-item visual treatment examples and implementation.
- [x] Add explicit monetization guardrails before fake rewarded ad implementation.
- [x] Balance-test the expanded quest catalogue against starter, recruited, and upgraded rosters.
- [x] Localize quest titles, summaries, difficulty labels, and tag labels through Android resources.
- [x] Add quest-specific and special-triggered journal lines to make hero specials visible in results.
- [x] Add unlock conditions for medium/high-risk quests using reputation, guild upgrades, or completed quest counts.
- [x] Add UI treatment for recommended heroes per quest.
- [ ] Visual QA the new quest banners on emulator once image preview/screenshot ACL issues are solved.
- [ ] Design Progression / Upgrade V2: guild facilities, hero XP, level-ups, promotions, and duplicate-use rules.
- [ ] Define first 30-minute progression pacing across quests, gold, reputation, hero XP, and upgrade costs.
- [ ] Create initial GitHub issues once GitHub CLI/connector access works.

## Blocked

- [ ] Create initial GitHub issues from this board. Blocked: `gh`/GitHub connector unavailable in the agent environment.

## Progression / Upgrade V2

### Guild Facilities

- [ ] Split the current basic facility pass into a real upgrade model with level caps, unlock requirements, costs, and visible effects.
- [x] Define the MVP facility set: Notice Board, Training Yard, Bunk Room, Armory/Forge, Infirmary, Scout Table, Tavern/Kitchen, and Accountant Office.
- [ ] Map each facility to clear gameplay effects: quest unlocks, party slots, hero XP gain, risk mitigation, expedition duration, loot quality, offline cap, recruit pool quality, and gold/reputation yield.
- [ ] Add facility unlock gates using reputation, completed quests, guild level, and previous facility levels.
- [x] Add upgrade recommendations on Guild Home based on the player's current bottleneck.
- [ ] Add visible Guild Home facility state changes or badges so upgrades feel concrete, not just numeric.
- [ ] Add facility persistence, migration, and unit tests once the model is implemented.
- [ ] Balance upgrade costs and effects for the first 30 minutes, first return session, and first high-risk quest unlock.

### Hero Growth

- [x] Define per-hero XP storage and level curve; current reward XP exists but does not yet drive hero growth.
- [x] Apply expedition XP to participating heroes and persist level/XP progress.
- [x] Add level-up stat growth rules using the 10-stat foundation without making every hero feel identical.
- [x] Add a post-result level-up reveal for heroes who gained levels.
- [x] Add Hero Detail progression UI: XP bar, next-level preview, stat changes, and special notes.
- [ ] Define hero promotion/rank-up rules for longer-term growth beyond raw levels.
- [ ] Decide how duplicate hero pulls feed progression: reputation only, hero shards/contracts, promotion currency, or selectable compensation.
- [ ] Add training actions or Training Yard bonuses that improve heroes without replacing expedition XP.
- [ ] Add balance tests for starter heroes, recruited heroes, level gaps, and promoted heroes.

### Upgrade Economy And UX

- [x] Create a single progression economy matrix covering gold, reputation, guild XP, hero XP, duplicate compensation, and optional future supplies.
- [ ] Make every major reward answer "what can I improve now?" through upgrade prompts, hero suggestions, or equip suggestions.
- [x] Replace manual recommended heroes with a dynamic hybrid score using active specials, quest tags, hero stats, equipment, and estimated success gain.
- [x] Define when the game should encourage upgrading guild facilities versus leveling heroes versus equipping loot.
- [ ] Add upgrade preview copy in English and French before implementation to keep the systems readable.
- [ ] Keep paid/random monetization out of hero power progression; rewarded ads may boost optional rewards only under the monetization guardrails.

## Later

- [x] Add Hero Detail and Equip Item flows.
- [x] Add basic guild facility effects and visible Guild Home facility status.
- [x] Add fake rewarded ad service.
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
17. Balance and gate the expanded quest catalogue.
18. Localize quest metadata and tag labels.
19. Add quest-specific journal content and special-trigger result lines.
20. Add recommended-hero treatment in quest prep.
21. Design guild and hero progression V2.
22. Add real guild facility model with unlocks, costs, and visible effects.
23. Add hero XP, level-up, and progression UI.
24. Define duplicate hero progression or compensation rules.
