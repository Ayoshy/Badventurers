# Progression Economy Matrix

Last updated: 2026-06-23

This note is the single planning surface for Badventurers progression currencies. It connects quest rewards, guild upgrades, hero XP, duplicate handling, loot, and future optional resources so each reward can answer: "what can I improve now?"

## Economy Goals

- Keep the first session generous: the player should launch, collect, equip, upgrade, and recruit without needing a spreadsheet.
- Make gold the short-loop spend currency, reputation the unlock pressure valve, hero XP the participation reward, and duplicates the long-term hero growth hook.
- Keep every paid or ad-adjacent path out of core power progression. Rewarded ads can add optional bonus rewards under `docs/14-monetization-guardrails.md`, but cannot become the best route to hero power.
- Prefer clear upgrades over hidden multipliers. When a reward lands, the UI should point to a concrete next action.

## Resource Roles

| Resource | Primary sources | Primary sinks | Player question answered | Guardrail |
| --- | --- | --- | --- | --- |
| Gold | Quest rewards, loot sells, achievements, optional rewarded bonus in prototype/debug | Guild facility upgrades, recruitment, future shop cosmetics/starter conveniences | "What can I buy right now?" | Do not sell random power for real money. |
| Reputation | Quest milestones, medium/high-risk unlocks, duplicate compensation, achievements | Quest gates, facility unlock gates, future recruit pool tiers | "What new trouble trusts my guild?" | Keep rep mostly earned through play, not ads. |
| Hero XP | Participating heroes on expedition result, future Training Yard actions/bonus | Hero levels, promotion eligibility | "Which heroes are getting better?" | XP comes from play; no paid XP boosts. |
| Guild XP | Future meta reward from completed quests, achievements, first clears | Guild level, facility cap unlocks, offline cap upgrades | "How established is this guild?" | Use as a pacing gate, not another spendable wallet. |
| Duplicate contracts | Duplicate hero pulls, future achievement milestones | Hero promotions/rank-up, optional shard conversion | "What happens when I pull someone I already own?" | No paid random duplicate pressure. |
| Charter seals | Achievement claims | Milestone unlocks, prestige/pre-charter bonuses | "What long-term milestone did I earn?" | Bounded bonuses, mostly unlocks and visibility. |
| Supplies | Future optional expedition material from offline returns/events | Temporary expedition prep boosts, event entries | "Can I take a tactical shortcut?" | Keep optional and non-mandatory for core quests. |

## Early Pacing Targets

The first 30 minutes should alternate between immediate action and clear anticipation.

| Time band | Expected player state | Reward emphasis | Next spend prompt |
| --- | --- | --- | --- |
| 0-3 min | First quest launched and collected | Gold, first loot, hero XP progress | Equip the obvious item or buy the cheapest guild upgrade. |
| 3-8 min | First repeat loop, one visible level bar moving | Gold, common/uncommon loot, first achievement | Notice Board if gold is close; otherwise recommend another quest. |
| 8-15 min | First recruit or facility choice | Gold, reputation trickle, duplicate explanation if it happens | Recruit if roster is thin; Training Yard if odds are poor; Bunk Room if party slots block progress. |
| 15-25 min | Medium-risk quests visible, some locked | Reputation and facility gates become readable | Show exact missing gate: rep, completed quests, or facility level. |
| 25-30 min | Player has seen at least one real upgrade outcome | Hero XP, better-fit hero prompts, loot suggestions | Point to the bottleneck: hero fit, facility unlock, or equipment. |

## Calibrated 30-Minute Prototype Path

Implemented baseline as of 2026-06-23:

- New sessions start at 0 gold, 0 reputation, guild level 1, and level-1 implemented facilities. The debug top-bar +/- controls replace the old rich debug start.
- A deterministic first-session route can clear Cave of Minor Regrets, Forest of Wrong Turns, Bandit Tax Office, Salted Swamp Chapel, Moonlit Smuggler Run, and The Hungry Siege in 855 quest seconds, leaving room inside the 30-minute target for reward handling and upgrade decisions.
- The route affords Notice Board 2 after the first clear, Training Yard 2 after the second clear, and Bunk Room 2 after the third clear while still leaving enough gold for a recruit or another meaningful action by the first high-risk unlock.
- Six clears unlock the first high-risk quests through completed-quest pacing, and starter heroes gain visible XP plus at least one level-up along the route.
- This is covered by `FirstThirtyMinutePacingTest`; first-return/offline pacing still needs its own pass once offline rewards are tuned.

## Reward-To-Action Rules

Each major reward surface should recommend one useful next action, not three competing lectures.

| Situation | Preferred prompt | Secondary prompt | Reason |
| --- | --- | --- | --- |
| Player has enough gold for a meaningful facility | Upgrade the best bottleneck facility | Equip newly kept loot | Gold is most satisfying when it immediately changes the guild. |
| Quest odds are below 70% and a better party exists | Swap to best-fit heroes | Upgrade Training Yard if affordable | Dynamic hero recommendations are cheaper than forcing upgrades. |
| Quest locked by reputation | Run highest unlocked quest with good odds | Claim achievements if rep reward is ready | Reputation should feel earned by becoming more famous. |
| Quest locked by party size | Upgrade Bunk Room | Recruit if roster is too small | Slot locks need an obvious facility answer. |
| Quest locked by Notice Board or Training Yard | Upgrade that facility | Sell unused loot if short on gold | Facility gates should name the exact next spend. |
| New loot kept with positive hero fit | Equip suggested hero | Sell if no fit beats current gear | Loot is only joyful if the upgrade path is visible. |
| Hero leveled up | Open Hero Detail | Send that hero where their special matches | Level-up should lead back to party composition. |
| Duplicate hero pulled | Explain duplicate contract/reputation gain | Show future promotion progress once implemented | Duplicates must feel generous and transparent. |
| Achievement claim ready | Claim reward | Show what the reward unlocks | Achievements should become long-term unlock scaffolding. |

## Facility Economy Draft

This is the target shape before replacing the current basic facility pass.

| Facility | Core effect | Unlock gate | Cost curve role | First 30-minute intent |
| --- | --- | --- | --- | --- |
| Notice Board | More quest gold, quest visibility, some quest gates | Always available | Cheapest early sink, ramps gently | First obvious upgrade and gold feedback loop. |
| Training Yard | Party power, future XP/training actions | After first completed quest or rep 2 | Medium-cost tactical sink | Fixes "my odds are bad" without replacing hero XP. |
| Bunk Room | Party slots and roster comfort | Rep/completed quest gate | Spiky milestone sink | Unlocks 4-hero quests and makes recruitment matter. |
| Armory/Forge | Equipment quality, equip suggestions, future rerolls | Notice Board level or loot milestone | Midgame loot sink | Makes loot management feel purposeful. |
| Infirmary | Risk mitigation, failure recovery, pity rewards | Failure/achievement milestone | Defensive sink | Turns bad outcomes into funny resilience. |
| Scout Table | Better quest intel, reduced duration, unlock previews | Rep and completed quest gate | Utility sink | Helps players choose the next best quest. |
| Tavern/Kitchen | Morale, hero XP bonus, long quest support | Roster size or chef-themed unlock | Support sink | Makes repeated expeditions feel cozy and productive. |
| Accountant Office | Gold efficiency, duplicate contracts, paperwork bonuses | Rep and paperwork quest gate | Optimization sink | Gives late early-game players a clever economy toy. |

## Duplicate Progression Draft

Current prototype duplicate pulls grant reputation. Keep that as the immediate fallback, then layer a clearer long-term path:

1. Duplicate pull always grants reputation now.
2. Duplicate also grants one future `contract` for that hero family.
3. Contracts fill promotion progress once promotion exists.
4. When a hero is fully promoted for the current cap, duplicates convert to reputation plus charter seal progress, never wasted power pressure.

This keeps the current generous behavior while making future gacha-adjacent progression understandable and monetization-safe.

## Recommendation Priority

Future upgrade prompts should evaluate bottlenecks in this order:

1. **Blocked content:** exact quest unlock requirement that the player can act on soon.
2. **Low success odds:** best-fit hero swap, Training Yard, equipment, or level-up path.
3. **Idle spend:** affordable facility with the highest immediate benefit.
4. **Reward cleanup:** equip useful loot, sell weak loot, claim achievements.
5. **Long-term growth:** hero detail, promotion progress, charter milestone.

The UI copy should stay concrete: "Upgrade Bunk Room for 4th slot" beats "Improve your guild."

## Open Implementation Hooks

- Replace the current hardcoded Guild Home recommendation with a `ProgressionAdvisor`.
- Give quest result and loot reward screens a single `nextAction` prompt.
- Tune facility costs numerically against the first 30-minute pacing now that the facility catalog owns caps, costs, gates, and effects.
- Add duplicate contract fields only when promotion rules are finalized.
- Keep rewarded-ad bonus copy separate from all advisor decisions.
