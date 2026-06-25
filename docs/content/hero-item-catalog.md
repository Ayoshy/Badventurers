# Hero And Item Catalog

Last updated: 2026-06-25

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

## Heroes

| ID | Artwork | Class | Name | Rarity | Start Level | Trait | Special | Stats Distribution | Per-Level Growth Profile | Evolution / Design Notes |
| --- | --- | --- | --- | --- | ---: | --- | --- | --- | --- | --- |
| brugg | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_brugg.png` | Bruiser | Brugg | Common | 3 | Overconfident | RamshackleCharge | For95 Mag8 Lck36 Ego92 Hyg20 BF64 Ori38 Pap10 End88 Cha45 | P=For; S=End; T=Ego; Trait=Ego; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| mira | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_mira.png` | ApprenticeMage | Mira | Uncommon | 2 | ReadsManual | GlyphReader | For16 Mag90 Lck48 Ego54 Hyg72 BF38 Ori44 Pap76 End32 Cha52 | P=Mag; S=Pap; T=Lck; Trait=Pap; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| nell | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_nell.png` | Rogueish | Nell | Uncommon | 2 | SuspiciouslyLucky | LightFingers | For34 Mag28 Lck88 Ego58 Hyg46 BF74 Ori84 Pap26 End48 Cha62 | P=Lck; S=Ori; T=BF; Trait=Lck; Odd=Pap; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| darrik | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_brugg.png` | Bruiser | Darrik | Common | 1 | Overconfident | HumanWall | For80 Mag10 Lck30 Ego78 Hyg26 BF58 Ori35 Pap12 End72 Cha40 | P=For; S=End; T=Ego; Trait=Ego; Odd=Pap; Extras=none | Reuses Brugg portrait; unique art TBD. Promotion ranks 0-4 planned. |
| quill | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_quill.png` | BardAccountant | Quill | Rare | 2 | PainfullyOrganized | AggressiveMinutes | For28 Mag42 Lck55 Ego64 Hyg70 BF52 Ori58 Pap96 End44 Cha82 | P=Pap; S=Cha; T=Lck; Trait=Pap; Odd=Mag; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| orla | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_brugg.png` | Bruiser | Orla | Rare | 2 | ReadsManual | TerrainManual | For92 Mag22 Lck45 Ego66 Hyg50 BF48 Ori52 Pap35 End86 Cha48 | P=For; S=End; T=Ego; Trait=Pap; Odd=For; Extras=+P at level multiples of 3 | Reuses Brugg portrait; unique art TBD. Promotion ranks 0-4 planned. |
| pippa | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_mira.png` | ApprenticeMage | Pippa | Epic | 3 | SuspiciouslyLucky | UnstableLuck | For18 Mag104 Lck74 Ego62 Hyg78 BF42 Ori64 Pap72 End42 Cha70 | P=Mag; S=Pap; T=Lck; Trait=Lck; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | Reuses Mira portrait; unique art TBD. Promotion ranks 0-4 planned. |
| vex | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_nell.png` | Rogueish | Vex | Epic | 3 | SuspiciouslyLucky | DirtyJackpot | For42 Mag44 Lck96 Ego68 Hyg54 BF86 Ori98 Pap24 End60 Cha64 | P=Lck; S=Ori; T=BF; Trait=Lck; Odd=Cha; Extras=+P at level multiples of 3; +S at level multiples of 4 | Reuses Nell portrait; unique art TBD. Promotion ranks 0-4 planned. |
| bramble | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_hunter.png` | Hunter | Bramble | Common | 1 | ReadsManual | FreshTrail | For64 Mag24 Lck58 Ego34 Hyg48 BF36 Ori94 Pap28 End76 Cha40 | P=Ori; S=End; T=Lck; Trait=Pap; Odd=BF; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| pax | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_priest.png` | Priest | Pax | Uncommon | 2 | PainfullyOrganized | CleanBlessing | For24 Mag78 Lck70 Ego38 Hyg92 BF22 Ori56 Pap66 End62 Cha76 | P=Hyg; S=Mag; T=Cha; Trait=Pap; Odd=Cha; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| sable | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_ninja.png` | Ninja | Sable | Rare | 2 | SuspiciouslyLucky | NoTrace | For48 Mag48 Lck82 Ego74 Hyg64 BF78 Ori106 Pap32 End66 Cha58 | P=Ori; S=Lck; T=BF; Trait=Lck; Odd=Cha; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| morrow | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_necromancer.png` | Necromancer | Morrow | Epic | 3 | Overconfident | NecroLever | For18 Mag110 Lck52 Ego86 Hyg30 BF70 Ori60 Pap88 End46 Cha42 | P=Mag; S=BF; T=Pap; Trait=Ego; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | Promotion ranks 0-4 planned; branching evolution TBD. |
| ledger | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_quill.png` | BardAccountant | Sir Ledger | Legendary | 4 | PainfullyOrganized | HostileAudit | For52 Mag82 Lck74 Ego90 Hyg86 BF64 Ori76 Pap120 End70 Cha92 | P=Pap; S=Cha; T=Lck; Trait=Pap; Odd=Pap; Extras=+P at level multiples of 3; +S at level multiples of 4; +Trait at level multiples of 5 | Reuses Quill portrait; unique art TBD. Promotion ranks 0-4 planned. |
| paladin | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_paladin.png` | Paladin | Dame Brindle | Legendary | 4 | ReadsManual | UnbreakableOath | For88 Mag54 Lck42 Ego66 Hyg70 BF18 Ori60 Pap84 End92 Cha78 | P=End; S=Hyg; T=For; Trait=Pap; Odd=Cha; Extras=+P at level multiples of 3; +S at level multiples of 4; +Trait at level multiples of 5 | Promotion ranks 0-4 planned; branching evolution TBD. |
| comptable | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_comptable.png` | Accountant | Tally Noakes | Rare | 2 | PainfullyOrganized | BalancedBooks | For18 Mag32 Lck60 Ego64 Hyg78 BF30 Ori86 Pap104 End40 Cha54 | P=Pap; S=Ori; T=Cha; Trait=Pap; Odd=Mag; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| jardinier | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_jardinier.png` | Gardener | Moss Fenlow | Common | 1 | ReadsManual | GreenThumb | For46 Mag20 Lck70 Ego36 Hyg88 BF24 Ori92 Pap26 End68 Cha44 | P=Hyg; S=Ori; T=End; Trait=Pap; Odd=Lck; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| chevalier_de_la_mort | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_chevalier_de_la_mort.png` | DeathKnight | Grave Odo | Epic | 3 | Overconfident | DeathDiscount | For74 Mag84 Lck40 Ego70 Hyg16 BF82 Ori50 Pap62 End76 Cha36 | P=End; S=Mag; T=BF; Trait=Ego; Odd=End; Extras=+P at level multiples of 3; +S at level multiples of 4 | Promotion ranks 0-4 planned; branching evolution TBD. |
| chef_cuistot | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_chef_cuistot.png` | Chef | Basil Bouillon | Uncommon | 2 | PainfullyOrganized | MoraleRations | For40 Mag26 Lck58 Ego48 Hyg94 BF22 Ori64 Pap72 End60 Cha84 | P=Hyg; S=Cha; T=End; Trait=Pap; Odd=For; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| expert_en_demolition | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_expert_en_demolition.png` | DemolitionExpert | Nix Powderkeg | Rare | 2 | Overconfident | PlanBExplosives | For98 Mag20 Lck38 Ego74 Hyg26 BF84 Ori34 Pap24 End92 Cha42 | P=For; S=BF; T=End; Trait=Ego; Odd=Mag; Extras=+P at level multiples of 3 | Promotion ranks 0-4 planned; branching evolution TBD. |
| elementaire_de_sel | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_elementaire_de_sel.png` | SaltElemental | Salina Brine | Uncommon | 3 | SuspiciouslyLucky | PreservationSalt | For18 Mag84 Lck74 Ego38 Hyg92 BF28 Ori76 Pap46 End54 Cha40 | P=Mag; S=Hyg; T=Lck; Trait=Lck; Odd=End; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |
| troll_stupide | `app/src/main/res-heroes/drawable-nodpi/hero_portrait_troll_stupide.png` | StupidTroll | Grum | Common | 1 | Overconfident | CreativeMisunderstanding | For100 Mag12 Lck54 Ego44 Hyg16 BF52 Ori28 Pap10 End100 Cha34 | P=For; S=End; T=BF; Trait=Ego; Odd=End; Extras=none | Promotion ranks 0-4 planned; branching evolution TBD. |

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

Items now use fixed-rarity catalog definitions. `LootGenerator` rolls a rarity from the active profile, then chooses an item from that rarity pool. Existing items are intentionally assigned to Common so higher-rarity drops can introduce distinct names and artwork instead of upgrading the same object by math only.

Current item rarity profiles are progression-gated in `LootGenerator`:

- Base profile before the Palier 2 gate: Common `75`, Uncommon `25`.
- Palier 2 Rare loot profile after `8` completed quests: Common `65`, Uncommon `25`, Rare `10`.
- Epic and Relic item definitions exist for later unlock profiles, but current runtime profiles do not roll them yet.

| Rarity | Catalog Count | Stat Slots |
| --- | ---: | ---: |
| Common | 25 | 1 |
| Uncommon | 8 | 2 |
| Rare | 8 | 3 |
| Epic | 8 | 4 |
| Relic | 8 | 5 |

Each stat slot chooses a unique stat type and a weighted value from `1` to `10`. Item stat values are flat bonuses; items do not level up yet.

One item equals one artwork resource. The `LootIcon` field remains as a legacy family/fallback for old saves and empty-slot affordances, while runtime item cards render through the item-specific art resource mapped by `LootArt` in `BadventurersUiArt.kt`.

## Items

| ID | Artwork | Slot | Name | Fixed Rarity | Stats Distribution | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| weapon_bent_spoon | `app/src/main/res-loot/drawable/loot_art_weapon_bent_spoon.xml` | Weapon | Bent Spoon | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| weapon_fork_spear | `app/src/main/res-loot/drawable/loot_art_weapon_fork_spear.xml` | Weapon | Fork Spear | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| weapon_moon_axe | `app/src/main/res-loot/drawable/loot_art_weapon_moon_axe.xml` | Weapon | Moon Axe | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| weapon_nibblade | `app/src/main/res-loot/drawable/loot_art_weapon_nibblade.xml` | Weapon | Nibblade | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| weapon_toast_mace | `app/src/main/res-loot/drawable/loot_art_weapon_toast_mace.xml` | Weapon | Toast Spoon | Common | 1 generated unique stat(s) | Existing item reassigned to Common; name/id mismatch retained for save compatibility. |
| armor_patch_hood | `app/src/main/res-loot/drawable/loot_art_armor_patch_hood.xml` | Armor | Patch Cloak | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| armor_moss_coat | `app/src/main/res-loot/drawable/loot_art_armor_moss_coat.xml` | Armor | Moss Coat | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| armor_winged_boots | `app/src/main/res-loot/drawable/loot_art_armor_winged_boots.xml` | Footwear | Winged Boots | Common | 1 generated unique stat(s) | Existing item reassigned to Common; legacy id prefix retained. |
| armor_travel_boots | `app/src/main/res-loot/drawable/loot_art_armor_travel_boots.xml` | Footwear | Travel Boots | Common | 1 generated unique stat(s) | Existing item reassigned to Common; legacy id prefix retained. |
| trinket_lucky_ring | `app/src/main/res-loot/drawable/loot_art_trinket_lucky_ring.xml` | Trinket | Lucky Ring | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| trinket_pocket_ring | `app/src/main/res-loot/drawable/loot_art_trinket_pocket_ring.xml` | Trinket | Pocket Ring | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| trinket_spare_ring | `app/src/main/res-loot/drawable/loot_art_trinket_spare_ring.xml` | Trinket | Spare Ring | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| trinket_dusty_ring | `app/src/main/res-loot/drawable/loot_art_trinket_dusty_ring.xml` | Trinket | Dusty Ring | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| trinket_quiet_ring | `app/src/main/res-loot/drawable/loot_art_trinket_quiet_ring.xml` | Trinket | Quiet Ring | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| armor_panic_helm | `app/src/main/res-loot/drawable/loot_art_armor_panic_helm.xml` | Headgear | Panic Helm | Common | 1 generated unique stat(s) | Existing item reassigned to Common; legacy id prefix retained. |
| headgear_soup_helm | `app/src/main/res-loot/drawable/loot_art_headgear_soup_helm.xml` | Headgear | Soup Helm | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| headgear_wobble_cap | `app/src/main/res-loot/drawable/loot_art_headgear_wobble_cap.xml` | Headgear | Wobble Cap | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| headgear_paper_crown | `app/src/main/res-loot/drawable/loot_art_headgear_paper_crown.xml` | Headgear | Paper Crown | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| headgear_lantern_hat | `app/src/main/res-loot/drawable/loot_art_headgear_lantern_hat.xml` | Headgear | Lantern Hat | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| headgear_grin_hood | `app/src/main/res-loot/drawable/loot_art_headgear_grin_hood.xml` | Headgear | Grin Hood | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| consumable_stale_potion | `app/src/main/res-loot/drawable/loot_art_consumable_stale_potion.xml` | Consumable | Stale Potion | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| consumable_brave_brew | `app/src/main/res-loot/drawable/loot_art_consumable_brave_brew.xml` | Consumable | Brave Brew | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| consumable_tiny_flask | `app/src/main/res-loot/drawable/loot_art_consumable_tiny_flask.xml` | Consumable | Tiny Flask | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| consumable_odd_elixir | `app/src/main/res-loot/drawable/loot_art_consumable_odd_elixir.xml` | Consumable | Odd Elixir | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| consumable_snap_tonic | `app/src/main/res-loot/drawable/loot_art_consumable_snap_tonic.xml` | Consumable | Snap Tonic | Common | 1 generated unique stat(s) | Existing item reassigned to Common. |
| weapon_receipt_cutter | `app/src/main/res-loot/drawable/loot_art_weapon_receipt_cutter.xml` | Weapon | Receipt Cutter | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| weapon_mop_halberd | `app/src/main/res-loot/drawable/loot_art_weapon_mop_halberd.xml` | Weapon | Mop Halberd | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| armor_buttoned_barrel | `app/src/main/res-loot/drawable/loot_art_armor_buttoned_barrel.xml` | Armor | Buttoned Barrel | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| footwear_squeaky_greaves | `app/src/main/res-loot/drawable/loot_art_footwear_squeaky_greaves.xml` | Footwear | Squeaky Greaves | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| trinket_queue_token | `app/src/main/res-loot/drawable/loot_art_trinket_queue_token.xml` | Trinket | Queue Token | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| headgear_bucket_visor | `app/src/main/res-loot/drawable/loot_art_headgear_bucket_visor.xml` | Headgear | Bucket Visor | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| consumable_warmish_tea | `app/src/main/res-loot/drawable/loot_art_consumable_warmish_tea.xml` | Consumable | Warmish Tea | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| consumable_pickle_potion | `app/src/main/res-loot/drawable/loot_art_consumable_pickle_potion.xml` | Consumable | Pickle Potion | Uncommon | 2 generated unique stat(s) | New Uncommon item. |
| weapon_fine_print_rapier | `app/src/main/res-loot/drawable/loot_art_weapon_fine_print_rapier.xml` | Weapon | Fine Print Rapier | Rare | 3 generated unique stat(s) | New Rare item. |
| weapon_taxman_gavel | `app/src/main/res-loot/drawable/loot_art_weapon_taxman_gavel.xml` | Weapon | Taxman Gavel | Rare | 3 generated unique stat(s) | New Rare item. |
| armor_invoice_mail | `app/src/main/res-loot/drawable/loot_art_armor_invoice_mail.xml` | Armor | Invoice Mail | Rare | 3 generated unique stat(s) | New Rare item. |
| footwear_witness_slippers | `app/src/main/res-loot/drawable/loot_art_footwear_witness_slippers.xml` | Footwear | Witness Slippers | Rare | 3 generated unique stat(s) | New Rare item. |
| trinket_overtime_hourglass | `app/src/main/res-loot/drawable/loot_art_trinket_overtime_hourglass.xml` | Trinket | Overtime Hourglass | Rare | 3 generated unique stat(s) | New Rare item. |
| headgear_notary_wig | `app/src/main/res-loot/drawable/loot_art_headgear_notary_wig.xml` | Headgear | Notary Wig | Rare | 3 generated unique stat(s) | New Rare item. |
| consumable_bottled_courage | `app/src/main/res-loot/drawable/loot_art_consumable_bottled_courage.xml` | Consumable | Bottled Courage | Rare | 3 generated unique stat(s) | New Rare item. |
| trinket_minor_oracle_receipt | `app/src/main/res-loot/drawable/loot_art_trinket_minor_oracle_receipt.xml` | Trinket | Minor Oracle Receipt | Rare | 3 generated unique stat(s) | New Rare item. |
| weapon_auditors_halberd | `app/src/main/res-loot/drawable/loot_art_weapon_auditors_halberd.xml` | Weapon | Auditors Halberd | Epic | 4 generated unique stat(s) | New Epic item. |
| weapon_dragon_stamp | `app/src/main/res-loot/drawable/loot_art_weapon_dragon_stamp.xml` | Weapon | Dragon Stamp | Epic | 4 generated unique stat(s) | New Epic item. |
| armor_misfiled_aegis | `app/src/main/res-loot/drawable/loot_art_armor_misfiled_aegis.xml` | Armor | Misfiled Aegis | Epic | 4 generated unique stat(s) | New Epic item. |
| footwear_plausible_denial_boots | `app/src/main/res-loot/drawable/loot_art_footwear_plausible_denial_boots.xml` | Footwear | Boots of Plausible Denial | Epic | 4 generated unique stat(s) | New Epic item. |
| trinket_bell_of_last_call | `app/src/main/res-loot/drawable/loot_art_trinket_bell_of_last_call.xml` | Trinket | Bell of Last Call | Epic | 4 generated unique stat(s) | New Epic item. |
| headgear_emergency_minutes_crown | `app/src/main/res-loot/drawable/loot_art_headgear_emergency_minutes_crown.xml` | Headgear | Crown of Emergency Minutes | Epic | 4 generated unique stat(s) | New Epic item. |
| consumable_second_chance_soup | `app/src/main/res-loot/drawable/loot_art_consumable_second_chance_soup.xml` | Consumable | Soup of Second Chances | Epic | 4 generated unique stat(s) | New Epic item. |
| trinket_contract_knot | `app/src/main/res-loot/drawable/loot_art_trinket_contract_knot.xml` | Trinket | Contract Knot | Epic | 4 generated unique stat(s) | New Epic item. |
| weapon_spoon_final_notice | `app/src/main/res-loot/drawable/loot_art_weapon_spoon_final_notice.xml` | Weapon | Spoon of Final Notice | Relic | 5 generated unique stat(s) | New Relic item. |
| weapon_moonlit_receipt_cleaver | `app/src/main/res-loot/drawable/loot_art_weapon_moonlit_receipt_cleaver.xml` | Weapon | Moonlit Receipt Cleaver | Relic | 5 generated unique stat(s) | New Relic item. |
| armor_many_signatures_cloak | `app/src/main/res-loot/drawable/loot_art_armor_many_signatures_cloak.xml` | Armor | Cloak of Many Signatures | Relic | 5 generated unique stat(s) | New Relic item. |
| footwear_inevitable_return_sandals | `app/src/main/res-loot/drawable/loot_art_footwear_inevitable_return_sandals.xml` | Footwear | Sandals of Inevitable Return | Relic | 5 generated unique stat(s) | New Relic item. |
| trinket_perpetual_queue_ring | `app/src/main/res-loot/drawable/loot_art_trinket_perpetual_queue_ring.xml` | Trinket | Ring of Perpetual Queue | Relic | 5 generated unique stat(s) | New Relic item. |
| headgear_halo_compliance | `app/src/main/res-loot/drawable/loot_art_headgear_halo_compliance.xml` | Headgear | Halo of Compliance | Relic | 5 generated unique stat(s) | New Relic item. |
| consumable_absolute_maybe_elixir | `app/src/main/res-loot/drawable/loot_art_consumable_absolute_maybe_elixir.xml` | Consumable | Elixir of Absolute Maybe | Relic | 5 generated unique stat(s) | New Relic item. |
| trinket_unpaid_charter_seal | `app/src/main/res-loot/drawable/loot_art_trinket_unpaid_charter_seal.xml` | Trinket | Unpaid Charter Seal | Relic | 5 generated unique stat(s) | New Relic item. |

Runtime loot/UI art note:

- `app/src/main/res-loot/drawable-nodpi/loot_icon_paper_shield.png` is used as the empty Armor slot icon; it is not currently tied to a generated loot definition.
- `app/src/main/res-loot/drawable-nodpi/loot_icon_*.png` files remain as legacy fallback icon families and empty-slot UI affordances.

## Item Evolution Notes

Open design space for item progression:

- Unlock Epic and Relic rarity profiles through Palier 3+ quests, Armory/Forge upgrades, or achievements.
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
3. Add or update the unique `LootArt` resource and `lootArtResource` mapping.
4. Update `docs/data/items.csv`.
5. Update loot generation tests if rarity pools, stat slots, stat ranges, sell value rules, or artwork uniqueness changes.
