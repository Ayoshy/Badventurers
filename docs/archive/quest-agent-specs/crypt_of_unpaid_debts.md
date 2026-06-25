# Crypt of Unpaid Debts

## Identity

- id: `crypt_of_unpaid_debts`
- Title FR: `Crypte des Dettes Impayées`
- Title EN: `Crypt of Unpaid Debts`
- Short card FR: `3 min. Risque élevé. Les morts réclament des intérêts, mais ils ont oublié où ils ont rangé les factures.`
- Short card EN: `3 min. High risk. The dead demand interest, but forgot where they filed the invoices.`

## Pitch

Sous l'ancien bureau de recouvrement municipal dort une crypte pleine de coffres, de squelettes comptables et de contrats signés avec des gens qui ne respirent plus depuis trois siècles. La guilde est engagée pour "récupérer les impayés", ce qui veut dire ouvrir des cercueils, négocier avec des créanciers morts-vivants, et repartir avant que quelqu'un remarque que Brugg a signé une reconnaissance de dette avec son bouclier.

Ton Badventurers: une expédition de donjon classique contaminée par l'administration. Les héros ne pillent pas vraiment une tombe: ils tentent de comprendre si les pièces dans les urnes sont un trésor, une succession, ou une charge déductible.

## Gameplay Tags

- `crypt`
- `undead`
- `debt`
- `paperwork`
- `gold`
- `magic`
- `trap`
- `loot`
- `high_risk`
- `bureaucracy`

## Recommended Tuning

| Field | Recommended value | Notes |
| --- | ---: | --- |
| difficulty | `290` | Palier après la première quête; demande une vraie équipe ou une amélioration de guilde. |
| risk | `High` | La crypte est dangereuse surtout à cause des clauses cachées. Target actuel: `306` avec la pénalité High. |
| baseGold | `760` | Récompense attractive pour une quête orientée argent sans casser le coût de recrutement actuel. |
| pityGold | `95` | Même un échec ridicule rapporte une "indemnité de déplacement funéraire". |
| durationSeconds | `180` | Assez longue pour sentir le palier suivant, encore courte pour boucle mobile. |
| partySlots | `4` | Encourage le recrutement et donne une vraie place aux profils Paperwork/Magic/Luck. |

Seed-data sketch:

```kotlin
Quest(
    id = "crypt_of_unpaid_debts",
    durationSeconds = 180,
    difficulty = 290,
    risk = QuestRisk.High,
    baseGold = 760,
    pityGold = 95,
    partySlots = 4,
)
```

## Heroes Who Benefit

- Quill, Sir Ledger, Comptable: gros bénéfice thématique et mécanique via `Paperwork` et `PainfullyOrganized`; ils comprennent les contrats, contestent les frais abusifs, et transforment des malédictions en lignes comptables.
- Mira, Pippa, Morrow, Élémentaire de sel: bons profils `Magic`; utiles pour lire les sceaux funéraires, calmer les registres maudits, et détecter les coffres qui respirent.
- Nell, Vex, Sable: bons profils `Luck`, `Orientation` et/ou `BadFaith`; parfaits pour trouver les "frais cachés" avant qu'ils mordent, ouvrir les niches latérales, et nier toute responsabilité.
- Pax, Paladin, Chef Cuistot: stabilisent les runs à risque; `Hygiene`, `Charisma` et organisation aident contre la poussière maudite, les esprits vexés et les buffets funéraires non déclarés.
- Morrow et Chevalier de la mort: interactions fortes avec les morts-vivants; ils peuvent négocier avec les débiteurs défunts, mais risquent aussi de sympathiser avec le service recouvrement.
- Brugg, Expert en démolition, Troll Stupide: utiles pour ouvrir les caveaux scellés si l'équipe manque de finesse; risque accru de lignes de journal absurdes et de dettes accidentelles.

## Expected Special Interactions

- `Paperwork` check: si au moins un héros a un haut score Paperwork ou la classe Accountant/BardAccountant, ajouter une chance de bonus gold ou de réduire une complication d'échec.
- `PainfullyOrganized` trait: peut convertir un Failure en Partial Success une fois par run sur cette quête, avec une ligne de journal sur un formulaire correctement rempli.
- `SuspiciouslyLucky` trait: chance d'un loot roll bonus sur Success/Great Success; faible chance de journal indiquant qu'une "nouvelle dette" a été créée par accident.
- Necromancer ou DeathKnight present: réduire le risque narratif lié aux squelettes; ajouter des lignes où les morts-vivants demandent une représentation syndicale.
- Priest ou Paladin present: bonus défensif contre les sceaux maudits; sur Great Success, possibilité d'une ligne où l'équipe rend une pièce par principe puis reprend le coffre par "frais de service".
- DemolitionExpert ou StupidTroll present: bonus potentiel pour ouvrir le coffre principal, mais sur Ridiculous Failure le groupe peut détruire l'escalier de sortie et facturer lui-même les réparations.
- Accountant/BardAccountant avec Rogueish/Ninja: combo idéal "audit furtif"; léger bonus de loot ou de gold, car personne ne sait si c'est du vol ou une correction comptable.

## Possible Journal Lines

FR:

- `{hero} a lu le contrat à voix haute. Trois squelettes ont démissionné.`
- `Le registre a soupiré, puis a demandé des intérêts.`
- `{hero} a trouvé 42 pièces dans un cercueil marqué "frais professionnels".`
- `La crypte a tenté de facturer le retour. Refusé pour vice de forme.`
- `{hero} a signé en bas de page. Mauvais bas de page.`
- `Les débiteurs morts ont accepté un échéancier. Premier paiement: jamais.`
- `Un coffre s'est ouvert après présentation d'un reçu de 300 ans.`
- `{hero} a confondu exorcisme et recouvrement. Étonnamment légal.`

EN:

- `{hero} read the contract aloud. Three skeletons resigned.`
- `The ledger sighed, then asked for interest.`
- `{hero} found 42 coins in a coffin marked "business expenses".`
- `The crypt tried to charge an exit fee. Rejected on a technicality.`
- `{hero} signed at the bottom. The wrong bottom.`
- `The dead debtors agreed to a payment plan. First payment: never.`
- `A chest opened after seeing a 300-year-old receipt.`
- `{hero} confused exorcism with debt collection. Surprisingly legal.`

## Reward Flavor

- Main reward fantasy: suspiciously recoverable gold.
- Loot flavor: rings, headgear, ledgers-as-shields, cursed payment stamps, grave-dust potions.
- Great Success: the party proves the crypt owes the guild money.
- Success: the party recovers coins and escapes with only standard haunting fees.
- Partial Success: gold recovered, but someone owes the crypt an apology or a small processing fee.
- Failure: the party returns with pity gold and a stamped rejection notice.
- Ridiculous Failure: the crypt repossesses a boot, a torch, or someone's confidence.

## Artwork Prompt

Polished 16-bit pixel art 2D mobile game quest banner, 4:1 aspect ratio, comedic fantasy idle RPG called Badventurers, underground crypt beneath a failing medieval tax office, crooked stone coffins, glowing coin piles, dusty parchment invoices, skeleton accountants holding quills and abacuses, one nervous adventurer party silhouetted at the entrance, warm lantern light against cool blue crypt shadows, moss green and brass accents, parchment UI compatibility, chunky readable phone-screen silhouettes, crisp pixel clusters, modern polish, absurd low-budget fantasy props, no readable text, no watermark, no existing franchise references.
