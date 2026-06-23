# Quest Agent Spec: Salted Swamp Chapel

## Identity

- id: `salted_swamp_chapel`
- title_fr: `Chapelle du Marais au Sel`
- title_en: `Salted Swamp Chapel`
- content_role: Early-mid quest, intended after the starter cave and first recruitment.
- unlock_hint: Unlock after the player has cleared `cave_minor_regrets` at least once and owns 4+ heroes, or when the Notice Board reaches level 2.

## Pitch

FR: Une chapelle s'enfonce lentement dans un marais trop sale, trop sale, et beaucoup trop administratif. Les villageois jurent qu'elle guerit les maledictions mineures, mais seulement si l'on remplit le formulaire de confession en trois exemplaires et que personne ne leche les murs.

EN: A chapel is sinking into a swamp that is too salty, too dirty, and far too administrative. Locals swear it cures minor curses, but only if the confession form is filed in triplicate and nobody licks the walls.

Tone target: Cozy disaster, religious bureaucracy, suspicious brine miracles, underqualified heroes treating a sacred site like a damp side hustle.

## Gameplay Tags

- `wet`
- `swamp`
- `salt`
- `chapel`
- `undead`
- `paperwork`
- `slime`
- `holy`
- `navigation`
- `minor_curse`

## Recommended Quest Values

```kotlin
Quest(
    id = "salted_swamp_chapel",
    durationSeconds = 180,
    difficulty = 210,
    risk = QuestRisk.Medium,
    baseGold = 560,
    pityGold = 60,
    partySlots = 4,
)
```

Design notes:

- `difficulty = 210`: Starter trio can scrape through with risk, but a fourth recruit makes the quest feel fair and rewarding.
- `risk = Medium`: The location is hazardous without becoming a punishment wall; the comedy should come from complications, not constant failure.
- `baseGold = 560`: A meaningful step above the starter quest while still below later high-risk expeditions.
- `pityGold = 60`: Enough to soften a bad roll and keep the idle loop moving.
- `durationSeconds = 180`: Three minutes gives the chapel a stronger "send them away, come back later" identity than the first quest.
- `partySlots = 4`: Encourages trying the new recruitment system and creates room for support heroes to matter.

## Reward Flavor

Suggested reward identity:

- Main reward: Gold from "recovered donations" that may or may not have been dropped today.
- Loot flavor: Brine-soaked holy objects, marsh survival junk, suspiciously useful salt.
- Rare loot ideas:
  - `Choir Bell With One Threat Left` - Trinket, Magic/Paperwork.
  - `Boots Of Liturgical Squish` - Armor, Orientation/Hygiene.
  - `Salt-Crusted Ladle` - Weapon, Force/Luck, especially funny on Chef.
  - `Confession Receipt, Pre-Stamped` - Trinket, Paperwork/BadFaith.

## Heroes Who Benefit

- Pax, Priest: High Hygiene, Magic, Paperwork, and Charisma fit the chapel cleansing angle. Expected to reduce curse-related complication lines and turn partial successes into cleaner outcomes.
- Paladin: Strong holy-site fantasy with good Endurance, Force, Hygiene, and Paperwork. Should be the reliable anchor when the chapel tries to declare the party morally unpaid.
- Elementaire de sel: Signature synergy with `salt`, `wet`, and `swamp`. Should unlock the funniest positive interaction: over-seasoning the swamp until the curse gets preserved.
- Jardinier: High Hygiene and Orientation make them excellent at reading reeds, roots, and bog paths. Helps avoid "we circled the chapel for twenty minutes" logs.
- Chef Cuistot: Salt plus swamp ingredients gives strong flavor-comedy. Can convert a bad swamp encounter into a morale snack or questionable stew.
- Quill / Sir Ledger / Comptable: Paperwork-heavy heroes should shine when chapel bureaucracy appears. They can audit the donation box, dispute a ghost invoice, or recover extra gold.
- Nell / Vex / Sable: Sneak/luck heroes benefit from trapped donation plates, sinking floorboards, and bell-rope ambushes. They should improve loot outcomes but risk pocketing evidence.
- Mira / Pippa / Morrow: Magic heroes interact with brine miracles, drowned hymn echoes, and curse residue. Morrow should get special necromancer comedy from interviewing the choir.

## Expected Special Interactions

- Salt blessing: If `elementaire_de_sel` is in the party, add a chance for a bonus positive log and slightly better loot flavor on Success or Great Success.
- Chapel bureaucracy: If any BardAccountant or Accountant is present, add a chance for bonus gold flavor on Success/Great Success; on Failure, use a line about the party being fined by a ledger.
- Holy cleanup: If Pax or Paladin is present, reduce the chance of curse/injury flavor lines and prefer "contained disaster" logs.
- Marsh navigation: If Jardinier, Hunter, Ninja, or Rogueish is present, prefer navigation/trap logs over blunt-force swamp failures.
- Bad idea demolition: If Brugg, Darrik, Orla, Expert en demolition, or Troll Stupide leads the party, allow a high-Force line where the chapel is "stabilized" by hitting the wrong support beam.
- Necromancer choir: If Morrow or Chevalier de la mort is present, unlock ghost-choir lines. These should be funny but not villainous; the undead are annoyed volunteers.
- Chef seasoning: If Chef Cuistot is present with Elementaire de sel, unlock a rare Great Success line where the swamp is declared "technically soup."
- Trait hooks:
  - `PainfullyOrganized`: Better paperwork and donation-box outcomes.
  - `ReadsManual`: Better chapel ritual outcomes.
  - `SuspiciouslyLucky`: Better loot from random floating objects.
  - `Overconfident`: Bigger comedy swings with bells, relics, and unstable planks.

## Outcome Texture

- Great Success: Party cleanses the chapel, recovers donations, and accidentally invents a new salty local holiday.
- Success: Party retrieves the reward, files enough paperwork, and leaves before the floor becomes a theological question.
- Partial Success: Reward is damp, chapel remains haunted-ish, and someone signs the wrong confession form.
- Failure: The swamp keeps most of the goods; the party returns with salt in boots, bags, and opinions.
- Ridiculous Failure: The chapel excommunicates the party by bell, mud, or invoice. Give pity gold as "refund for spiritual inconvenience."

## Possible Journal Lines

FR:

- `{hero} a sonne la cloche. Trois grenouilles ont repondu "occupe".`
- `{hero} a sale l'eau benite. Personne n'a ose corriger la recette.`
- `La chapelle a accepte nos excuses et refuse notre devis.`
- `{hero} a trouve le tronc des dons. Le tronc a demande une piece d'identite.`
- `Le choeur noye chantait faux, mais en equipe.`
- `{hero} a marche sur une planche sacree. Elle a depose plainte.`
- `Le marais a rendu les bottes. Il a garde la dignite.`
- `{hero} a rempli le formulaire de miracle. Case "peut-etre" cochee.`
- `Nous avons purifie la chapelle a 63 pour cent, ce qui est mieux que mardi.`
- `Le sel a absorbe la malediction, puis a demande des heures supplementaires.`
- `Un fantome a signe le recu avec une ecriture presque lisible.`
- `{hero} a declare le marais techniquement soupe. Le Chef a approuve trop vite.`

EN:

- `{hero} rang the bell. Three frogs replied, "busy."`
- `{hero} salted the holy water. Nobody corrected the recipe.`
- `The chapel accepted our apology and rejected our estimate.`
- `{hero} found the donation box. The box asked for identification.`
- `The drowned choir sang off-key, but as a team.`
- `{hero} stepped on a sacred plank. It filed a complaint.`
- `The swamp returned the boots. It kept the dignity.`
- `{hero} filled out the miracle form. Box "maybe" checked.`
- `We purified 63 percent of the chapel, which is better than Tuesday.`
- `The salt absorbed the curse, then requested overtime.`
- `A ghost signed the receipt in almost readable handwriting.`
- `{hero} declared the swamp technically soup. The Chef agreed too quickly.`

## Artwork Prompt

Polished 16-bit pixel art 2D mobile game quest banner, 4:1 wide landscape composition, comedic fantasy idle RPG called Badventurers, a crooked moss-covered chapel half-sunk in a green brackish salt swamp, warm candlelight glowing through broken stained glass, brass donation box floating in shallow water, reeds, salt crystals, tiny ghost choir silhouettes, suspicious paperwork nailed to a leaning door, cozy disaster mood, readable phone-screen silhouettes, crisp pixel clusters, rich warm highlights with cool blue-green shadows, moss green and brass accents, parchment UI compatibility, no readable text, no logo, no watermark, no existing franchise references.

## Implementation Notes

- This quest should work as a content-only addition once multi-quest selection exists.
- Use `salted_swamp_chapel` as the stable localization/content key.
- Prefer short journal lines. The chapel is funny because it behaves like a damp public office, not because it needs long lore.
- If only current quest fields are available, start with the recommended `Quest(...)` values and keep tags/interactions as future content metadata.
