# Gameplay Loop

Last updated: 2026-06-25

This is the current loop to protect while adding systems.

## Core Return Loop

1. Open the Guild tab.
2. Collect an expedition result or offline report.
3. Read why the result happened: party, plan, facilities, hero specials, passive incidents.
4. Keep or discard limited reward loot.
5. Equip useful loot, recruit heroes, claim achievements, or buy a facility upgrade.
6. Pick a quest and contract plan.
7. Send the party out and leave with a visible timer/reason to return.

## Current Active Hooks

- Expedition Prep asks for party composition and contract plan selection.
- Quest Result explains deterministic causes.
- Offline Summary combines expedition completion, passive guild income, incidents, and next-action advice.
- Reward Loot creates a limited recovery choice instead of keeping everything.
- Achievements can grant one-time rewards, including recruitment tickets.

## Current Idle Hooks

- Core Crew contributes passive gold.
- Bunk Room expands crew/party/recovery capacity.
- Notice Board, Training Yard, and Bunk Room affect quest outcomes and pacing.
- Passive incidents add flavor and small rewards after offline time.

## Guardrail

Do not add systems that make the player manage every minute. Badventurers should reward short sessions and funny returns, not demand constant babysitting.
