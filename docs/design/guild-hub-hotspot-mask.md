# Guild Hub Hotspot Mask

BV-032 introduces an art-authored mask for Guild hub interactions. Runtime hit testing and highlights should use this mask instead of broad hand-tuned polygons.

## Files

- Base art: `app/src/main/res-guild/drawable-nodpi/guild_hub_interactive_v3.png`
- Hotspot mask: `app/src/main/res-guild/drawable-nodpi/guild_hub_hotspot_mask.png`
- Verification preview: `artifacts/mockups/bv032/guild-hub-hotspot-mask-contact-sheet.png`
- Starter mockup: `artifacts/mockups/bv018/guild-hub-hotspots-v3.json`

## Canvas

The mask must always match the base art exactly:

- Width: `941 px`
- Height: `1672 px`
- Density folder: `drawable-nodpi`
- Origin: top-left, same pixel coordinate space as the base art

Do not trim, scale, pad, or crop the mask independently from the base art.

## Palette

Every interactive action uses one fully opaque, flat sRGB color. Transparent pixels and black pixels mean no action.

| Hotspot | Runtime action | Mask color |
| --- | --- | --- |
| Charter / Achievements | `open_achievements` | `#FF3B30` |
| Core Crew | `open_core_crew` | `#007AFF` |
| Quests / Expedition | `open_quests` | `#34C759` |
| Loot | `open_loot` | `#FFCC00` |
| Facilities / Upgrades | `open_facilities` | `#AF52DE` |
| No action | none | transparent or `#000000` |

Runtime should compare exact RGB values after rejecting transparent pixels. Treat alpha below the chosen opaque threshold as no action; the current exported mask uses only alpha `0` and `255`.

## Current Mask Notes

The current `guild_hub_hotspot_mask.png` is derived from the BV-018 directional polygon mockup, then exported as a hard-edged flat-color PNG. It is acceptable for the BV-032 integration pass because it follows the V3 landmark regions and preserves dead zones. A later production art pass may repaint the same mask by hand from layered source art, provided it keeps the palette and export rules in this document.

The top HUD band and bottom status drawer are intentionally empty:

- Top HUD no-action band: `y = 0..150 px` (`0.000..0.090` of height)
- Bottom drawer no-action band: `y >= 1518 px` (`0.908..1.000` of height)

The central floor, wall dressing, side props, stairs, barrels, plants, and other decorative objects are also no-action unless a future ticket assigns them a real feature.

## Highlight Rendering

Selected and tappable highlights should be derived from the mask silhouette:

1. Map the screen tap or pointer position into base-image pixel coordinates using the same aspect-ratio transform used to draw `guild_hub_interactive_v3.png`.
2. Read the mask pixel at that source coordinate.
3. Convert the exact palette color to a runtime action.
4. For highlighting, isolate all pixels with the selected action color and render that silhouette with the same image transform as the base art.
5. Apply highlight treatment on top of the art, such as a translucent warm overlay plus a 1-2 source-pixel outline/dilation.

Do not use the BV-018 polygons or floating debug markers as the production highlight shape. Finger tap slop may search a small radius around the exact lookup point, but the rendered highlight should still come from the mask color region so dead zones stay visually honest.

2026-06-26 runtime note: mask palette colors are lookup data only. The app renders hotspots with a warm parchment/gold rim, light fill, and edge halo; never render raw mask red, blue, green, yellow, or purple fills over the guild art.

## Export Rules

When rebuilding the mask from source art:

1. Keep the canvas exactly `941x1672` unless the base hub artwork changes; if it changes, regenerate the mask at the new exact base dimensions and update this document.
2. Paint with a hard pencil or nearest-neighbor selection. No anti-aliased edges, feathering, opacity ramps, glow, blur, or partially transparent color pixels.
3. Use only the five action colors above, transparent pixels, and optional black no-action pixels.
4. Keep HUD, bottom drawer, central floor, and decorative props unpainted unless they become assigned actions.
5. Export as PNG in `drawable-nodpi`; preserve exact sRGB hex values.
6. Re-run palette verification after export before handing the mask to runtime work.

## Verification

Minimum verification for the mask asset:

```powershell
# Check dimensions and exact palette values.
# Expected dimensions: 941x1672.
# Expected non-empty action colors: #FF3B30, #007AFF, #34C759, #FFCC00, #AF52DE.
# Expected alpha values: 0 and 255 only.
```

The BV-032 contact sheet overlays the mask on the base art for quick visual review, but the programmatic dimension and palette checks are the source of truth.