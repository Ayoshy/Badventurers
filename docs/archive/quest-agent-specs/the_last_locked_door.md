# Quest Agent Spec: The Last Locked Door

## Identity

- id: `the_last_locked_door`
- title_fr: `La Derniere Porte Verrouillee`
- title_en: `The Last Locked Door`
- content_role: Midgame capstone quest for players with a broader roster and at least one specialist.
- unlock_hint: Unlock after two medium-risk quests are cleared, or when the player owns 5+ heroes and Notice Board level 3.
- One-line card EN: `One final locked door. The key is inside. The invoice is outside.`
- One-line card FR: `Une derniere porte fermee. La cle est dedans. La facture est dehors.`

## Pitch

FR: Au fond d'un vieux donjon se trouve une porte que tout le monde appelle "la derniere", surtout parce que personne ne veut financer les suivantes. Elle possede trois serrures, deux sceaux magiques, un judas qui juge les chaussures, et un contrat indiquant que toute tentative d'ouverture vaut acceptation des conditions.

EN: Deep inside an old dungeon waits a door everyone calls "the last one", mostly because nobody wants to fund the next ones. It has three locks, two magic seals, a peephole that judges footwear, and a contract stating that any attempt to open it counts as acceptance.

Tone target: Comedic dungeon heist, cursed locksmithing, petty legal ownership, underqualified heroes trying to defeat a door that has better procedure than the guild.

## Gameplay Tags

- `dungeon`
- `locked_door`
- `lockpicking`
- `trap_heavy`
- `magic_seal`
- `paperwork`
- `puzzle`
- `heist`
- `bad_faith`
- `brute_force_risk`
- `luck_swing`
- `midgame_capstone`

## Recommended Quest Values

```kotlin
Quest(
    id = "the_last_locked_door",
    durationSeconds = 240,
    difficulty = 280,
    risk = QuestRisk.High,
    baseGold = 980,
    pityGold = 95,
    partySlots = 4,
)
```

Design notes:

- `difficulty = 280`: This should feel like a meaningful step above early-mid content. A generic party can survive, but specialists make the quest feel winnable instead of merely noisy.
- `risk = High`: The door is not a monster, which makes it funnier when it is more dangerous than one. Failure should be comedic and recoverable, not punitive.
- `baseGold = 980`: The reward should feel like a proper midgame payday from a sealed vault, especially because the quest asks for a 4-hero party and a longer timer.
- `pityGold = 95`: Enough to soften a failed 4-minute run while still making success matter.
- `durationSeconds = 240`: Four minutes supports the "serious expedition to open one stupid door" joke and gives the result screen more payoff.
- `partySlots = 4`: Encourages mixed parties: one lock specialist, one magic/paperwork specialist, one durable anchor, and one terrible idea.

## Reward Flavor

Suggested reward identity:

- Main reward: Gold from the sealed room, minus "door emotional damages" if the party loses the argument.
- Loot flavor: Keys, cursed hinges, audit stamps, anti-door tools, and valuables that were clearly stored behind the door to avoid talking about them.
- Rare loot ideas:
  - `Key That Refuses Closure` - Trinket, Luck/Paperwork.
  - `Hinge Of Final Opinions` - Armor, Endurance/BadFaith.
  - `Lockpick With A Receipt` - Weapon, Sneak/Luck.
  - `Vault Dust, Premium Grade` - Trinket, Magic/Hygiene.
  - `Doormat Of Conditional Entry` - Armor, Charisma/Paperwork.

## Heroes Who Benefit

- Nell: High Luck and Orientation make her the safest early lock specialist. She should find false keyholes, trap catches, and "totally unrelated" coin piles.
- Vex: Excellent Luck, Sneak-adjacent identity, and very high Orientation make Vex ideal for trap-heavy door logic. Expect stylish bypass logs and suspicious bonus loot.
- Sable: Very high Orientation and Luck fit silent scouting, ceiling access, and avoiding the door's most insulting traps.
- Mira: High Magic and Paperwork are perfect for reading the fine print on arcane seals. She should reduce cursed-lock complications and produce manual-check comedy.
- Pippa: Strong Magic, Luck, and Paperwork make her the best high-upside seal breaker. She can turn a dangerous magic failure into an accidental bonus.
- Morrow: Very high Magic and Paperwork let him interrogate the door's previous victims or the lock's lingering spite. Great for spooky-but-office-like logs.
- Quill: Top-tier Paperwork and Charisma let him dispute the door's terms, identify forged ownership clauses, and convert success into bonus gold flavor.
- Sir Ledger: Legendary Paperwork gives the cleanest "we legally opened it" route. He can turn the door's contract against itself.
- Comptable: Exceptional Paperwork and good Orientation make him ideal for inventorying keys, permits, hinge depreciation, and unpaid vault rent.
- Paladin: Good Endurance, Force, Hygiene, and Paperwork make him the safest anchor when the door becomes sacred, cursed, or loudly disappointed.
- Chevalier de la mort: Force, Magic, Endurance, and BadFaith make him strong against cursed threshold effects. Should unlock lines where death recognizes the door and refuses to queue.
- Brugg, Orla, Expert en demolition, or Troll Stupide: High Force can solve a lock very directly, but should add risk of damaged loot, louder traps, and the door winning on technicality.
- Elementaire de sel: Useful against rusted hinges, damp mechanisms, and weird vault preservation. Bonus comedy when the lock becomes "seasoned but still locked."

## Expected Special Interactions

- Lock specialist present: If Nell, Vex, or Sable is in the party, increase chance of Success/Great Success flavor around false keyholes, hidden catches, and trap bypasses.
- Paperwork specialist present: If Quill, Sir Ledger, or Comptable is present, add bonus gold flavor from invalidating the door's late fees, ownership clause, or "entry tax."
- Magic seal breaker present: If Mira, Pippa, Morrow, Paladin, or Chevalier de la mort is present, reduce curse-flavored failure lines and prefer seal negotiation or ritual debugging.
- Brute-force opener present: If Brugg, Orla, Expert en demolition, or Troll Stupide leads the party, allow faster, louder opening flavor with a chance to downgrade reward flavor due to crushed contents.
- Paladin anchor: If Paladin is present, the door's moral judgement should become less dangerous and more bureaucratic.
- Necromantic witness: If Morrow or Chevalier de la mort is present, unlock lines where previous adventurers testify that the door was "like this when we died."
- Salted hinge: If Elementaire de sel is present, add chance for a positive log about removing rust, followed by a negative log about adding personality.
- Chef Cuistot side hook: If Chef Cuistot is present, the party may identify the final room as a pantry, improving morale while making the treasure smell like onions.
- Trait hooks:
  - `PainfullyOrganized`: Better key sorting, contract handling, and vault inventory outcomes.
  - `ReadsManual`: Better magical seal and lock mechanism outcomes; may discover the manual is locked inside.
  - `SuspiciouslyLucky`: Extra loot from random key bowls, doormats, or a trap that drops coins when offended.
  - `Overconfident`: Higher chance of dramatic wrong-key attempts, kicked doors, and signing the door's waiver by accident.

## Outcome Texture

- Great Success: The party opens the door legally, magically, and physically, then invoices the vault for wasting everyone's time.
- Success: The party opens the door, retrieves the reward, and leaves before the door can appeal.
- Partial Success: The door opens halfway, enough for gold, one arm, and several complaints.
- Failure: The party returns with bent lockpicks, a stamped denial, and the strong suspicion that the door smirked.
- Ridiculous Failure: The door remains locked, but the party accidentally unlocks the hallway behind them and must pay an exit convenience fee.

## Possible Journal Lines

FR:

- `{hero} a crochete la premiere serrure. La deuxieme a applaudi par sarcasme.`
- `{hero} a lu le contrat de la porte. La porte a demande une pause syndicale.`
- `{hero} a trouve la cle. Elle etait verrouillee dans une petite boite encore plus fiere.`
- `La porte a exige un mot de passe. Le groupe a propose "s'il vous plait". Refuse.`
- `{hero} a frappe la porte. La porte a gagne aux points.`
- `{hero} a graisse les gonds. Les gonds ont commence a parler d'eux-memes.`
- `Le sceau magique disait "ne pas toucher". Nous avons touche administrativement.`
- `{hero} a classe la porte comme meuble hostile. Ca a tenu au tribunal.`
- `Le coffre derriere la porte contenait de l'or, une charniere, et une excuse non signee.`
- `{hero} a ouvert la mauvaise serrure si bien qu'elle est devenue la bonne.`
- `La porte est restee fermee, mais elle nous a rembourse le deplacement par honte.`
- `{hero} a trouve une trappe sous le paillasson. Le paillasson a demande un pourboire.`

EN:

- `{hero} picked the first lock. The second one clapped sarcastically.`
- `{hero} read the door's contract. The door requested a union break.`
- `{hero} found the key. It was locked inside a smaller, prouder box.`
- `The door demanded a password. The party tried "please." Rejected.`
- `{hero} kicked the door. The door won on points.`
- `{hero} oiled the hinges. The hinges began talking about themselves.`
- `The magic seal said "do not touch." We touched it administratively.`
- `{hero} classified the door as hostile furniture. It held up in court.`
- `The vault held gold, one hinge, and an unsigned apology.`
- `{hero} opened the wrong lock so well it became the right one.`
- `The door stayed shut, but refunded our travel out of shame.`
- `{hero} found a trapdoor under the doormat. The doormat requested a tip.`

## Implementation Notes

- Suggested unlock timing: after `salted_swamp_chapel` or another medium-risk quest, once the player has seen specialist value in party prep.
- Best stat emphasis: Sneak/Luck/Orientation for physical access, Paperwork for legal bypass and bonus gold, Magic for seal mitigation, Force as a high-risk shortcut.
- Best UI card summary EN: `240 s. High risk. Bring lockpicks, forms, and someone the door respects.`
- Best UI card summary FR: `240 s. Risque eleve. Apportez des crochets, des formulaires, et quelqu'un que la porte respecte.`
- Suggested result icon: heavy brass keyhole inside a smug stone door, with one tiny red wax seal.
- Use `the_last_locked_door` as the stable localization/content key.
- If only current quest fields are available, start with the recommended `Quest(...)` values and keep special interactions as future metadata.

## Artwork Prompt

Polished 16-bit pixel art 2D mobile game quest banner, 4:1 wide landscape composition, comedic fantasy idle RPG called Badventurers, massive ancient stone dungeon door with three oversized brass locks and glowing magical seals, tiny underqualified adventurer silhouettes arguing with lockpicks, forms, and a too-large key, warm torchlight, moss green shadows, brass highlights, parchment-friendly palette, cozy disaster mood, readable phone-screen silhouettes, crisp pixel clusters, subtle comic props like a judgemental peephole and suspicious doormat, no readable text, no letters, no numbers, no logo, no watermark, no existing franchise references.
