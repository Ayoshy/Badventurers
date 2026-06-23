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

Generated Android-ready resources are split across Android resource roots declared in `app/build.gradle.kts`:

- `app/src/main/res-guild/drawable-nodpi/`: guild backgrounds.
- `app/src/main/res-heroes/drawable-nodpi/`: hero portraits and hero UI art.
- `app/src/main/res-loot/drawable-nodpi/`: semantic loot icons. Runtime icons must have truthful names, e.g. `loot_icon_boots` shows boots.
- `app/src/main/res-quests/drawable-nodpi/`: quest banners, staging quest card slices, and quest frames.
- `app/src/main/res-resources/drawable-nodpi/`: resource/status icons.
- `app/src/main/res-ui/drawable/`: vector or XML UI slots.

Source sheets that are not needed at runtime live in `docs/art/android-source-sheets/` so they do not bloat the APK. The older `quest_card_*` row slices are staging assets and should not be used directly in Compose.

The Android prototype composes a quest banner with a separate transparent frame overlay, currently `quest_banner_bandit_tax_office_v2` plus `quest_frame_brass`.
