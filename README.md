# Badventurers

Badventurers is an Android-first idle incremental RPG about a deeply questionable fantasy guild. Players recruit flawed adventurers, send them on automated expeditions, read absurd combat logs, collect loot, upgrade the guild, and come back later to discover what went wrong while they were away.

The game is designed as a small, shippable mobile project: short sessions, offline progress, bilingual content from day one, and monetization that stays optional.

## Product Direction

- Platform: Android first.
- Orientation: portrait.
- Genre: idle / incremental RPG / loot game.
- Tone: absurd fantasy, incompetent heroes, dry management humor.
- Languages: English and French.
- Monetization: rewarded ads, no-ads purchase, optional cosmetic and starter packs.
- First milestone: playable vertical slice with guild, heroes, expedition timer, loot, journal, and local save.

## Documentation

Start with [Docs index](docs/README.md) and [Agent context](AGENTS.md).

Active docs are organized by purpose:

- Product: [Vision](docs/product/vision.md), [Requirements](docs/product/requirements.md), [Kanban](docs/product/kanban.md), [Monetization guardrails](docs/product/monetization-guardrails.md)
- Engineering: [Setup and testing](docs/engineering/setup-test.md), [Workflow](docs/engineering/workflow.md), [Stack decision](docs/decisions/0001-stack-android-native-compose.md)
- Design: [Gameplay loop](docs/design/gameplay-loop.md), [UX screen map](docs/design/ux-screen-map.md), [Art direction](docs/design/art-direction.md), [Progression](docs/design/progression.md)
- Content/data: [Hero and item catalog](docs/content/hero-item-catalog.md), [Quest catalog](docs/content/quest-catalog.md), [CSV data mirrors](docs/data/README.md)

## Target Tech Stack

- Kotlin
- Jetpack Compose
- Gradle / Android Gradle Plugin
- Snapshot-based local persistence with SharedPreferences JSON; DataStore or Room only if save complexity grows
- AdMob and Google Play Billing after the core loop is fun

## Development Status

The Android Compose prototype is buildable and contains the current playable loop: Guild, Quests, Heroes, Loot, and Upgrades tabs; 12 playable quests; Expedition Prep with generic and quest-specific contract plans; result causality cards; offline return summaries with passive income/incidents; reward loot recovery choices; hero recruitment, recruitment tickets, detail, XP, and equipment flows; implemented Notice Board, Training Yard, and Bunk Room upgrades; achievements; generated journal/loot; art-backed UI; and snapshot save/load. See [Kanban](docs/product/kanban.md) and [Agent context](AGENTS.md) for the current next steps.