# Quest Agent Spec: Moonlit Smuggler Run

## Identity

- id: `moonlit_smuggler_run`
- Title EN: Moonlit Smuggler Run
- Title FR: Course des contrebandiers au clair de lune
- Content role: early-mid quest, unlocked after the first repeatable expedition and ideally after the player has seen loot/recruitment once.

## Pitch

The guild is hired to move a cart of "perfectly legal moon-stuff" across the old river road before sunrise. The cargo is suspicious, the client is wearing three fake moustaches, and the only map is printed on the back of a tax warning. Success means fast gold and odd loot. Failure means the heroes accidentally deliver the crates to customs, then ask customs for a receipt.

FR tone target:

Une course nocturne pour livrer des caisses "legalement lunaires" avant l'aube. Le client a trois fausses moustaches, la route est pleine de gardes mal reveilles, et la guilde n'a toujours pas compris si elle aide des contrebandiers ou le service des objets trouves.

## Gameplay Tags

- `night`
- `smuggling`
- `escort`
- `stealth`
- `road`
- `river`
- `paperwork`
- `loot_heavy`
- `medium_risk`
- `early_mid_game`

## Recommended Tuning

- difficulty: `225`
- risk: `Medium`
- baseGold: `560`
- pityGold: `45`
- durationSeconds: `180`
- durationLabel: `3 min`
- partySlots: `3`

Rationale:

- This sits above `cave_minor_regrets` (`difficulty=120`, `Low`, `baseGold=320`) without becoming a hard wall.
- Medium risk adds the current `+8` target penalty, making the practical target `233`.
- A starter trio around 200 party power can still attempt it, but wants gear, upgrades, or a better-fit recruit for consistent success.
- `baseGold=560` makes the quest feel meaningfully more profitable while staying below a premium or late-game payout.

## Heroes Who Benefit

- Nell: high Luck, Orientation, and BadFaith make her ideal for finding shortcuts, lying to guards, and somehow returning with one extra crate.
- Vex: stronger Rogueish variant for later rosters; should feel like the premium answer to stealth and smuggling tags.
- Sable: Ninja kit fits night routes, silent detours, and smoke-invoice humor.
- Bramble: high Orientation and exploration flavor help navigate river roads, missing signs, and suspicious moonlit trails.
- Quill: PainfullyOrganized plus high Paperwork can turn a smuggling run into a "mobile inventory audit" for gold or reduced failure penalties.
- Comptable: high Paperwork and Orientation make them great at forged receipts, route permits, and making customs owe the guild money.
- Sir Ledger: legendary administrative overkill; should create the funniest version of success by making contraband fiscally compliant.
- Mira: high Magic and Paperwork can stabilize moonlit wards, read the bad map, or illuminate the road at the wrong but useful moment.
- Pippa: magic plus luck makes her good at chaotic route fixes and lucky crate outcomes.
- Brugg: useful as a loud distraction if the party also has stealth or paperwork support; alone, his Ego should increase funny complication odds.

## Expected Special Interactions

- Rogueish or Ninja present: add a chance for a stealth-flavored journal line and optionally +1 loot roll on Great Success.
- PainfullyOrganized or high Paperwork hero present: add a "forged receipt" line and optionally reduce Failure to Partial Success once per run if the margin is close.
- Hunter or high Orientation hero present: add a shortcut line and optionally reduce duration by a small percentage in a future implementation.
- ApprenticeMage present: add a moonlight magic line; on Great Success it helps, on Failure it signals the guards beautifully.
- Bruiser, StupidTroll, or Overconfident lead hero: add a loud-distraction line; can be positive with a stealth/admin teammate, risky without one.
- SaltElemental present: add a salted-road or preserved-contraband line; good fit for absurd item rewards.
- BardAccountant or Accountant present: convert part of the route into an invoice joke; potential small gold bonus in future content tuning.
- If all selected heroes have low Orientation flavor: use a wrong-destination line where the party almost delivers the cargo back to the guild.

## Possible Journal Lines

EN:

- "The cart was quiet. The heroes were not."
- "{hero} forged a receipt so official the guards apologized to it."
- "{hero} found a shortcut. It was mostly river."
- "The moon lit the road, the crates, and several crimes."
- "{hero} distracted customs by asking where to declare a cursed onion."
- "One crate hummed. The party agreed not to learn why."
- "The delivery arrived before dawn, technically at the right building."
- "A guard inspected the cargo. The cargo inspected back."
- "{hero} hid the cart behind a smaller cart. Nobody was proud, but it worked."
- "The client paid in gold and refused to take back the moustaches."

FR:

- "La charrette etait discrete. Les heros, moins."
- "{hero} a forge un recu si officiel que les gardes lui ont presente des excuses."
- "{hero} a trouve un raccourci. C'etait surtout une riviere."
- "La lune eclairait la route, les caisses, et plusieurs delits."
- "{hero} a distrait la douane en demandant ou declarer un oignon maudit."
- "Une caisse bourdonnait. Le groupe a vote pour ne pas savoir pourquoi."
- "La livraison est arrivee avant l'aube, techniquement au bon batiment."
- "Un garde a inspecte la cargaison. La cargaison a inspecte en retour."
- "{hero} a cache la charrette derriere une plus petite charrette. Personne n'etait fier, mais ca a marche."
- "Le client a paye en or et a refuse de reprendre les moustaches."

## Reward And Loot Flavor

- Gold theme: fast cash, suspicious coins, emergency delivery fee.
- Loot theme: road contraband, fake permits, moonlit trinkets, smuggler tools.
- Suggested item hooks:
- `Permit of Very Legal Intentions`
- `Moon-Dented Lantern`
- `Boots of Quietly Panicking`
- `Crate Key, Probably`
- `Customs-Proof Moustache`

## Failure Flavor

- Failure should be funny, not punitive: the heroes survive, earn pity gold, and return with a receipt, a fine, or one legal crate they did not leave with.
- Ridiculous Failure should imply bureaucratic disaster rather than violence: customs hires the party as witnesses, the cart follows someone else home, or the moon "recognizes" the cargo.

## Artwork Prompt

Polished 16-bit pixel art 4:1 horizontal banner for a comedic fantasy idle RPG called Badventurers, moonlit river road at night, underqualified adventurers escorting a rickety smuggler cart full of suspicious glowing crates, sleepy customs guards in the distance, crooked signposts, fake moustaches, paperwork flying from the cart, cozy disaster humor, cool blue moon shadows with warm lantern highlights, moss green and brass accents, chunky readable silhouettes for mobile, premium pixel clusters, modern polish, no readable text, no logo, no watermark, no existing franchise references.
