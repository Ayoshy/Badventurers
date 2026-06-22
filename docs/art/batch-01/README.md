# Batch 01: Prototype Art Pack

Generated: 2026-06-21

This batch validates the polished 16-bit pixel art direction before producing sliced, app-ready assets.

## Files

- `guild-home-background.png`: Guild Home background concept.
- `starter-heroes-sheet.png`: 4 starter hero portraits as a concept sheet.
- `quest-cards-sheet.png`: 3 quest card concepts.
- `loot-icons-sheet.png`: 10 loot icon concepts.
- `resource-ui-icons-sheet.png`: resource and status UI icon concepts.
- `gallery.html`: local preview page.

## Status

These files are approved for direction testing, not final production use.

Before Android integration, we should:

- choose which assets are visually approved;
- regenerate weak assets individually if needed;
- slice approved sheets into separate PNG/WebP files;
- normalize sizes and naming;
- test readability in Compose on phone-sized screens.

## Intended Use

Use this batch to update the mockup, guide UI composition, and create the first Android prototype visual targets.
## Android Slices

Generated Android-ready PNGs live in `app/src/main/res/drawable-nodpi/`:

- Hero portraits: `hero_portrait_brugg`, `hero_portrait_mira`, `hero_portrait_nell`, `hero_portrait_quill` from the 2x2 starter sheet.
- Quest banners: `quest_banner_cave_minor_regrets`, `quest_banner_bandit_tax_office`, `quest_banner_03` as clean cropped 4:1 banner artwork from the 3-location sheet. The older `quest_card_*` row slices are staging assets and should not be used directly in Compose.
- Loot icons: semantic `loot_icon_*` resources plus `loot_icon_extra_09` and `loot_icon_extra_10` from the 5x2 sheet.
- Resource icons: `resource_icon_01` through `resource_icon_12` from the 4x3 sheet.
- Quest frames: `quest_frame_brass`, `quest_frame_oak`, and `quest_frame_moss` are transparent ornament overlays for composing quest card variants.

The Android prototype composes a quest banner with a separate transparent frame overlay, currently `quest_banner_bandit_tax_office` plus `quest_frame_brass`.
