# Hero And Item Catalog

Last updated: 2026-06-27

This is the working registry for hero and item content. When a hero, item, rarity, stat profile, evolution rule, or art assignment changes, update this document in the same change as the implementation.

Current implementation sources:

- Heroes: `app/src/main/java/com/ayoshy/badventurers/game/HeroGacha.kt`
- Hero growth: `app/src/main/java/com/ayoshy/badventurers/game/HeroProgression.kt`
- Hero specials: `app/src/main/java/com/ayoshy/badventurers/game/HeroSpecialCatalog.kt`
- Hero portraits: `app/src/main/java/com/ayoshy/badventurers/ui/BadventurersUiArt.kt`
- Items: `app/src/main/java/com/ayoshy/badventurers/game/Loot.kt`
- Spreadsheet mirrors: `docs/data/heroes.csv` and `docs/data/items.csv`

## Stat Legend

| Short | Stat |
| --- | --- |
| For | Force |
| Mag | Magic |
| Lck | Luck |
| Ego | Ego |
| Hyg | Hygiene |
| BF | Bad Faith |
| Ori | Orientation |
| Pap | Paperwork |
| End | Endurance |
| Cha | Charisma |

## Hero Growth Rules

Each level-up currently grants:

- `+2` class primary stat.
- `+1` class secondary stat.
- `+1` trait stat.
- `+1` rotating stat: class tertiary stat on even levels, hero quirk stat on odd levels.
- Rare or better heroes gain `+1` class primary stat on levels divisible by 3.
- Epic or better heroes gain `+1` class secondary stat on levels divisible by 4.
- Legendary heroes gain `+1` trait stat on levels divisible by 5.

The hero table lists the exact growth profile inputs as `P`, `S`, `T`, `Trait`, and `Odd`.

Example: Brugg has `P=For`, `S=End`, `T=Ego`, `Trait=Ego`, and `Odd=BF`. On level 4 he gains `+2 For`, `+1 End`, `+1 Ego` from trait, and `+1 Ego` from even-level tertiary. On level 5 he gains `+2 For`, `+1 End`, `+1 Ego` from trait, and `+1 BF` from odd-level quirk.

Hero gacha rarity weights are currently Common `55`, Uncommon `25`, Rare `13`, Epic `6`, Legendary `1`. Starter heroes are the first three catalog entries: Brugg, Mira, and Nell.

## Hero Creation Rules

A hero is no longer content-complete with only a portrait and catalog row. Any new hero, hero skin, or major hero artwork replacement must also plan and track a Live-Lite animation pack.

Minimum animation states for an accepted hero pack:

- `idle`: readable standing loop for roster/prep/live pauses.
- `walk`: side-view expedition movement loop.
- `fight`: short attack or effort loop for route hazards.
- `hurt` or `dead`: failure/downed state for bad outcomes.
- `celebrate`: success or great-success reaction.
- `loot` or `interact`: reward/search/paperwork interaction beat.

Until the animation pipeline exists, every hero row should keep its portrait assignment and note whether animation coverage is missing, planned, or accepted. Final animation assets should use stable hero IDs and state names so Live-Lite can swap from placeholders to real hero artwork without changing gameplay logic.

## Heroes

| ID | Artwork | Animation Pack | Class | Name | Rarity | Start Level | Trait | Special | Stats Distribution | Per-Level Growth Profile | Evolution / Design Notes |
| --- | --- | --- | --- | --- | ---: | --- | --- | --- | --- | --- | --- |
| brugg | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_brugg.png` | Planned (BV-044) | Bruiser | Brugg | Common | 3 | Overconfident | RamshackleCharge | For95 Mag8 Lck36 Ego92 Hyg20 BF64 Ori38 Pap10 End88 Cha45 | P=For; S=End; T=Ego; Trait=Ego; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| mira | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_mira.png` | Planned (BV-044) | ApprenticeMage | Mira | Uncommon | 2 | ReadsManual | GlyphReader | For16 Mag90 Lck48 Ego54 Hyg72 BF38 Ori44 Pap76 End32 Cha52 | P=Mag; S=Pap; T=Lck; Trait=Pap; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| nell | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_nell.png` | Planned (BV-044) | Rogueish | Nell | Uncommon | 2 | SuspiciouslyLucky | LightFingers | For34 Mag28 Lck88 Ego58 Hyg46 BF74 Ori84 Pap26 End48 Cha62 | P=Lck; S=Ori; T=BF; Trait=Lck; Odd=Pap; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| darrik | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_darrik.png` | Missing (BV-045) | Bruiser | Darrik | Common | 1 | Overconfident | HumanWall | For80 Mag10 Lck30 Ego78 Hyg26 BF58 Ori35 Pap12 End72 Cha40 | P=For; S=End; T=Ego; Trait=Ego; Odd=Pap; Extras=none | Unique portrait added for BV-030. Promotion ranks 0-4 planned. |
| quill | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_quill.png` | Missing (BV-045) | Bard | Quill | Rare | 2 | PainfullyOrganized | AggressiveMinutes | For28 Mag42 Lck55 Ego64 Hyg70 BF52 Ori58 Pap96 End44 Cha82 | P=Cha; S=Mag; T=Lck; Trait=Pap; Odd=Mag; Extras=+P at level multiples of 3 | Class corrected from BardAccountant to Bard. Promotion ranks 0-4 planned; branching evolution TBD. |
| orla | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_orla.png` | Missing (BV-045) | Bruiser | Orla | Rare | 2 | ReadsManual | TerrainManual | For92 Mag22 Lck45 Ego66 Hyg50 BF48 Ori52 Pap35 End86 Cha48 | P=For; S=End; T=Ego; Trait=Pap; Odd=For; Extras=+P at level multiples of 3 | Unique portrait added for BV-030. Promotion ranks 0-4 planned. |
| pippa | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_pippa.png` | Missing (BV-045) | ApprenticeMage | Pippa | Epic | 3 | SuspiciouslyLucky | UnstableLuck | For18 Mag104 Lck74 Ego62 Hyg78 BF42 Ori64 Pap72 End42 Cha70 | P=Mag; S=Pap; T=Lck; Trait=Lck; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | BV-030 replacement portrait: storm-calligrapher mage silhouette, distinct from Mira. Promotion ranks 0-4 planned. |
| vex | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_vex.png` | Missing (BV-045) | Rogueish | Vex | Epic | 3 | SuspiciouslyLucky | DirtyJackpot | For42 Mag44 Lck96 Ego68 Hyg54 BF86 Ori98 Pap24 End60 Cha64 | P=Lck; S=Ori; T=BF; Trait=Lck; Odd=Cha; Extras=+P at level multiples of 3; +S at level multiples of 4 | BV-030 replacement portrait: casino-duelist silhouette, distinct from Nell. Promotion ranks 0-4 planned. |
| bramble | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_hunter.png` | Missing (BV-045) | Hunter | Bramble | Common | 1 | ReadsManual | FreshTrail | For64 Mag24 Lck58 Ego34 Hyg48 BF36 Ori94 Pap28 End76 Cha40 | P=Ori; S=End; T=Lck; Trait=Pap; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| pax | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_priest.png` | Missing (BV-045) | Priest | Pax | Uncommon | 2 | PainfullyOrganized | CleanBlessing | For24 Mag78 Lck70 Ego38 Hyg92 BF22 Ori56 Pap66 End62 Cha76 | P=Hyg; S=Mag; T=Cha; Trait=Pap; Odd=Cha; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| sable | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_ninja.png` | Missing (BV-045) | Ninja | Sable | Rare | 2 | SuspiciouslyLucky | NoTrace | For48 Mag48 Lck82 Ego74 Hyg64 BF78 Ori106 Pap32 End66 Cha58 | P=Ori; S=Lck; T=BF; Trait=Lck; Odd=Cha; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| morrow | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_necromancer.png` | Missing (BV-045) | Necromancer | Morrow | Epic | 3 | Overconfident | NecroLever | For18 Mag110 Lck52 Ego86 Hyg30 BF70 Ori60 Pap88 End46 Cha42 | P=Mag; S=BF; T=Pap; Trait=Ego; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | Promotion ranks 0-4 planned; branching evolution TBD. |
| ledger | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_comptable.png` | Missing (BV-045) | BardAccountant | Sir Ledger | Legendary | 4 | PainfullyOrganized | HostileAudit | For52 Mag82 Lck74 Ego90 Hyg86 BF64 Ori76 Pap120 End70 Cha92 | P=Pap; S=Cha; T=Lck; Trait=Pap; Odd=Pap; Extras=+P at level multiples of 3; +S at level multiples of 4; +Trait at level multiples of 5 | Original accountant portrait restored to Sir Ledger for BV-030. Promotion ranks 0-4 planned. |
| paladin | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_paladin.png` | Missing (BV-045) | Paladin | Sir Brindle | Legendary | 4 | ReadsManual | UnbreakableOath | For88 Mag54 Lck42 Ego66 Hyg70 BF18 Ori60 Pap84 End92 Cha78 | P=End; S=Hyg; T=For; Trait=Pap; Odd=Cha; Extras=+P at level multiples of 3; +S at level multiples of 4; +Trait at level multiples of 5 | Promotion ranks 0-4 planned; branching evolution TBD. |
| comptable | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_tally_noakes.png` | Missing (BV-045) | Quartermaster | Tally Noakes | Rare | 2 | PainfullyOrganized | BalancedBooks | For18 Mag32 Lck60 Ego64 Hyg78 BF30 Ori86 Pap104 End40 Cha54 | P=Ori; S=Pap; T=End; Trait=Pap; Odd=Mag; Extras=+P at level multiples of 3 | Retagged as a unique Quartermaster to match the field-audit marshal skin with ledger-shield and stamp hammer. Promotion ranks 0-4 planned. |
| jardinier | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_jardinier.png` | Missing (BV-045) | Gardener | Moss Fenlow | Common | 1 | ReadsManual | GreenThumb | For46 Mag20 Lck70 Ego36 Hyg88 BF24 Ori92 Pap26 End68 Cha44 | P=Hyg; S=Ori; T=End; Trait=Pap; Odd=Lck; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| chevalier_de_la_mort | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_chevalier_de_la_mort.png` | Missing (BV-045) | DeathKnight | Grave Odo | Epic | 3 | Overconfident | DeathDiscount | For74 Mag84 Lck40 Ego70 Hyg16 BF82 Ori50 Pap62 End76 Cha36 | P=End; S=Mag; T=BF; Trait=Ego; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | Promotion ranks 0-4 planned; branching evolution TBD. |
| chef_cuistot | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_chef_cuistot.png` | Missing (BV-045) | Chef | Basil Bouillon | Uncommon | 2 | PainfullyOrganized | MoraleRations | For40 Mag26 Lck58 Ego48 Hyg94 BF22 Ori64 Pap72 End60 Cha84 | P=Hyg; S=Cha; T=End; Trait=Pap; Odd=For; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| expert_en_demolition | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_expert_en_demolition.png` | Missing (BV-045) | DemolitionExpert | Nix Powderkeg | Rare | 2 | Overconfident | PlanBExplosives | For98 Mag20 Lck38 Ego74 Hyg26 BF84 Ori34 Pap24 End92 Cha42 | P=For; S=BF; T=End; Trait=Ego; Odd=Mag; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| elementaire_de_sel | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_elementaire_de_sel.png` | Missing (BV-045) | SaltElemental | Salina Brine | Uncommon | 3 | SuspiciouslyLucky | PreservationSalt | For18 Mag84 Lck74 Ego38 Hyg92 BF28 Ori76 Pap46 End54 Cha40 | P=Mag; S=Hyg; T=Lck; Trait=Lck; Odd=End; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| troll_stupide | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_troll_stupide.png` | Missing (BV-045) | StupidTroll | Grum | Common | 1 | Overconfident | CreativeMisunderstanding | For100 Mag12 Lck54 Ego44 Hyg16 BF52 Ori28 Pap10 End100 Cha34 | P=For; S=End; T=BF; Trait=Ego; Odd=End; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |

## Hero Evolution / Promotion Notes

Current promotion planning lives in `../design/hero-promotion.md`. The current planned MVP promotion package is:

- Promotion rank starts at `0` and caps at `4`.
- Each promotion rank grants `+3` class primary stat, `+2` class secondary stat, and `+1` trait stat.
- Duplicate pulls are planned to produce hero contracts for the matching hero id.
- Blank contracts may come later from achievements, events, or Training Yard milestones.

Open design space for this catalog:

- Add hero-specific evolution names, such as class title upgrades.
- Decide whether evolution is purely promotion rank, a branching choice, or a cosmetic/title layer on top of rank.
- Decide whether rarity can ever change, or whether rarity is fixed identity and evolution is separate.
- Add per-hero authored evolution notes once the first hero-specific promotion pass starts.

## Item Rules

Items use fixed-rarity catalog definitions. `LootGenerator` rolls a rarity from the active profile, then chooses an item from that rarity pool. Existing items are assigned to Common so higher-rarity drops can introduce distinct names and eventual distinct artwork instead of upgrading the same object by math only.

Current item rarity profiles are progression-gated in `LootGenerator`:

- Base profile before the Palier 2 gate: Common `75`, Uncommon `25`.
- Palier 2 Rare loot profile after `8` completed quests: Common `65`, Uncommon `25`, Rare `10`.
- Epic item definitions can now roll from the Palier 3 profile after 13 completed quests; Relic definitions remain authored for later unlock profiles.

| Rarity | Catalog Count | Stat Slots | Art Status |
| --- | ---: | ---: | --- |
| Common | 25 | 1 | Accepted BV-024 PNG art in app resources. |
| Uncommon | 8 | 2 | Accepted BV-025 PNG art in app resources. |
| Rare | 8 | 3 | Accepted BV-025 PNG art in app resources. |
| Epic | 8 | 4 | Accepted BV-026 PNG art in app resources. |
| Relic | 8 | 5 | Accepted BV-026 PNG art in app resources. |

Each stat slot chooses a unique stat type and a weighted value from `1` to `10`. Item stat values are flat bonuses; items do not level up yet.

The rejected generated XML placeholder batch is not accepted item artwork. Common, Uncommon, Rare, Epic, and Relic items now render through accepted item-specific PNGs under `app/src/main/res-loot/drawable-nodpi`; the older `LootIcon` family remains fallback coverage only for future unaccepted items. Resource existence alone must not be treated as art acceptance.

## Items

| ID | Artwork Status | Slot | Name | Fixed Rarity | Stats Distribution | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| weapon_bent_spoon | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_bent_spoon.png` | Weapon | Bent Spoon | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Spoon` remains fallback coverage. |
| weapon_fork_spear | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_fork_spear.png` | Weapon | Fork Spear | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Weapon` remains fallback coverage. |
| weapon_moon_axe | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_moon_axe.png` | Weapon | Moon Axe | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Weapon` remains fallback coverage. |
| weapon_nibblade | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_nibblade.png` | Weapon | Nibblade | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Blade` remains fallback coverage. |
| weapon_toast_mace | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_toast_mace.png` | Weapon | Toast Spoon | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Spoon` remains fallback coverage. |
| armor_patch_hood | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_patch_hood.png` | Armor | Patch Cloak | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Hood` remains fallback coverage. |
| armor_moss_coat | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_moss_coat.png` | Armor | Moss Coat | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Hood` remains fallback coverage. |
| armor_winged_boots | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_winged_boots.png` | Footwear | Winged Boots | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Boots` remains fallback coverage. |
| armor_travel_boots | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_travel_boots.png` | Footwear | Travel Boots | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Boots` remains fallback coverage. |
| trinket_lucky_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_lucky_ring.png` | Trinket | Lucky Ring | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Ring` remains fallback coverage. |
| trinket_pocket_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_pocket_ring.png` | Trinket | Pocket Ring | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Ring` remains fallback coverage. |
| trinket_spare_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_spare_ring.png` | Trinket | Spare Ring | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Ring` remains fallback coverage. |
| trinket_dusty_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_dusty_ring.png` | Trinket | Dusty Ring | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Ring` remains fallback coverage. |
| trinket_quiet_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_quiet_ring.png` | Trinket | Quiet Ring | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Ring` remains fallback coverage. |
| armor_panic_helm | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_panic_helm.png` | Headgear | Panic Helm | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Helmet` remains fallback coverage. |
| headgear_soup_helm | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_soup_helm.png` | Headgear | Soup Helm | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Helmet` remains fallback coverage. |
| headgear_wobble_cap | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_wobble_cap.png` | Headgear | Wobble Cap | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Hood` remains fallback coverage. |
| headgear_paper_crown | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_paper_crown.png` | Headgear | Paper Crown | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Helmet` remains fallback coverage. |
| headgear_lantern_hat | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_lantern_hat.png` | Headgear | Lantern Hat | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Helmet` remains fallback coverage. |
| headgear_grin_hood | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_grin_hood.png` | Headgear | Grin Hood | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Hood` remains fallback coverage. |
| consumable_stale_potion | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_stale_potion.png` | Consumable | Stale Potion | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Potion` remains fallback coverage. |
| consumable_brave_brew | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_brave_brew.png` | Consumable | Brave Brew | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Tankard` remains fallback coverage. |
| consumable_tiny_flask | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_tiny_flask.png` | Consumable | Tiny Flask | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Potion` remains fallback coverage. |
| consumable_odd_elixir | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_odd_elixir.png` | Consumable | Odd Elixir | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Potion` remains fallback coverage. |
| consumable_snap_tonic | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_snap_tonic.png` | Consumable | Snap Tonic | Common | 1 generated unique stat(s) | Runtime renders accepted BV-024 Common item art; legacy `Potion` remains fallback coverage. |
| weapon_receipt_cutter | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_receipt_cutter.png` | Weapon | Receipt Cutter | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Blade` remains fallback coverage. |
| weapon_mop_halberd | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_mop_halberd.png` | Weapon | Mop Halberd | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Weapon` remains fallback coverage. |
| armor_buttoned_barrel | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_buttoned_barrel.png` | Armor | Buttoned Barrel | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Hood` remains fallback coverage. |
| footwear_squeaky_greaves | `app/src/main/res-loot/drawable-nodpi/loot_art_footwear_squeaky_greaves.png` | Footwear | Squeaky Greaves | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Boots` remains fallback coverage. |
| trinket_queue_token | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_queue_token.png` | Trinket | Queue Token | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Ring` remains fallback coverage. |
| headgear_bucket_visor | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_bucket_visor.png` | Headgear | Bucket Visor | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Helmet` remains fallback coverage. |
| consumable_warmish_tea | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_warmish_tea.png` | Consumable | Warmish Tea | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Tankard` remains fallback coverage. |
| consumable_pickle_potion | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_pickle_potion.png` | Consumable | Pickle Potion | Uncommon | 2 generated unique stat(s) | Runtime renders accepted BV-025 Uncommon item art; legacy `Potion` remains fallback coverage. |
| weapon_fine_print_rapier | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_fine_print_rapier.png` | Weapon | Fine Print Rapier | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Blade` remains fallback coverage. |
| weapon_taxman_gavel | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_taxman_gavel.png` | Weapon | Taxman Gavel | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Weapon` remains fallback coverage. |
| armor_invoice_mail | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_invoice_mail.png` | Armor | Invoice Mail | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Hood` remains fallback coverage. |
| footwear_witness_slippers | `app/src/main/res-loot/drawable-nodpi/loot_art_footwear_witness_slippers.png` | Footwear | Witness Slippers | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Boots` remains fallback coverage. |
| trinket_overtime_hourglass | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_overtime_hourglass.png` | Trinket | Overtime Hourglass | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Ring` remains fallback coverage. |
| headgear_notary_wig | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_notary_wig.png` | Headgear | Notary Wig | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Hood` remains fallback coverage. |
| consumable_bottled_courage | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_bottled_courage.png` | Consumable | Bottled Courage | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Potion` remains fallback coverage. |
| trinket_minor_oracle_receipt | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_minor_oracle_receipt.png` | Trinket | Minor Oracle Receipt | Rare | 3 generated unique stat(s) | Runtime renders accepted BV-025 Rare item art; legacy `Ring` remains fallback coverage. |
| weapon_auditors_halberd | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_auditors_halberd.png` | Weapon | Auditors Halberd | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Weapon` remains fallback coverage. |
| weapon_dragon_stamp | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_dragon_stamp.png` | Weapon | Dragon Stamp | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Weapon` remains fallback coverage. |
| armor_misfiled_aegis | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_misfiled_aegis.png` | Armor | Misfiled Aegis | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Hood` remains fallback coverage. |
| footwear_plausible_denial_boots | `app/src/main/res-loot/drawable-nodpi/loot_art_footwear_plausible_denial_boots.png` | Footwear | Boots of Plausible Denial | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Boots` remains fallback coverage. |
| trinket_bell_of_last_call | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_bell_of_last_call.png` | Trinket | Bell of Last Call | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Ring` remains fallback coverage. |
| headgear_emergency_minutes_crown | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_emergency_minutes_crown.png` | Headgear | Crown of Emergency Minutes | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Helmet` remains fallback coverage. |
| consumable_second_chance_soup | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_second_chance_soup.png` | Consumable | Soup of Second Chances | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Tankard` remains fallback coverage. |
| trinket_contract_knot | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_contract_knot.png` | Trinket | Contract Knot | Epic | 4 generated unique stat(s) | Runtime renders accepted BV-026 Epic item art; legacy `Ring` remains fallback coverage. |
| weapon_spoon_final_notice | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_spoon_final_notice.png` | Weapon | Spoon of Final Notice | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Spoon` remains fallback coverage. |
| weapon_moonlit_receipt_cleaver | `app/src/main/res-loot/drawable-nodpi/loot_art_weapon_moonlit_receipt_cleaver.png` | Weapon | Moonlit Receipt Cleaver | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Blade` remains fallback coverage. |
| armor_many_signatures_cloak | `app/src/main/res-loot/drawable-nodpi/loot_art_armor_many_signatures_cloak.png` | Armor | Cloak of Many Signatures | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Hood` remains fallback coverage. |
| footwear_inevitable_return_sandals | `app/src/main/res-loot/drawable-nodpi/loot_art_footwear_inevitable_return_sandals.png` | Footwear | Sandals of Inevitable Return | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Boots` remains fallback coverage. |
| trinket_perpetual_queue_ring | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_perpetual_queue_ring.png` | Trinket | Ring of Perpetual Queue | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Ring` remains fallback coverage. |
| headgear_halo_compliance | `app/src/main/res-loot/drawable-nodpi/loot_art_headgear_halo_compliance.png` | Headgear | Halo of Compliance | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Helmet` remains fallback coverage. |
| consumable_absolute_maybe_elixir | `app/src/main/res-loot/drawable-nodpi/loot_art_consumable_absolute_maybe_elixir.png` | Consumable | Elixir of Absolute Maybe | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Potion` remains fallback coverage. |
| trinket_unpaid_charter_seal | `app/src/main/res-loot/drawable-nodpi/loot_art_trinket_unpaid_charter_seal.png` | Trinket | Unpaid Charter Seal | Relic | 5 generated unique stat(s) | Runtime renders accepted BV-026 Relic item art; legacy `Ring` remains fallback coverage. |

Runtime loot/UI art note:

- `app/src/main/res-loot/drawable-nodpi/loot_icon_paper_shield.png` is used as the empty Armor slot icon; it is not currently tied to a generated loot definition.
- `app/src/main/res-loot/drawable-nodpi/loot_icon_*.png` files remain as legacy fallback icon families and empty-slot UI affordances.
- Accepted final loot art must follow `docs/design/loot-art-quality-bar.md` and the relevant batch briefs.

## Item Evolution Notes

Open design space for item progression:

- Unlock Relic rarity profiles through post-Palier 3 quests, Armory/Forge upgrades, or achievements.
- Upgrade tiers that add a stat slot, increase values, or unlock a special rule.
- Reroll or reforging system owned by the Armory/Forge.
- Consumables that are actually spent for one expedition instead of equipped as flat-stat loot.
- Class-tagged items, such as Rogueish trinkets or Bruiser weapons, if hero builds need stronger identity.

## Update Checklist

When adding or changing a hero:

1. Update the hero row in this catalog.
2. Update `HeroCatalog` in `HeroGacha.kt`.
3. Update `HeroSpecialCatalog` if the hero has a new or changed special.
4. Update portrait mapping in `BadventurersUiArt.kt` if artwork changes.
5. Update balance tests if growth, rarity, or recruitment odds change.

When adding or changing an item:

1. Update the item row in this catalog.
2. Update `LootCatalog` in `Loot.kt`.
3. Update `docs/data/items.csv`.
4. Add or update a reviewed raster asset only after it passes `docs/design/loot-art-quality-bar.md`.
5. Update loot generation tests if rarity pools, stat slots, stat ranges, sell value rules, or artwork acceptance changes.
