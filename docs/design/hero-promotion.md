# Hero Promotion And Duplicate Rules

Last updated: 2026-06-23

This note defines the longer-term hero growth layer that sits above the current XP and level system. It also decides how duplicate hero pulls should feed progression without making random pulls the required route to power.

## Goals

- Keep expedition XP as the main way heroes improve.
- Make duplicate pulls feel useful without making them mandatory.
- Give long-lived heroes a visible rank ladder after early levels.
- Keep paid or ad-adjacent rewards out of core hero power.
- Make the eventual implementation easy to migrate from the current `Hero(level, xp)` model.

## Terms

| Term | Meaning |
| --- | --- |
| Hero level | Current participation growth from expedition XP. Levels grant stat growth and stay the primary power path. |
| Promotion rank | A bounded long-term rank for a specific hero. Rank starts at 0 and caps at 4 for MVP planning. |
| Hero contract | Duplicate currency keyed to one hero id. It represents paperwork proving the guild somehow hired the same person twice. |
| Blank contract | A future non-random promotion token from achievements, events, or Training Yard milestones. It can replace one hero contract. |

## Promotion Ranks

Promotion rank should be stored separately from level so the current XP system remains stable.

| Rank | Display name | Eligibility | Contract cost | Gold cost | Intended effect |
| ---: | --- | --- | ---: | ---: | --- |
| 0 | Filed Adventurer | Current default | 0 | 0 | Current hero behavior. |
| 1 | Certified Liability | Level 5, 5 completed quests | 1 | 500 | Small stat boost and promotion badge. |
| 2 | Senior Liability | Level 10, Training Yard 2, 15 completed quests | 2 | 1,200 | Stronger class stat boost and special preview copy. |
| 3 | Charter Oddity | Level 15, Training Yard 3, 30 completed quests | 3 | 2,500 | Small special upgrade hook for future implementation. |
| 4 | Guild Legend | Level 20, clear any high-risk quest, Training Yard 4 | 5 | 5,000 | Final MVP rank cap with prestige-style visuals, not a new random chase. |

Ranks are eligibility milestones, not immediate hard level caps. The prototype can keep leveling heroes past these thresholds until the promotion UI and migration are ready.

## Promotion Effects

Promotion bonuses should be bounded and readable:

- Each rank grants a one-time stat package based on the hero class growth profile: `+3 primary`, `+2 secondary`, and `+1 trait stat`.
- Ranks 2 and 3 may later unlock special-text upgrades or tiny special modifiers, but these must be capped and tested against quest balance.
- Rank 4 is mostly prestige and identity: a final stat package, a badge, and stronger result/recommendation copy.
- Promotions should never be required for the first high-risk quest unlock. Levels, equipment, facility upgrades, and better party fit must remain enough.

## Duplicate Pull Decision

Current behavior stays valid: duplicate pulls grant reputation immediately.

When contracts are implemented, a duplicate pull should:

1. Grant the current reputation reward immediately.
2. Add `1` hero contract for the duplicate hero id.
3. Surface promotion progress if that hero is near an eligible rank.
4. If the hero is fully promoted for the current cap, keep the contract in overflow or convert it to a future charter milestone reward. It should never vanish.

Blank contracts should come from non-random play sources before promotions become important:

- Achievement milestones.
- Training Yard milestones or training actions.
- First-clear rewards for higher-risk quests.
- Future events, only if they follow monetization guardrails.

## Economy Guardrails

- Do not sell hero contracts directly for real money.
- Rewarded ads may not grant contracts or promotion ranks.
- Recruitment can be a source of duplicates, but promotions must have at least one non-random contract source before they affect meaningful quest power.
- Duplicate compensation remains generous even before promotion implementation: reputation now, contracts later.

## Implemented Balance Hook

`HeroPromotion.previewPromoted` now projects the planned promotion stat package for balance tests only. It clamps to the MVP rank cap and applies the documented `+3 primary`, `+2 secondary`, and `+1 trait stat` package per rank.

This is not full promotion implementation yet: promotion rank storage, contract storage, eligibility checks, and UI are still future work.

## Implementation Hooks

Future code work should add these fields with a snapshot version bump:

- `promotionRank` on each restored hero or a `promotionRanksByHeroId` map in `PlaySessionState`.
- `heroContractsByHeroId: Map<String, Int>`.
- `blankHeroContracts: Int`.

Recommended tests:

- Duplicate pull grants reputation plus a hero contract once contracts exist.
- Promotion eligibility checks level, completed quests, Training Yard, high-risk clear, contracts, and gold.
- Promotion consumes hero contracts before blank contracts.
- Fully promoted duplicate contracts do not disappear.
- Promoted hero balance remains below the existing upgraded-roster safety ceiling unless equipment and facilities also contribute.
