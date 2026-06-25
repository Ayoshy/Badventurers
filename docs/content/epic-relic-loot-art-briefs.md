# Epic and Relic Loot Art Briefs

Last updated: 2026-06-25

BV-026 produced accepted production PNG artwork for the 8 Epic and 8 Relic loot definitions. The accepted runtime files live under `app/src/main/res-loot/drawable-nodpi` and are referenced by `docs/data/items.csv`, `docs/content/hero-item-catalog.md`, and `acceptedLootArtResourceIds`.

## Production Checklist

- Source sheet: `artifacts/loot-art/bv026/bv026-generated-source.png`
- Runtime exports: `app/src/main/res-loot/drawable-nodpi/loot_art_<item_id>.png`
- Contact sheet: `docs/content/epic-relic-loot-art-contact-sheet.png`
- Slicer: `tools/slice-bv026-loot-art.ps1`
- Output size: 128x128 PNG with alpha channel and transparent corners.
- Style target: polished 16-bit pixel-art loot icons, readable at phone UI size, premium but not noisy.
- Constraint: item identity comes from object design and silhouette, not just rarity color or borders.

## Generation Prompt Set

Use case: stylized-concept
Asset type: mobile game loot icon sprite sheet for Badventurers, source sheet to be sliced into sixteen 128x128 transparent PNG item icons.
Primary request: Create a 4 by 4 sprite sheet of sixteen separate polished 16-bit pixel-art fantasy loot icons on a perfectly flat solid #00ff00 chroma-key background. No labels, no numbers, no UI frames, no text, no watermark.
Style/medium: polished 16-bit-inspired 2D pixel art with modern mobile-game readability, crisp pixel clusters, chunky silhouettes, warm tavern highlights, cool shadow accents, subtle brass/moss/burgundy identity.
Composition/framing: Each cell contains one centered isolated item with generous padding. Items must not touch cell borders or overlap neighboring cells. Use a consistent three-quarter icon angle where helpful.
Rarity direction: First two rows are Epic items: premium, ornate, gold/brass/burgundy, magical accents, still readable and funny. Last two rows are Relic items: ancient mythic artifacts with moonlight, pale gold, antique bone, faint ethereal glow, but not neon and not dominant purple-blue.
Constraints: flat uniform #00ff00 background only, no shadows on background, no gradients in background, do not use #00ff00 in any item, no readable text, no letters, no labels, no border grid lines, no character hands, no existing franchise references. Keep every object visually distinct by shape and material, not only color. High contrast silhouettes for phone-sized UI.

## Item Briefs

| Order | Item id | Rarity | Slot | Brief |
| --- | --- | --- | --- | --- |
| 1 | weapon_auditors_halberd | Epic | Weapon | Ornate polearm with ledger-paper blade, red audit wax tag, brass fittings. |
| 2 | weapon_dragon_stamp | Epic | Weapon | Heavy rubber stamp shaped like a dragon head, glowing seal face, bureaucratic but heroic. |
| 3 | armor_misfiled_aegis | Epic | Armor | Crooked shield made of layered misfiled folders and metal bands, premium defensive silhouette. |
| 4 | footwear_plausible_denial_boots | Epic | Footwear | Elegant evasive boots with tiny smoke puffs and brass buckles. |
| 5 | trinket_bell_of_last_call | Epic | Trinket | Tavern bell with heroic crack, ribbon, warm brass shine. |
| 6 | headgear_emergency_minutes_crown | Epic | Headgear | Improvised crown of parchment meeting minutes, wax seals, tiny urgent sparkle. |
| 7 | consumable_second_chance_soup | Epic | Consumable | Ornate soup bowl or tankard with steam forming a faint retry arrow, cozy magical glow. |
| 8 | trinket_contract_knot | Epic | Trinket | Knotted contract ribbon and parchment cord, legal wax seals, magical binding. |
| 9 | weapon_spoon_final_notice | Relic | Weapon | Legendary spoon weapon with sharp ceremonial edge and tiny final-notice wax seal. |
| 10 | weapon_moonlit_receipt_cleaver | Relic | Weapon | Crescent cleaver made from moonlit receipt paper and steel, cool moon highlight. |
| 11 | armor_many_signatures_cloak | Relic | Armor | Folded cloak covered with tiny unreadable signature strokes and antique clasp. |
| 12 | footwear_inevitable_return_sandals | Relic | Footwear | Ancient sandals with looping path motif and small dust trail. |
| 13 | trinket_perpetual_queue_ring | Relic | Trinket | Ornate ring holding a tiny queue-ticket loop and endless circular motif. |
| 14 | headgear_halo_compliance | Relic | Headgear | Small bureaucratic halo headgear with parchment slips and holy brass glow. |
| 15 | consumable_absolute_maybe_elixir | Relic | Consumable | Relic potion bottle with ambiguous swirling liquid, cork, and pale gold glow. |
| 16 | trinket_unpaid_charter_seal | Relic | Trinket | Ancient guild charter seal medallion with ribbon and cracked wax. |