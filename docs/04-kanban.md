# Kanban

Last updated: 2026-06-24

This is the repo-local product board. It replaces the previous "almost polish" framing with a more honest one:

> Badventurers has a working core loop, but it is not ready for final polish. The next goal is to make the active decision layer and the long-term idle layer strong enough that returning later feels exciting.

GitHub Issues can mirror this board later, but this file is the source of truth while product direction is still moving.

## North Star

Build a funny guild-management idle RPG where the player:

1. prepares a questionable plan;
2. assigns a roster that visibly changes the outcome;
3. leaves the guild running in the background;
4. returns to a compact report full of rewards, incidents, passive income, and one obvious next decision.

The long-term hook is not "send mission, wait, collect." It is:

> My guild is becoming a strange little machine. My roster, facilities, plans, and achievements change what happens while I am away.

## Current Reality

### Baseline Already In

- Android app builds and tests pass.
- The core expedition loop works: choose quest, prep party, run timer, resolve result, collect loot, upgrade/recruit/equip.
- The app has 8 playable quests, hero stats, hero specials, loot, XP/level-ups, gacha recruitment, duplicate reputation, achievements, save/load, and offline expedition completion.
- The best existing active screen is Expedition Prep, but it is still more of an optimizer than a gameplay hook.

### Main Problems

- 8 quests is not enough content for a real progression spine.
- Active play lacks a signature verb. The player mostly follows recommendations and waits.
- Offline progress is too tied to one active mission. There is not yet a true passive guild economy.
- Hero specials are mostly formula modifiers; they need more visible result causality.
- Achievement rewards are useful but not attractive enough yet. Recruitment tickets and rare unlocks should make them matter.
- Facilities are defined more deeply than they are implemented.
- "Polish" should wait until the active hook, passive idle loop, and content ladder are stronger.

## Product Targets

### Target A: Active Hook

Expedition Prep becomes a small tactical ritual. Before launch, the player chooses a contract plan or clause:

- faster but riskier;
- safer but lower reward;
- loot-focused;
- gold-focused;
- hero-specific;
- quest-specific.

The result screen then explains why the chosen plan and heroes mattered.

### Target B: Content Ladder

Move from 8 quests to at least 16 quests, grouped into unlock paliers. Each palier unlocks new reward quality, hero pool quality, facilities, or systems.

### Target C: Passive Guild Income

The player selects a Core Crew of 3 to 4 heroes. Their stats, levels, rarity, gear, and guild facilities generate passive gold, supplies, light loot chances, and offline incidents.

The return screen combines:

- finished expedition;
- passive guild income;
- passive incidents;
- achievement/ticket progress;
- one recommended next action.

### Target D: Achievement Pull Rewards

Achievements should award attractive one-time prizes:

- basic recruitment tickets;
- rare/epic recruitment tickets;
- veteran hero tickets;
- blank promotion contracts;
- special reward-choice unlocks.

Tickets must stay earnable through play and follow monetization guardrails.

### Target E: Live-Lite Mode

A future optional mode can show heroes in action while an expedition is running. It should be a light viewer/intervention mode, not a full combat game that competes with the idle loop.

## Now: Rebuild The Fun Core

These are the next implementation priorities.

### 1. Expedition Plans / Contract Clauses

- [x] Add an `ExpeditionPlan` domain model with id, title key, summary key, tags, modifiers, and unlock conditions.
- [x] Add plan selection to Expedition Prep.
- [x] Persist selected plan in active expedition snapshot.
- [x] Implement 4 generic plans:
  - Rush the Job: shorter duration, higher risk, lower consistency.
  - Safety First: lower risk, lower great-success odds.
  - Loot Priority: harder success check, extra loot roll on success.
  - Audit Everything: longer duration, better gold/reputation on paperwork-friendly quests.
- [x] 2026-06-24: Generic contract plans implemented in code, UI, save migration, and tests; quest-specific plans remain next.
- [x] Add 1 to 2 quest-specific plans for the first 8 quests. 2026-06-24: Added one authored quest-specific contract clause for each of the 8 current quests, with UI copy and estimate coverage.
- [x] Add balance tests proving plans change risk/reward without making one plan always optimal.

### 2. Result Causality Cards

- [x] Add a `ResultCause` model for plan effects, hero special triggers, facility effects, loot bonuses, pity effects, and achievement effects.
- [x] Show 2 to 4 cause cards on Quest Result.
- [x] Make hero specials read like visible events, not hidden math.
- [x] 2026-06-24: Result causality cards implemented for plan, hero specials, facilities, and achievement features; plan-specific prose remains next.
- [x] Add result copy for plan-specific wins and failures. 2026-06-24: Win/mixed/fail lines added for all four generic plans in result and offline report.
- [x] Test that a result can explain its major modifiers deterministically.

### 3. Offline Summary 2.0

- [x] Rework Offline Summary into a combined return report. 2026-06-24: Offline Summary now combines completed expedition, away time, result causes, rewards, party, charter progress, and a projected next action.
- [x] Include active expedition completion. 2026-06-24: The return report explicitly names the completed quest and outcome.
- [ ] Include passive guild income once Core Crew exists.
- [ ] Include passive incidents.
- [ ] Include achievement/ticket progress. 2026-06-24: Charter achievement progress now appears in the return report; ticket progress remains pending until ticket types exist.
- [x] End with one concrete next action. 2026-06-24: The report ends with the same projected next-action panel used after quest rewards.

### 4. Reward Choice / Loot Recovery Friction

- [x] 2026-06-24: Replace the old "keep or sell every dropped item" flow with a limited recovery choice. By default, the guild can bring back only 1 item from the dropped loot pool; unchosen loot is destroyed.
- [x] 2026-06-24: Add state, save migration, UI copy, and tests for pending loot carry capacity.
- [x] 2026-06-24: Add first capacity bonuses: Bunk Room levels increase recovery capacity, veteran heroes can add a slot, and loot-specialist heroes get a level-gated hook.
- [x] Keep the Reward Choice charter unlock meaningful by making it add an extra loot candidate on Great Success, not an automatic extra kept item.
- [x] Add visible UI breakdown for why the party can recover N items: base, Bunk Room, veteran, specialist. 2026-06-24: Reward Loot now shows persisted recovery sources for base, Bunk Room, veteran heroes, and loot specialists.
- [x] Add authored hero/level-up rewards that explicitly increase loot recovery capacity. 2026-06-24: Hero progression now announces specialist and veteran recovery-slot unlocks, with result and hero-detail UI copy.
- [ ] Later reward-choice variants:
  - item A vs item B;
  - gold vs reputation;
  - loot now vs ticket progress;
  - safe reward vs risky bonus.

## Next: Double The Quest Content

Goal: expand from 8 quests to 16 quests with meaningful paliers.

### Palier 1: Local Disasters

- [x] 8 current quests playable.
- [ ] Rebalance current unlocks after Expedition Plans exist.
- [x] Add plan hooks to every current quest. 2026-06-24: Every current quest now exposes a unique contract clause alongside the generic plans.

### Palier 2: Licensed Trouble

- [ ] Add quests 9 to 12.
- [ ] Unlock Rare loot as a visible tier goal.
- [ ] Unlock Rare hero pool improvements.
- [ ] Unlock Scout Table as a functional facility.
- [ ] Introduce Core Crew passive income.
- [ ] Add quest banners, localization, journal lines, and balance tests.

### Palier 3: Regional Liability

- [ ] Add quests 13 to 16.
- [ ] Unlock Epic hero ticket rewards from achievements and first clears.
- [ ] Unlock Epic loot chance through high-risk quests or Armory/Forge.
- [ ] Unlock Reward Choice as a regular Great Success payoff.
- [ ] Add more hero-specific plan clauses.
- [ ] Add quest banners, localization, journal lines, and balance tests.

### Content Rules For Every New Quest

- [ ] Define tags, risk tier, duration, difficulty, reward focus, and unlock gate.
- [ ] Define at least 2 plan hooks.
- [ ] Define at least 2 hero spotlight interactions.
- [ ] Define 1 passive guild incident that can appear after the quest is first cleared.
- [ ] Define first-clear reward and repeat reward.
- [ ] Add localized title/summary/tags.
- [ ] Add banner art or an explicit temporary art brief.

## Next: Passive Guild Income

Goal: idle progress should come from the quality of the guild, not only from a single timer.

### Core Crew

- [ ] Add a Core Crew assignment screen or Guild Home card.
- [ ] Start with 3 Core Crew slots.
- [ ] Unlock a 4th slot through Bunk Room or Charter progress.
- [ ] Let each assigned hero contribute based on stats, level, rarity, gear, and special.
- [ ] Prevent active expedition heroes from double-counting at full passive value, or apply a clear reduced contribution rule.

### Passive Income Model

- [ ] Add passive gold per hour based on guild level, Notice Board, Accountant Office, and Core Crew.
- [ ] Add passive supplies or "guild errands" as a future prep resource.
- [ ] Add low-frequency passive loot finds gated by Armory/Forge and Scout Table.
- [ ] Add offline cap upgrades through facilities.
- [ ] Add tests for offline income caps, roster contribution, and save migration.

### Passive Incidents

- [ ] Add a passive incident generator for offline reports.
- [ ] Incidents should mention Core Crew heroes, facilities, and unlocked quest regions.
- [ ] Incidents can grant small rewards, warnings, or achievement progress.
- [ ] Keep passive rewards useful but below active quest rewards unless the player has built heavily for passive income.

## Next: Achievements And Recruitment Tickets

Goal: make hauts faits feel like progression milestones, not just checkboxes.

### Ticket Types

- [ ] Basic Hiring Voucher: one normal recruit pull.
- [ ] Specialist Invitation: pull from a narrowed class/tag pool.
- [ ] Rare Contract Ticket: guaranteed Rare or better hero.
- [ ] Epic Liability Writ: guaranteed Epic or better hero, limited to major milestones.
- [ ] Veteran Ticket: recruit a hero at a higher starting level.
- [ ] Blank Contract: non-random promotion material.

### Achievement Reward Pass

- [ ] Add ticket rewards to early, medium, and high-value achievements.
- [ ] Add first-clear ticket rewards to selected Palier 2 and Palier 3 quests.
- [ ] Make duplicate handling show both immediate reputation and future contract value.
- [ ] Add UI copy explaining ticket odds and guaranteed rarity.
- [ ] Test that ticket rewards are one-time, saved, and claimable only once.

## Next: Facilities With Gameplay Effects

The facility catalog already names the right fantasy. The next pass should make each facility unlock an active or passive lever.

- [ ] Scout Table: reveals plan warnings, unlock previews, and passive scouting incidents.
- [ ] Armory/Forge: improves loot quality, enables item rerolls, and supports passive loot finds.
- [ ] Infirmary: improves failure recovery and unlocks safe plan variants.
- [ ] Tavern/Kitchen: improves morale, hero XP, and long expedition support.
- [ ] Accountant Office: improves passive gold, paperwork bonuses, and duplicate/contract UX.
- [ ] Add tests proving each implemented facility changes a real decision.

## Later: Live-Lite Expedition Mode

This is not the immediate fix, but it is a strong medium-term bet if the prep/result hook still needs more juice.

### Product Shape

- [ ] Prototype a "Watch Expedition" screen for active expeditions.
- [ ] Heroes auto-act in a short side-view or card-view sequence.
- [ ] Player can make light interventions while watching:
  - spend a supply;
  - call a safer route;
  - push for loot;
  - encourage a hero;
  - trigger a hero-specific trick.
- [ ] Interventions should give small bonuses, not punish offline players.
- [ ] The mode should feed the same `ExpeditionEngine`, not become a separate combat simulator.

### Asset Needs

- [ ] Small quest stage backgrounds by biome/theme.
- [ ] Hero action pose sheet: idle, effort, success, mistake, loot.
- [ ] Hazard/event card illustrations.
- [ ] Plan/clause card icons.
- [ ] Result cause card icons.
- [ ] Passive income report illustration for Guild Home / Offline Summary.

## Later: Long-Term Progression

- [ ] Implement hero promotion rank storage.
- [ ] Implement hero contracts and blank contracts.
- [ ] Add prestige / Guild Charter reset or non-reset meta layer only after the 16-quest ladder works.
- [ ] Add events / seasonal quest packs after the base content ladder feels good.
- [ ] Add store/no-ads/cosmetics only after core retention is strong.

## Polish Queue

These matter, but they should not distract from the hook.

- [ ] Fix mojibake / encoding issues in French docs and strings.
- [ ] Add Settings screen with language and audio controls.
- [ ] Clean up Loot screen actions where "Keep" is already implied.
- [ ] Update README and AGENTS docs so they match the current app.
- [ ] Add store listing assets.
- [ ] Add beta analytics and crash reporting.
- [ ] Add AdMob rewarded ads only after monetization copy and guardrails are validated.
- [ ] Add Google Play Billing no-ads product only after the loop is fun enough to test.

## Blocked / External

- [ ] Create GitHub Issues from this board when `gh` or the GitHub connector is available.

## References

- Long-term design brief: [Long-term gameplay plan](19-long-term-gameplay-plan.md)
- Roadmap: [Roadmap](03-roadmap.md)
- Competitive research: [Competitive research](13-competitive-research.md)
- Progression economy: [Progression economy matrix](17-progression-economy-matrix.md)
- Achievements: [Achievement system plan](15-achievements-system.md)
- Promotion and duplicates: [Hero promotion and duplicate rules](18-hero-promotion-duplicate-rules.md)
