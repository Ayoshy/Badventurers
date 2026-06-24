# Long-Term Gameplay Plan

Last updated: 2026-06-24

This document captures the product direction behind the new Kanban. It is intentionally detailed so the idea does not collapse back into "add more bonuses and polish the UI."

## One-Line Goal

Badventurers should become a compact guild-management idle RPG where active choices set up funny automated outcomes, and the offline economy proves that the whole guild is growing while the player is away.

## Player Fantasy

The player is not a hero. The player runs the paperwork desk for a guild that should probably not be licensed.

The satisfying loop is:

1. choose a job;
2. pick a questionable plan;
3. assign the right/wrong heroes;
4. leave them to make decisions unsupervised;
5. return to a report that says exactly how the guild profited, survived, or misunderstood the assignment;
6. spend the gains on the roster, facilities, tickets, or next unlock.

The joke is not only in the text. The joke should be in the systems: a bad plan can work, a specialist can save a run, failure can become useful, and the guild can make money while doing something visibly absurd.

## Long-Term Objective

The long-term objective is to grow from a shabby local guild into a chartered disaster operation.

### Charter Rank 1: Local Disasters

Scope: current 8 quests plus active hook.

Player goals:

- complete the first 8 quests;
- understand party prep;
- unlock basic facilities;
- recruit a few heroes;
- see first level-ups;
- experience at least one funny failure;
- unlock the first achievement rewards.

Systems needed:

- Expedition Plans;
- Result Causality Cards;
- real Reward Choice foundation;
- first version of improved Offline Summary.

### Charter Rank 2: Licensed Trouble

Scope: quests 9 to 12 and passive guild income.

Player goals:

- unlock Rare loot and better recruit odds;
- assign a Core Crew;
- see passive gold and incidents on return;
- use Scout Table and Armory/Forge as real systems;
- earn first recruitment tickets from achievements.

Systems needed:

- Core Crew;
- passive income formula;
- passive incidents;
- Rare hero/loot gates;
- ticket rewards.

### Charter Rank 3: Regional Liability

Scope: quests 13 to 16 and richer payoff.

Player goals:

- unlock Epic hero tickets from major achievements or first clears;
- unlock Epic loot chance through high-risk content;
- use hero-specific plans;
- make real reward choices after Great Success;
- start caring about promotion contracts.

Systems needed:

- Palier 3 quests;
- Epic/rare reward rules;
- first promotion storage;
- broader Reward Choice;
- stronger facility effects.

### Charter Rank 4: Chartered Disaster Operation

Scope: post-16 quest long tail.

Player goals:

- build specialized rosters;
- choose passive income strategies;
- watch or intervene in live-lite expeditions;
- complete seasonal/event quest packs;
- chase cosmetic prestige and charter milestones.

Systems needed later:

- Live-Lite Expedition Mode;
- event packs;
- prestige/charter meta;
- cosmetics/no-ads/store after retention validation.

## Content Ladder To 16 Quests

The immediate content goal is not "many more quests." It is "enough quests that progression can breathe."

### Palier Structure

| Palier | Quest count | Role | Unlocks |
| --- | ---: | --- | --- |
| 1: Local Disasters | 1-8 | Current base loop | Basic loot, common/uncommon recruits, first facilities |
| 2: Licensed Trouble | 9-12 | First expansion | Rare loot, better recruit pool, Scout Table, Core Crew |
| 3: Regional Liability | 13-16 | Midgame proof | Epic tickets, epic loot chance, stronger reward choice, hero plans |

### New Quest Slots

These are placeholders to guide scope. They can be renamed when writing final quest specs.

| Slot | Working title | Tags | Reward focus | System purpose |
| ---: | --- | --- | --- | --- |
| 9 | The Mill That Would Not Stop | Machinery, Endurance, Craft | Gold, supplies | Introduces passive income materials and Core Crew logic. |
| 10 | Festival Permit Of Doom | Paperwork, Charm, Trickery | Reputation, ticket progress | Makes paperwork/social heroes matter beyond raw score. |
| 11 | The Mirror Mine Misprint | Luck, Magic, Exploration | Rare loot | First clear unlocks higher loot visibility. |
| 12 | The Library That Charges Late Fees | Wits, Paperwork, Stealth | Rare recruit ticket | Teaches ticket reward value and Scout Table hints. |
| 13 | A Wedding With Too Many Oaths | Charm, Leadership, Risk | Epic ticket shard | Hero-specific plan clauses start appearing. |
| 14 | The Sunken Toll Booth | Endurance, Craft, Loot | Epic loot chance | Armory/Forge and passive loot hooks matter. |
| 15 | The Crown's Missing Receipt | Paperwork, Leadership, High Risk | Reputation, contracts | Accountant Office gets its first strong use case. |
| 16 | The Tower Built Sideways | Magic, Force, High Risk | Epic reward choice | Capstone for Palier 3 and bridge to live-lite/stage art. |

### Required Quest Spec Template

Every new quest should include:

- title and one-sentence premise;
- risk tier, duration, difficulty, tags, party slots;
- unlock requirements;
- base rewards and first-clear reward;
- plan hooks;
- hero spotlight interactions;
- special failure and great-success lines;
- passive incident unlocked after first clear;
- banner art brief;
- localization keys.

## Active Hook: Expedition Plans

Expedition Plans are the main short-term gameplay hook.

### Plan Rules

- A quest offers 2 to 3 plans.
- At least one plan is safe and one is greedy.
- Plans should change at least two things: duration, risk, reward, loot, XP, or result table.
- Plans should interact with tags and hero specials.
- A plan should never be a hidden best answer across every quest.

### Starter Plan Set

| Plan | Upside | Downside | Best with |
| --- | --- | --- | --- |
| Rush the Job | Shorter duration, better return speed | Higher risk, lower floor | High mobility, high confidence party |
| Safety First | Lower risk, better failure recovery | Lower great-success chance | Weak party, important unlock attempt |
| Loot Priority | Extra loot on success | Harder check, more failure variance | Strong party, loot-focused quest |
| Audit Everything | Better gold/rep and paperwork causes | Longer duration | Paperwork heroes and Accountant Office |
| Hero Leads The Plan | Big hero-specific trigger | Bigger downside if the hero underperforms | Favorite/specialist heroes |

### Result Causality

The result screen should make the player feel ownership.

Each result should show cause cards like:

- Plan: Rush the Job saved 54 seconds but added risk.
- Hero: Quill's paperwork special increased gold.
- Facility: Training Yard added party power.
- Loot: Nell's equipped item tipped the stealth check.
- Achievement: Insurance Desk improved failure payout.

## Passive Idle Core

Passive income is the missing idle backbone.

### Core Crew

The player assigns 3 heroes to run guild operations while the player is away. A 4th slot unlocks later.

Suggested roles:

| Role | Main stats | Output |
| --- | --- | --- |
| Desk Lead | Paperwork, Wits, Leadership | Gold efficiency, better reports, contract progress |
| Quartermaster | Craft, Endurance, Luck | Supplies, passive loot chance, gear upkeep |
| Recruit Wrangler | Charm, Leadership, Trickery | Recruit discounts, ticket progress, roster events |
| Trouble Scout | Stealth, Wits, Magic | Quest intel, passive incidents, rare find chance |

The same hero can still go on expeditions, but the system needs a clear rule:

- either active expedition heroes contribute only 25-50% passive value while away;
- or Core Crew heroes cannot be sent on expedition without confirming the swap.

The first option is more forgiving for mobile.

### Passive Income Formula Shape

Do not make this exact math final yet, but the shape should be:

```text
offline_gold =
    base_guild_rate
    * offline_hours_capped
    * facility_multiplier
    * core_crew_quality
    * charter_bonus
```

Where:

- base guild rate comes from guild/charter level;
- cap comes from facilities;
- facility multiplier comes from Notice Board and Accountant Office;
- core crew quality comes from hero level, rarity, relevant stats, and gear;
- charter bonus is capped and additive where possible.

### Passive Loot

Passive loot should be modest:

- common materials/supplies early;
- low chance of basic items after Armory/Forge;
- rare passive finds only after Scout Table/Armory investment;
- no passive flood that makes expeditions irrelevant.

### Passive Incidents

Return reports should include 1 to 3 incidents when enough time passed.

Examples:

- Desk Lead found an old invoice and converted it into gold.
- Quartermaster repaired a questionable item into usable loot.
- Recruit Wrangler convinced a candidate to wait for a ticket pull.
- Trouble Scout marked a new quest warning on the board.
- Accountant Office reduced a failure penalty through paperwork.

These incidents are small but emotionally important. They prove the guild lived while the app was closed.

## Achievement Tickets

Recruitment tickets make achievements attractive.

### Ticket Ladder

| Ticket | Use | Source |
| --- | --- | --- |
| Basic Hiring Voucher | One normal recruit pull | Early achievements, onboarding |
| Specialist Invitation | Pull from a narrowed class/tag pool | Quest mastery, roster achievements |
| Rare Contract Ticket | Guaranteed Rare or better | Palier 2 first clears, medium achievements |
| Epic Liability Writ | Guaranteed Epic or better | Palier 3 capstones, major achievements |
| Veteran Ticket | Hero starts above level 1 | High-risk first clears, charter milestones |
| Blank Contract | Promotion material | Achievement milestones, no random pressure |

### Guardrails

- Tickets are earnable through play.
- Paid random power stays out of scope.
- Odds and guarantees must be visible.
- Ticket rewards should be one-time or milestone-based.
- Duplicate compensation remains generous.

## Live-Lite Expedition Mode

Live-lite is a future mode, not the next immediate task. It is still worth defining now because it affects art direction.

### Goal

Let the player see heroes in action without turning Badventurers into a full manual combat RPG.

### Core Loop

1. Player launches expedition as usual.
2. If the app stays open, a "Watch Expedition" button appears.
3. The screen shows a compressed auto-run with 3 to 5 event beats.
4. Heroes perform small actions based on stats, tags, specials, and the selected plan.
5. Player can choose occasional interventions.
6. Interventions add small bonuses or alter rewards, but offline players are not punished.

### Intervention Examples

| Intervention | Effect | Cost / Limit |
| --- | --- | --- |
| Call A Safer Route | Reduce risk, reduce reward ceiling | 1 intervention per run |
| Push For Loot | Increase loot chance, increase risk | Requires loot plan or Scout Table |
| Spend Supplies | Add small score boost | Costs supplies |
| Encourage Specialist | Boost one hero's relevant stat for one beat | Limited to once per hero |
| Abort With Dignity | Avoid ridiculous failure, lose most rewards | Unlock via Infirmary |

### Implementation Guardrails

- Reuse expedition stats and result logic.
- Keep live bonuses small.
- Never require live play for core progression.
- Use live mode to make heroes lovable, not to create a second balance game.

### Art Needs

The live-lite mode needs assets beyond the current static quest banners.

| Asset | Purpose | Notes |
| --- | --- | --- |
| Stage backgrounds | Tiny scene behind the action | One per quest family, not one per quest at first |
| Hero action poses | Show heroes doing things | idle, effort, mistake, success, loot |
| Hazard cards | Represent event beats | Can be card UI instead of animated enemies |
| Intervention icons | Make choices readable | supplies, route, loot, morale, abort |
| Result cause icons | Tie live and result screens together | plan, hero, facility, loot, achievement |
| Passive report illustration | Offline Summary identity | Guild desk with reports, coins, tickets, loot |

## Visual Briefs

No production artwork is required before the systems are chosen, but these briefs preserve the direction.

### Brief 1: Contract Plan Cards

Mobile card sheet, 16-bit painterly pixel art, warm guild parchment UI, four readable icons: rushing boots, shield route, open chest, stamped audit form. Each card must read clearly at small phone size.

### Brief 2: Passive Guild Report

Portrait mobile illustration of the guild desk after a night away: papers stacked, coins sorted, a recruit ticket pinned to a board, loot in a crate, and small signs that the Core Crew has been busy.

### Brief 3: Live-Lite Expedition Stage

Side-view compact adventure stage for a mobile portrait screen. Three hero slots at bottom, event/hazard card in the middle, quest background behind, intervention buttons near thumb reach. The art should suggest action without requiring full animation.

## MVP Decision Order

The order matters:

1. Expedition Plans.
2. Result Causality Cards.
3. Reward Choice as a real choice.
4. Offline Summary 2.0.
5. Core Crew passive income.
6. Quests 9 to 12.
7. Achievement tickets.
8. Quests 13 to 16.
9. Facility gameplay pass.
10. Live-lite prototype.
11. Polish, monetization, store readiness.

If a task does not make active choice, passive return, or long-term progression stronger, it should wait.
