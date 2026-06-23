# Quest Agent Spec: Forest of Wrong Turns

## Identity

- id: `forest_of_wrong_turns`
- Title EN: `Forest of Wrong Turns`
- Title FR: `Foret des Mauvais Detours`
- One-line card EN: `A forest where every shortcut is confident, wrong, and somehow invoiced.`
- One-line card FR: `Une foret ou chaque raccourci est sur de lui, faux, et facture quand meme.`

## Pitch

The guild accepts a simple job: cross the forest, retrieve a dropped delivery crate, and do not argue with the signposts. Naturally, the signposts argue first.

This quest should feel like classic fantasy exploration filtered through Badventurers incompetence: heroic marching, suspicious moss, maps that update out of spite, and a party slowly realizing that the "wrong turn" is not a mistake but the local ecosystem.

## Gameplay Tags

- `exploration`
- `forest`
- `navigation`
- `orientation_check`
- `ambush_light`
- `foraging`
- `map_chaos`
- `luck_swing`
- `paperwork_bonus`
- `hygiene_penalty`

## Recommended Tuning

- difficulty: `175`
- risk: `Medium`
- baseGold: `520`
- pityGold: `45`
- durationSeconds: `90`
- partySlots: `3`

Balancing note: this is intended as an early-to-mid quest after `cave_minor_regrets`. It should be meaningfully riskier than the first cave, but not a hard wall. A starter party can limp through with partial successes; a party with an Orientation specialist or Paperwork specialist should feel clever.

## Hero Synergies

- Bramble: high Orientation makes him the cleanest thematic carry. He can read tracks, identify real trails, and politely ignore decorative paths designed by squirrels.
- Nell: high Luck and Orientation make her strong for shortcut rolls, hidden cache discovery, and turning getting lost into profit.
- Sable: very high Orientation and Luck make her excellent at avoiding repeated wrong turns, though she may "vanish ahead" and return with suspiciously specific mushrooms.
- Quill: strong Paperwork and Charisma let him challenge predatory signpost fees, fake tolls, and forest bylaws.
- Comptable: exceptional Paperwork and Orientation make him ideal for route permits, refund claims, and proving the party was technically never lost.
- Jardinier: high Hygiene and Orientation fit forest hazards well. He can identify aggressive vines, prune cursed hedges, and reduce thorn-related nonsense.
- Elementaire de sel: useful against damp moss, slimes, and slippery trails. Bonus flavor if the forest becomes less magical and more seasoned.
- Brugg or Troll Stupide: useful as brute-force backup for blocked paths, but their low Orientation should increase comedy events and wrong-turn chains.

## Expected Special Interactions

- Orientation specialist present: increased chance of Success or Great Success flavored as "the party followed the only trail not wearing a fake moustache."
- Paperwork specialist present: bonus gold chance from contesting illegal woodland tolls, recovering "lost path fees", or charging the signposts for emotional damages.
- Luck specialist present: chance to find a hidden picnic cache, shiny compass, or merchant who is also lost and willing to overpay for confidence.
- Hygiene-heavy hero present: reduced chance of bad mushroom, swamp boot, and suspicious pollen complications.
- Overconfident hero present: higher chance of Ridiculous Failure line where the hero chooses the "obviously heroic" path marked `Certain Doom, Scenic`.
- ReadsManual trait present: bonus journal flavor about checking the map legend, then discovering the legend has resigned.
- SuspiciouslyLucky trait present: chance to return with extra loot found in a bush that was "probably done with it."
- PainfullyOrganized trait present: chance to convert a failure into a partial success by filing a route correction form directly with the forest.
- Demolition Expert present: can open a new path, but may convert navigation problems into landscape problems.
- Gardener present with Bramble or Nell: rare Great Success flavor where the party befriends the hedge maze and gets escorted out by embarrassed shrubbery.

## Reward Flavor

Preferred loot themes:

- Bent compass that points to snacks.
- Mossy boots of almost knowing.
- Thornproof gloves with one suspicious hole.
- Receipt from a tree.
- Map fragment that labels the party as the landmark.
- Acorn-shaped charm with terrible confidence.

## Possible Journal Lines

- `{hero} followed the map. The map followed them back.`
- `{hero} asked a signpost for directions. The signpost requested a manager.`
- `{hero} found the shortcut. It led directly to a longer shortcut.`
- `{hero} marked the safe path with chalk. The forest edited it for tone.`
- `{hero} negotiated with a hedge. The hedge accepted exposure as payment.`
- `{hero} discovered three norths and chose the cheapest.`
- `{hero} accused the moss of lying. The moss had documentation.`
- `{hero} found the missing crate. It had started a small business.`
- `{hero} took a wrong turn so confidently that the forest apologized.`
- `{hero} filed a complaint against the trail. The trail filed one first.`
- `{hero} used a compass, a receipt, and panic to invent southeast.`
- `{hero} returned with the delivery, two mushrooms, and a tree's signature.`
- `The party escaped the forest by following the smell of guild debt.`
- `The forest remains undefeated, but it now owes us parking fees.`
- `Even the birds gave contradictory directions, but at least they harmonized.`

## Outcome Beats

- Great Success: the party recovers the crate, invoices the forest, and discovers a legitimate shortcut back to the guild.
- Success: the party returns with the crate and enough moss to imply effort.
- Partial Success: the party returns with most of the crate, a replacement crate, or a tree that insists it was involved.
- Failure: the party returns late, muddy, and carrying a map that now shows only emotional landmarks.
- Ridiculous Failure: the party exits the forest at the guild entrance they started from, older by ninety seconds and legally banned from one fern.

## Implementation Notes

- Suggested unlock timing: second or third quest slot, after the player has seen the first full expedition result loop.
- Best stat emphasis: Orientation first, Paperwork/Luck second, Hygiene as a small mitigation hook.
- Best UI card summary EN: `90 s. Medium risk. Bring a map, then bring someone who distrusts maps.`
- Best UI card summary FR: `90 s. Risque moyen. Apportez une carte, puis quelqu'un qui se mefie des cartes.`
- Suggested result icon: crooked signpost wrapped in moss, one arrow pointing back at itself.

## Artwork Prompt

Polished 16-bit pixel art 4:1 quest banner for a comedic fantasy idle RPG called Badventurers, enchanted forest path splitting into too many wrong turns, crooked mossy signposts pointing in contradictory directions, tiny underqualified adventurer silhouettes arguing with a map, warm golden light shafts through dense green trees, cozy disaster tone, chunky readable mobile-game silhouettes, brass and parchment friendly palette accents, subtle absurd props like a toll booth tree stump and smug mushrooms, crisp pixel clusters, modern polish, no readable text, no logo, no watermark, no existing franchise references.
