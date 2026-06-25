# Quest Agent Spec: The Hungry Siege

## Identity

- id: `the_hungry_siege`
- Title EN: `The Hungry Siege`
- Titre FR: `Le Siege Affame`
- Quest role: early-mid repeatable quest, designed to be the next meaningful step after `cave_minor_regrets`.
- Suggested unlock: after the player has cleared the first quest and recruited at least one additional hero, or after the guild has unlocked a party-size upgrade.

## Pitch

EN: The hamlet of Crumbwall is under siege by starving pantry goblins, a soup catapult, and one deeply offended onion. The villagers do not need legendary heroes. They need someone to hold the bread-crate barricade, count the emergency biscuits, and convince the attackers that soup can be a peace treaty.

FR: Le hameau de Miette-Mur est assiege par des gobelins de garde-manger affames, une catapulte a soupe, et un oignon beaucoup trop implique. Les villageois ne cherchent pas des legendes. Ils cherchent quelqu'un pour tenir la barricade en caisses de pain, compter les biscuits de crise, et prouver qu'une soupe peut etre un accord de paix.

Tone target: cozy disaster, food panic, medieval logistics handled by people who should not be trusted near logistics.

## Gameplay Tags

- `food`
- `siege`
- `crowd_control`
- `attrition`
- `hygiene`
- `orientation`
- `paperwork`
- `force_check`
- `midgame`
- `four_slot`
- `comedy`

## Recommended Tuning

```kotlin
Quest(
    id = "the_hungry_siege",
    durationSeconds = 180,
    difficulty = 240,
    risk = QuestRisk.Medium,
    baseGold = 880,
    pityGold = 110,
    partySlots = 4,
)
```

- difficulty: `240`
- risk: `Medium`
- expected target power with current risk penalty: `248`
- baseGold: `880`
- pityGold: `110`
- duration: `180` seconds
- partySlots: `4`
- Balance intent: a 3-hero starter party can attempt it with real uncertainty, while a fourth recruited hero should make it feel like a confident upgrade from the first quest.
- Reward feel: bigger payout chunk than `cave_minor_regrets`, but not so efficient that the first quest becomes useless for short sessions.

## Heroes Who Benefit

- `chef_cuistot` / Chef: best thematic fit. Turns panic rations into morale, soup diplomacy, and suspiciously edible tactics.
- `elementaire_de_sel` / SaltElemental: preserves rations, salts the road, and makes every wound and invoice worse in useful ways.
- `bramble` / Hunter: strong orientation and endurance make them ideal for finding supply routes around the siege line.
- `jardinier` / Gardener: can identify edible weeds, weaponize pumpkins, and explain that the village "technically has a salad problem."
- `quill`, `ledger`, `comptable` / BardAccountant or Accountant: ration ledgers, emergency contracts, compensation forms, and charging the besiegers for damages.
- `brugg`, `orla`, `troll_stupide` / Bruiser-style heroes: hold barricades, lift soup cauldrons, and solve siege engines by standing where the problem used to be.
- `pax`, `paladin` / Priest or Paladin: morale, hygiene, orderly queues, and stopping villagers from declaring the bread sainted.
- `nell`, `vex`, `sable` / Rogueish or Ninja: sneak through pantry tunnels, steal back flour, and return with "extra" spoons nobody asked about.
- `expert_en_demolition`: opens blocked gates quickly, but should carry funny collateral-risk journal lines.
- `morrow`, `chevalier_de_la_mort`: undead-adjacent heroes are useful because they do not eat the emergency rations, though they make the soup nervous.

## Expected Special Interactions

- Ration Ledger: if the party includes `BardAccountant`, `Accountant`, or any `PainfullyOrganized` hero, unlock journal lines about ration forms, siege invoices, and audited soup. Optional future effect: small gold flavor bonus or reduced pity-floor embarrassment on bad outcomes.
- Soup Diplomacy: if `chef_cuistot` is present, favor success lines where the siege ends through cooking rather than combat. With `elementaire_de_sel`, add a rare line about brined peace.
- Supply Route: if the party includes `Hunter`, `Rogueish`, or `Ninja`, unlock lines about pantry tunnels and stolen flour. Optional future effect: improve partial-success flavor into "village saved, pantry damaged."
- Barricade Duty: if the party includes `Bruiser`, `Paladin`, `DeathKnight`, or `StupidTroll`, unlock lines about holding the gate, carrying cauldrons, and being mistaken for siege equipment.
- Hygiene Check: if the party includes `Chef`, `Priest`, `Gardener`, or `SaltElemental`, use cleaner victory lines. If none are present, allow failure lines where the real enemy is soup storage.
- Demolition Clause: if `expert_en_demolition` is present, add a high-comedy branch where the siege engine is removed along with part of the objective.
- Onion Incident: any hero can trigger it, but `ApprenticeMage`, `Necromancer`, and `Chef` should get the strangest onion-related lines.

## Possible Journal Lines

FR:

- `{hero} a mis fin au siege en mangeant le plan de bataille. Personne n'a ose corriger.`
- `{hero} a trouve le tunnel de ravitaillement. Il menait surtout a une quiche tres defensive.`
- `Les gobelins ont accepte une treve contre trois bols de soupe et une excuse ecrite.`
- `{hero} a tenu la barricade. La barricade demande maintenant des indemnites.`
- `La catapulte a soupe a rate le village et assaisonne la colline.`
- `{hero} a negocie avec l'oignon geant. Tout le monde a pleure, donc on appelle ca de l'emotion.`
- `Les assiegeants etaient affames, pas malins. La guilde etait les deux.`
- `{hero} a classe les rations par urgence, puis par niveau de culpabilite.`
- `Victoire partielle: le village est sauve, la soupe est en fuite.`
- `Echec: la porte tient encore, mais du mauvais cote.`
- `{hero} a promis de ne plus utiliser "canon a bouillon" dans un espace ferme.`
- `Le rapport mentionne "pertes minimes", puis liste quatre pains, deux bancs, et la dignite du maire.`

EN:

- `{hero} ended the siege by eating the battle plan. Nobody was brave enough to correct them.`
- `{hero} found the supply tunnel. It mostly led to a very defensive quiche.`
- `The goblins accepted a truce for three bowls of soup and a written apology.`
- `{hero} held the barricade. The barricade is now requesting hazard pay.`
- `The soup catapult missed the village and seasoned the hill.`
- `{hero} negotiated with the giant onion. Everyone cried, so we are calling it diplomacy.`
- `The besiegers were hungry, not clever. The guild was both.`
- `{hero} sorted the rations by urgency, then by guilt.`
- `Partial victory: the village is safe, but the soup is at large.`
- `Failure: the gate is still standing, just on the wrong side.`
- `{hero} promised not to use "broth cannon" indoors again.`
- `The report says "minimal losses," then lists four loaves, two benches, and the mayor's dignity.`

## Artwork Prompt

Polished 16-bit pixel art 2D mobile game quest banner, 4:1 aspect ratio, comedic fantasy idle RPG, a tiny fortified bakery village under a ridiculous food siege at dusk, starving goblins with empty bowls pushing a wooden soup catapult, mismatched underqualified adventurers holding a bread-crate barricade, warm oven light, moss green and brass accents, parchment UI compatibility, chunky readable silhouettes for phone screens, cozy disaster mood, crisp pixel clusters, modern mobile polish, no readable text, no letters, no UI, no watermark, no existing franchise references.

