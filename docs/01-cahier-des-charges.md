# Cahier Des Charges

Last updated: 2026-06-21

## Scope

Build a portrait Android idle RPG for public release on Google Play. The first playable version focuses on a complete core loop rather than breadth of content.

## Functional Requirements

### Core Loop

- The player owns a guild.
- The player recruits adventurers.
- The player sends a party on idle expeditions.
- Expeditions complete after a timer, including while the app is closed.
- Completed expeditions produce rewards: gold, experience, items, and journal entries.
- The player equips loot and upgrades guild facilities.
- Upgrades improve future expedition success, speed, rewards, or offline progress.

### Heroes

- Heroes have a class, level, stats, traits, and equipment slots.
- Classes should be readable and familiar: bruiser, mage, rogue, healer, ranger, bard.
- Traits create comedy and gameplay modifiers: cowardly, overconfident, allergic to slime, suspiciously lucky.

### Expeditions

- Each expedition has a name, duration, difficulty, risk, reward table, and event pool.
- Party power is compared to expedition difficulty.
- Results include success, partial success, failure, and ridiculous failure.
- Failure still gives something useful often enough to avoid frustration.

### Loot

- Items have rarity, slot, stat modifiers, and flavor text.
- Early loot should be abundant and funny.
- Rarity tiers for MVP: Common, Uncommon, Rare, Epic.

### Journal

- The journal displays recent expedition events.
- Logs should be short, skimmable, and reusable with variables.
- Logs must support English and French variants.

### Save And Offline Progress

- The game saves locally.
- Offline progress calculates completed expeditions and pending rewards.
- The MVP does not require cloud save.

### Localization

- English and French are supported from the start.
- Text content must be stored outside gameplay logic.
- Jokes may be adapted per language instead of translated literally.

### Monetization

- Rewarded ad placeholder in prototype.
- Rewarded ad use cases: double expedition loot, speed up a timer, reroll a reward, recover from a bad outcome.
- Paid use cases: remove forced ads if any are added, cosmetic guild themes, starter pack.
- No monetized system should block basic progression.

## Non-Functional Requirements

- Android portrait layout works on common phone sizes.
- App launches quickly and remains playable offline.
- UI is readable one-handed.
- State changes are deterministic enough to test.
- Gameplay formulas live in testable Kotlin classes outside UI code.
- No copyrighted parody target is referenced directly in names, lore, dialogue, or marketing.

## MVP Acceptance Criteria

- A player can start fresh, recruit or receive heroes, send a party to a quest, close/reopen the app, collect rewards, equip at least one item, and buy at least one guild upgrade.
- The app includes both English and French strings for all visible UI and sample content.
- The core loop can be tested without AdMob, Play Billing, or Play Console accounts.

## Out Of Scope For MVP

- Real-time combat.
- Multiplayer.
- Full 3D scenes.
- Voice acting.
- Cloud save.
- Live events.
- Real money purchases before the loop is validated.

