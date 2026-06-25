# Uncommon And Rare Loot Art Briefs

Source scope: `docs/data/items.csv`, `docs/content/hero-item-catalog.md`, and `docs/design/loot-art-quality-bar.md`.

Purpose: reviewed-ready briefs and integration notes for the 8 Uncommon and 8 Rare loot items delivered in BV-025. These are for art review and future replacement passes only; the source of truth for gameplay values remains the item catalog and `Loot.kt`.

Integration status: BV-025 Uncommon/Rare PNGs are integrated under `app/src/main/res-loot/drawable-nodpi/loot_art_<item_id>.png`, reviewed as a batch in `docs/content/uncommon-rare-loot-art-contact-sheet.png`, and validated by `tools/validate-loot-art.ps1`.

## Production Checklist

- Each icon is a 128x128 PNG with alpha and transparent corners.
- Each item uses an item-specific object concept, not a recolored copy of a generic slot icon.
- Uncommon items read as sturdier, cleaner, and more intentional than Common items.
- Rare items add richer construction, polish, and story detail without depending on readable text.
- The item silhouette must still read at 96x96, 64x64, and 40x40 inside the loot card frame.

## Briefs

### weapon_receipt_cutter - Receipt Cutter

- Item: `weapon_receipt_cutter`; display name: Receipt Cutter; slot: Weapon; rarity: Uncommon.
- Distinct silhouette: Short hooked blade shaped like a sharpened receipt spike, with a clipped-paper notch and compact handle.
- Materials/colors: Worn steel edge, brass rivets, muted parchment wrap, teal shadow.
- Comedy/Badventurers hook: A filing tool upgraded into a dramatic dueling knife.
- Forbidden shortcuts: No normal dagger, no plain paper slip, no readable receipt text.

### weapon_mop_halberd - Mop Halberd

- Item: `weapon_mop_halberd`; display name: Mop Halberd; slot: Weapon; rarity: Uncommon.
- Distinct silhouette: Long mop haft with a broad improvised axe hook and visible mop strands bound near the head.
- Materials/colors: Damp wood, dull iron blade, blue-gray mop fiber, brass clamps.
- Comedy/Badventurers hook: Cleaning equipment repurposed with more confidence than engineering.
- Forbidden shortcuts: No plain spear, no broom recolor, no generic polearm head.

### armor_buttoned_barrel - Buttoned Barrel

- Item: `armor_buttoned_barrel`; display name: Buttoned Barrel; slot: Armor; rarity: Uncommon.
- Distinct silhouette: Rounded barrel cuirass with oversized front buttons and side straps.
- Materials/colors: Dark oak staves, iron hoops, polished button studs, mossy green cloth padding.
- Comedy/Badventurers hook: Formalwear energy applied to a wine barrel.
- Forbidden shortcuts: No normal chestplate, no plain crate, no hood silhouette.

### footwear_squeaky_greaves - Squeaky Greaves

- Item: `footwear_squeaky_greaves`; display name: Squeaky Greaves; slot: Footwear; rarity: Uncommon.
- Distinct silhouette: Pair of bulky shin guards with curled squeaky soles and small hinge plates.
- Materials/colors: Tin plates, leather straps, pale rubber sole, cool blue highlights.
- Comedy/Badventurers hook: Stealth gear that announces itself every step.
- Forbidden shortcuts: No ordinary boots, no single shoe, no recolored Common footwear.

### trinket_queue_token - Queue Token

- Item: `trinket_queue_token`; display name: Queue Token; slot: Trinket; rarity: Uncommon.
- Distinct silhouette: Round token charm with a queue-hook notch and dangling cord.
- Materials/colors: Aged brass, stamped pseudo-marks, dark cord, teal patina.
- Comedy/Badventurers hook: A bureaucratic talisman for waiting your turn at danger.
- Forbidden shortcuts: No plain ring, no readable number, no coin-only circle.

### headgear_bucket_visor - Bucket Visor

- Item: `headgear_bucket_visor`; display name: Bucket Visor; slot: Headgear; rarity: Uncommon.
- Distinct silhouette: Upside-down bucket helmet with a hinged visor slit and handle crest.
- Materials/colors: Scuffed tin, leather hinge, brass handle pins, moss shadow.
- Comedy/Badventurers hook: Kitchenware pretending to be knightly equipment.
- Forbidden shortcuts: No standard helmet, no plain bucket, no face or character head.

### consumable_warmish_tea - Warmish Tea

- Item: `consumable_warmish_tea`; display name: Warmish Tea; slot: Consumable; rarity: Uncommon.
- Distinct silhouette: Squat mug or tankard with visible steam that is barely committed.
- Materials/colors: Cream ceramic, amber tea, blue-gray steam, brass spoon accent.
- Comedy/Badventurers hook: A comfort item whose temperature is already suspicious.
- Forbidden shortcuts: No plain potion bottle, no readable label, no coffee cup stock shape.

### consumable_pickle_potion - Pickle Potion

- Item: `consumable_pickle_potion`; display name: Pickle Potion; slot: Consumable; rarity: Uncommon.
- Distinct silhouette: Rounded potion flask with a pickle spear shape suspended inside.
- Materials/colors: Green brine, glass highlights, cork stopper, small brass tag with pseudo-marks.
- Comedy/Badventurers hook: Technically medicinal if the paperwork says so.
- Forbidden shortcuts: No generic green potion, no readable label, no jar-only silhouette.

### weapon_fine_print_rapier - Fine Print Rapier

- Item: `weapon_fine_print_rapier`; display name: Fine Print Rapier; slot: Weapon; rarity: Rare.
- Distinct silhouette: Long elegant rapier with scroll-like guard and tiny pseudo-writing marks on the blade base.
- Materials/colors: Polished silver, burgundy grip, gold guard, deep blue shadow.
- Comedy/Badventurers hook: The contract clause that literally stabs back.
- Forbidden shortcuts: No plain sword, no readable words, no duplicate of Receipt Cutter.

### weapon_taxman_gavel - Taxman Gavel

- Item: `weapon_taxman_gavel`; display name: Taxman Gavel; slot: Weapon; rarity: Rare.
- Distinct silhouette: Heavy gavel head with reinforced bands and a short authoritative handle.
- Materials/colors: Dark hardwood, gold inlay, steel corner caps, burgundy wrap.
- Comedy/Badventurers hook: A court tool upgraded for aggressive collections.
- Forbidden shortcuts: No plain hammer, no axe, no readable emblem.

### armor_invoice_mail - Invoice Mail

- Item: `armor_invoice_mail`; display name: Invoice Mail; slot: Armor; rarity: Rare.
- Distinct silhouette: Layered mail shirt made of envelope-like plates and seal tabs.
- Materials/colors: Silver scale plates, parchment tabs, red wax dots, blue steel shadows.
- Comedy/Badventurers hook: Armor that itemizes every dent.
- Forbidden shortcuts: No normal chainmail, no plain cloak, no readable invoices.

### footwear_witness_slippers - Witness Slippers

- Item: `footwear_witness_slippers`; display name: Witness Slippers; slot: Footwear; rarity: Rare.
- Distinct silhouette: Pair of soft slippers with formal buckles and alert little raised toes.
- Materials/colors: Deep blue velvet, gold buckles, pale sole trim, cool shadow.
- Comedy/Badventurers hook: Courtroom footwear for someone who definitely saw nothing.
- Forbidden shortcuts: No boots, no sandals, no duplicated Squeaky Greaves pose.

### trinket_overtime_hourglass - Overtime Hourglass

- Item: `trinket_overtime_hourglass`; display name: Overtime Hourglass; slot: Trinket; rarity: Rare.
- Distinct silhouette: Small hourglass charm with too much sand trapped in both bulbs.
- Materials/colors: Blue glass, gold frame, silver chain, warm amber sand.
- Comedy/Badventurers hook: Timekeeping that has filed for extra hours.
- Forbidden shortcuts: No plain ring, no generic clock, no readable numbers.

### headgear_notary_wig - Notary Wig

- Item: `headgear_notary_wig`; display name: Notary Wig; slot: Headgear; rarity: Rare.
- Distinct silhouette: Powdered legal wig with curled side rolls and a tiny seal pin.
- Materials/colors: Pale gray curls, burgundy ribbon, gold pin, blue shadow.
- Comedy/Badventurers hook: Defensive paperwork worn directly on the head.
- Forbidden shortcuts: No hood, no normal helmet, no character face.

### consumable_bottled_courage - Bottled Courage

- Item: `consumable_bottled_courage`; display name: Bottled Courage; slot: Consumable; rarity: Rare.
- Distinct silhouette: Tall bottle with a bright inner glow and reinforced cork cage.
- Materials/colors: Blue glass, amber glow, gold wire, burgundy wax seal.
- Comedy/Badventurers hook: Bravery with a cork and questionable side effects.
- Forbidden shortcuts: No generic potion, no readable label, no plain flask.

### trinket_minor_oracle_receipt - Minor Oracle Receipt

- Item: `trinket_minor_oracle_receipt`; display name: Minor Oracle Receipt; slot: Trinket; rarity: Rare.
- Distinct silhouette: Folded receipt charm with a crystal bead and sealed corner.
- Materials/colors: Pale parchment, blue crystal, gold clip, red wax seal.
- Comedy/Badventurers hook: A prophecy issued as a tiny refundable document.
- Forbidden shortcuts: No readable text, no plain scroll, no duplicate Queue Token circle.
