# BV-018 Guild Hub V3 Notes

This pass switches from "overlaying an existing tavern" to designing the guild home as a navigation hub first.

## Files

- `guild-hub-functional-art-v3.png` - direction mockup for a purpose-built interactive guild hall.
- `guild-hub-hotspot-overlay-v3.png` - thin technical hitmap overlay with pin markers, not a UI style proposal.
- `guild-hub-hotspots-v3.json` - normalized mapping contract for future Compose implementation.
- `guild-hub-functional-art-v3-prompt.txt` - prompt used for the V3 concept.

## Mapping Rule

The implementation should map object silhouettes, not page regions. Add only small finger-friendly tap slop at runtime. The central floor, top HUD band, bottom drawer band, balcony, banners, stairs, plants, barrels, and incidental clutter should stay non-interactive unless they get an explicit feature later.

## What V3 Fixes

- Charter/achievements is now a readable wall board, not the whole upper wall.
- Core crew is anchored to one desk with four explicit hero slots.
- Quests, loot, and facilities are visually separate landmarks.
- Central walking floor is intentionally dead space.
- Overlay uses small pins and thin boundaries instead of big opaque blocks.

## Still Not Final

The lower-left quest table and lower-right loot cache are still too close to the frame edges for production. The final art brief should demand stronger inset margins and cleaner top/bottom safe bands. V3 is the direction and mapping contract, not the final shipped image.