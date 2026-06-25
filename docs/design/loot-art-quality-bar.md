# Loot Art Quality Bar

Last updated: 2026-06-25

## Purpose

This is the acceptance standard for Badventurers loot artwork before it is integrated into the Android app.

The previous XML-vector placeholder batch does not meet this bar. Simple geometric shapes, flat symbolic icons, palette swaps, procedural vector arrangements, or "one shape plus a rarity color" are placeholders, not loot art.

Loot art must feel like a real collectible reward in a polished 16-bit-inspired mobile RPG: readable on a phone, warm and tactile, a little ridiculous, and grounded in the cozy low-budget fantasy guild identity.

## Minimum Quality Bar

An acceptable loot icon must satisfy all of these:

- The item reads as its slot at small size: weapon, armor, footwear, trinket, headgear, or consumable.
- The item reads as its specific concept, not only its category. A `Receipt Cutter` must look different from a `Fine Print Rapier`; a `Lucky Ring` must not be the same ring with a different tint.
- The silhouette is chunky, asymmetrical enough to be memorable, and recognizable without text.
- The art has pixel-crafted form: crisp clusters, controlled stair-steps, deliberate highlights, shadow planes, and no blurred or muddy scaling.
- The item has material identity: metal, wood, cloth, glass, leather, paper, soup, moss, brass, wax, etc. should be visible through texture and lighting.
- The humor is in the object design: bad repairs, stamps, labels, straps, paperwork, guild bureaucracy, cheap fantasy improvisation, or overconfident craftsmanship.
- The icon has reward appeal. Even Common items may be shabby, but they should look intentional and tappable.
- The asset looks finished when shown in the live item card, not only in an isolated art preview.

Reject the asset if it looks like:

- Android vector placeholder geometry.
- A flat asset-pack symbol.
- A line icon, emoji, UI glyph, badge, or rarity frame pretending to be the item.
- A generic silhouette recolored per rarity.
- A readable text joke instead of a visual joke.
- An AI-concept sketch that was not cleaned into crisp pixel art.

## Asset Format And Delivery

Use raster pixel art for final loot assets.

- Final app asset: PNG or WebP, transparent background.
- Source asset: layered or editable source preferred, such as Aseprite, PSD, Krita, or a high-quality indexed PNG source.
- Canvas: square, with a recommended working size of `128x128` pixels.
- Internal art bounds: keep the main object inside roughly `104x104` pixels, leaving safe padding for rarity frames, card shadows, and reveal effects.
- Background: transparent. Do not bake item-card backgrounds, rarity frames, glows, labels, or UI plates into the item image.
- Pixel scale: author at a consistent internal pixel scale and export at integer scaling only.
- Edges: crisp opaque pixels with deliberate anti-aliasing only when it supports modern mobile readability. No soft blur around the whole object.
- Lighting: one clear key light, usually warm top-left tavern light, with cool or dark shadow accents where useful.
- Orientation: use three-quarter or slightly tilted presentation when it helps the silhouette. Avoid perfectly flat front-on shapes unless the object concept demands it.
- Text: no readable text in the art. Use stamps, seals, tags, tally marks, or pseudo-writing marks instead.
- Transparency check: no accidental opaque square, hidden matte, exported checkerboard, or cropped shadow.

## Readability Constraints

Before integration, review the asset at these sizes:

- `96x96`: should show the specific item and its main joke.
- `64x64`: should still read as the correct slot and broad concept.
- `40x40`: should retain a clear silhouette and rarity tier feel when framed in UI.

The asset should survive:

- Warm parchment or dark tavern UI behind it.
- A small rarity frame around it.
- Adjacent item icons from the same slot.
- Quick scanning in an inventory grid.

Do not rely on tiny details for the only identifying feature. Small details are allowed, but the big silhouette must carry the read.

## Rarity Visual Language

Rarity should change the construction, materials, finish, and story of the object. It must not be only a recolor.

| Rarity | Visual Language |
| --- | --- |
| Common | Shabby but charming. Patched cloth, bent cutlery, chipped wood, dull metal, cheap straps, crooked repairs, one clear material contrast. Little or no magic. |
| Uncommon | Better assembled and more specific. Brass fittings, small guild tags, brighter material accents, clearer prop joke, one secondary detail such as a label, charm, wax seal, or rivet pattern. |
| Rare | Professionally absurd. Sharper silhouette, richer materials, controlled shine, stronger bureaucracy/fantasy mashup, small animated-effect potential such as glint, steam, sparkle, or stamped aura. |
| Epic | Theatrical and elaborate. Layered materials, unusual construction, magical or ceremonial detail, visible craftsmanship, bolder asymmetry, and a strong story read before the item name is shown. |
| Relic | Legendary office-fantasy nonsense. Ancient guild seals, moonlit metal, impossible paperwork, haloed compliance, cursed queues, signature chains, rare materials, and a unique silhouette that cannot be mistaken for another item. |

Rarity frames, glows, or UI treatments may support the tier, but the item itself must already communicate the tier through form and detail.

## Item Brief Template

Use one brief per item before producing art.

```text
Item id:
Item name:
Slot:
Rarity:

Silhouette:
- Primary read:
- Secondary shape:
- Angle / pose:

Materials:
- Main material:
- Secondary material:
- Highlight / shadow notes:

Comedy hook:
- Visual joke:
- Guild/bureaucracy/fantasy detail:

Readability hook:
- What reads at 64x64:
- What reads at 40x40:
- Must differ from:

Forbidden shortcuts:
- No:
- No:
- No:

Review checklist:
- Slot reads without text:
- Item-specific concept reads without text:
- Rarity changes construction/materials, not just color:
- Pixel clusters are crisp:
- Transparent background is clean:
- Works on parchment and dark tavern UI:
- Does not duplicate another item silhouette:
```

## Pre-Integration Acceptance Checklist

Use this checklist before adding or replacing app assets.

- The item has a completed brief using the template above.
- The delivered asset is raster pixel art, not XML/vector placeholder geometry.
- The file has transparent background and no baked UI frame.
- The item reads at `96x96`, `64x64`, and `40x40`.
- The slot and specific item concept are recognizable without reading the name.
- The rarity tier is visible through silhouette, materials, finish, and detail, not only color.
- The humor comes from the prop design and Badventurers world, not from readable text.
- The art uses warm tavern/brass/parchment/moss identity without becoming a one-color brown asset.
- The asset has enough contrast for both parchment panels and dark tavern surfaces.
- Similar items in the same slot have distinct silhouettes.
- The live Android item card has been visually checked before the placeholder is considered replaced.
- The asset path and item row are aligned with `docs/data/items.csv` and the item catalog.

If any checklist item fails, the asset remains a placeholder and must not be marked accepted.
