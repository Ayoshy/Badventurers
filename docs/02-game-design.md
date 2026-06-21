# Game Design

Last updated: 2026-06-21

## Player Fantasy

The player turns a disastrous guild into a suspiciously successful business. Every upgrade feels like putting tape on a collapsing wagon, and somehow the wagon becomes profitable.

## Session Flow

1. Open the guild.
2. Collect completed expedition rewards.
3. Read the funniest recent log lines.
4. Equip better loot or sell junk.
5. Upgrade a facility or hero.
6. Start another expedition.
7. Optionally watch a rewarded ad for a bonus.

## Primary Resources

- Gold: main spend currency.
- Reputation: unlocks quests, heroes, and facilities.
- Guild XP: long-term account progression.
- Supplies: optional pacing currency for expeditions, added only if needed.

## Hero Stats

- Might: physical contribution.
- Wits: magic and tactical contribution.
- Sneak: traps, ambushes, and loot quality.
- Grit: injury and failure resistance.
- Luck: chaos modifier.
- Ego: improves big wins and big mistakes.

## Starting Classes

- Bruiser: high Might, low subtlety.
- Apprentice Mage: high Wits, unstable outcomes.
- Rogue-ish: high Sneak, may pocket loot.
- Field Medic: reduces injury and failure penalties.
- Ranger: balanced, good at exploration.
- Bard Accountant: improves gold and reputation, questionable morale.

## Traits

Traits should combine a mechanical modifier with a comedic identity.

Examples:

- Cowardly: lower injury chance, lower success chance on dangerous quests.
- Overconfident: higher critical success chance, higher ridiculous failure chance.
- Allergic To Slime: penalty in wet dungeon tags, bonus in dry tags.
- Suspiciously Lucky: improved loot rolls, rare chance to create debt.
- Reads The Manual: faster expedition prep, lower Ego.

## Expedition Resolution

Prototype formula:

```text
partyScore = sum(hero stats + gear bonuses + guild bonuses)
questScore = difficulty + riskModifier
roll = random weighted by Luck and traits
result = compare(partyScore + roll, questScore)
```

Result bands:

- Great Success: best reward table, positive rare log.
- Success: standard rewards.
- Partial Success: reduced rewards, funny complication.
- Failure: small compensation, injury or delay chance.
- Ridiculous Failure: memorable log, small pity reward, possible achievement later.

## Loot Model

Item fields:

- id
- localized name
- localized flavor
- rarity
- slot
- stat modifiers
- tags
- sell value

MVP slots:

- Weapon
- Armor
- Trinket

Example items:

- Bent Sword Of Intentions
- Boots Of Strategic Retreat
- Almost Invisible Ring
- Helmet With A Plan
- Staff Of Fire, Usually

## Journal Content

Journal entries should be modular:

```text
{hero} tried to disarm {trap}. The trap gave them pointers.
{hero} cast {spell}. The room filed a complaint.
{hero} found {item}. Nobody knows why it was warm.
```

Rules:

- Keep entries short.
- Avoid long lore dumps.
- Mix success, failure, and neutral flavor.
- Prefer reusable templates with variable nouns.

## Progression

Early progression should unlock quickly:

- First 2 minutes: first quest and first loot.
- First 5 minutes: first upgrade.
- First 10 minutes: second hero or second quest area.
- First return session: offline rewards and a clear upgrade target.

Longer-term systems:

- Guild facilities.
- Hero promotions.
- Prestige reset with permanent guild charter bonuses.
- Seasonal quest packs.

