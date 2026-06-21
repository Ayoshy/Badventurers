# ADR 0001: Android Native With Kotlin And Jetpack Compose

Date: 2026-06-21

## Status

Accepted for MVP.

## Context

Badventurers is an Android-first idle RPG with mostly UI-driven gameplay: timers, lists, upgrades, loot, journal text, localization, ads, purchases, and local persistence. It does not require physics-heavy 2D gameplay, real-time combat, or complex animation in the MVP.

## Decision

Use native Android with Kotlin and Jetpack Compose.

## Rationale

- Strong fit for UI-heavy mobile gameplay.
- Direct integration with Android, Play Billing, AdMob, notifications, and local persistence.
- Smaller runtime and simpler store path than Unity for this kind of game.
- Testable domain logic can stay in plain Kotlin.
- Compose supports fast iteration for the guild, roster, inventory, and quest screens.

## Consequences

- We need to create simple custom visuals or use lightweight assets rather than relying on an engine editor.
- If the game later needs richer animation, we can add libraries or targeted canvas rendering without changing the whole stack.
- The project should keep gameplay logic independent from Compose UI to avoid making balancing and tests painful.

