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

- [Agent context](AGENTS.md)
- [Vision](docs/00-vision.md)
- [Cahier des charges](docs/01-cahier-des-charges.md)
- [Game design](docs/02-game-design.md)
- [Roadmap](docs/03-roadmap.md)
- [Kanban](docs/04-kanban.md)
- [Setup and testing](docs/05-setup-test.md)
- [Tools and workflow](docs/06-outils-workflow.md)
- [Stack decision](docs/decisions/0001-stack-android-native-compose.md)
- [UX flow](docs/08-ux-flow.md)
- [Art direction](docs/09-art-direction.md)
- [Visual prototype](docs/10-visual-prototype.md)
- [Android scaffold](docs/11-android-scaffold.md)
- [Gameplay screen loop](docs/12-gameplay-screen-loop.md)
- [Competitive research](docs/13-competitive-research.md)

## Target Tech Stack

- Kotlin
- Jetpack Compose
- Gradle / Android Gradle Plugin
- Local persistence first, likely DataStore or Room depending on save complexity
- AdMob and Google Play Billing after the core loop is fun

## Development Status

The Android Compose prototype is buildable and contains the first playable loop: Guild, Quests, Heroes, Loot, and Upgrades tabs; expedition timer/result flow; generated loot and journal entries; Batch 01 art; and local persistence work in progress. See [Kanban](docs/04-kanban.md) and [Agent context](AGENTS.md) for the current next steps.

