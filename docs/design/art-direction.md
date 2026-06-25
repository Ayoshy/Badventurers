# Art Direction

Last updated: 2026-06-25

## Decision

Badventurers uses polished 2D pixel art inspired by the readability and charm of 16-bit RPGs, with modern mobile-game polish.

The game should not feel like a text app with jokes. Text carries the humor, but artwork carries the fantasy, reward feeling, and store appeal.

Reference target:

- Early style target assets are no longer active docs. Use runtime Android resources and any files under `docs/Extra Art/` as inspection references when present.

## Art Pillars

1. **Readable first**: every hero, item, and quest state must be clear on a phone screen.
2. **Premium pixel finish**: crisp pixel clusters, controlled highlights, rich lighting, and no muddy scaling.
3. **Absurd fantasy**: familiar RPG shapes, but slightly crooked, underfunded, and overconfident.
4. **Cozy disaster**: warm tavern/guild atmosphere with broken systems, suspicious paperwork, and messy loot.
5. **Animation-friendly**: assets should support small loops, bounces, sparkles, and state changes without requiring full character animation pipelines.

## Look And Feel

- 16-bit-inspired pixel art, not raw retro minimalism.
- Chunky silhouettes and exaggerated props.
- Warm interior lighting with cool shadow corners.
- Slightly glossy loot and UI highlights.
- Characters should look incompetent, not ugly.
- Humor comes from posture, props, equipment mismatch, and tiny environmental details.

## Color Direction

Use multiple color families so the app does not become a one-note brown tavern.

Core palette direction:

- Warm wood and brass for guild/tavern areas.
- Parchment and bone for UI panels.
- Moss green for primary actions and guild identity.
- Muted burgundy for danger, bad outcomes, and warnings.
- Cool blue shadows for depth and nighttime/dungeon contrast.
- Rarity colors should be restrained but satisfying.

Avoid:

- Overly beige full-screen UI.
- Dominant purple-blue gradient identity.
- Pure dark slate mobile dashboard styling.
- Neon arcade palette.
- Flat asset-pack pixel art with no lighting pass.

## Camera And Scale

Recommended mobile asset approach:

- Main scene: portrait-friendly 2D guild room background.
- Characters: three-quarter/front-facing idle sprites, large enough to show personality.
- Items: chunky icon sprites with readable silhouettes.
- Quest locations: small illustrated scene cards rather than full maps.
- UI: pixel-flavored panels, but still smooth and legible on modern Android screens.

Pixel scale:

- Author sprites at a consistent internal pixel scale.
- Export to app-ready PNG/WebP at integer scaling.
- Avoid non-integer scaling that blurs pixels.

## MVP Visual Asset List

### Must Have

- App icon.
- Guild home background.
- 4 starter hero portraits or half-body sprites.
- 3 quest location cards.
- 20 item icons.
- 4 rarity frames.
- Resource icons: gold, reputation, guild XP.
- Expedition result banners: great success, success, partial success, failure, ridiculous failure.
- Small journal/event icons.
- UI panel and button style.

### Should Have

- Hero idle animations.
- Active expedition progress vignette.
- Offline reward chest.
- Guild upgrade icons.
- Shop/no-ads visual.
- Splash/key art for store page.

### Later

- Seasonal quest backgrounds.
- Prestige guild charter artwork.
- Animated loot reveal.
- Cosmetic guild themes.
- Alternate hero skins.

## Screen-Level Art Needs

### Guild Home

Needs the strongest background art. It is the player's return screen and should feel like the identity of the game.

Visible art:

- Guild room.
- Quest board.
- Counter or desk.
- Loot pile.
- Current party sprites.
- Small animated details.

### Quest List

Use illustrated quest cards. Each quest should be recognizable by a tiny scene, not just a title.

Examples:

- Cave of Minor Regrets: damp cave entrance, one nervous torch.
- Bandit Tax Office: broken desk, coin sacks, threatening paperwork.
- Suspicious Pantry: shelves, traps, glowing cheese.

### Expedition Active

Use a compact animated vignette: party walking, torch flicker, progress meter, or silhouette scene.

### Quest Result

Needs reward theater:

- Loot reveal frame.
- Outcome banner.
- Short visual gag tied to the outcome.

### Heroes

Hero cards need portraits with strong silhouettes. Traits should be visible through props and posture.

Examples:

- Bruiser: oversized weapon, tiny helmet, heroic stance that is not justified.
- Apprentice Mage: unstable wand, scorched sleeves, too many notes.
- Rogue-ish: cloak, suspicious pockets, looking elsewhere.
- Bard Accountant: lute plus ledger, painfully confident.

### Loot

Item icons should be chunky and fun. The player should want to tap them before reading stats.

## UI Direction

The UI can have pixel flavor without being hard to read.

Use:

- Parchment-like panels with crisp borders.
- Pixel-corner buttons with modern touch sizes.
- Rarity frames around loot.
- Small icon badges for status.
- Bottom navigation with clear symbols and labels.

Avoid:

- Tiny pixel fonts for body text.
- Heavy decorative borders around every container.
- Card-inside-card layouts.
- UI that depends on long text to feel complete.

## Asset Production Workflow

1. Create style targets for the guild, heroes, loot, and quest cards.
2. Approve one style target before producing a batch.
3. Build a small asset kit for the Android prototype.
4. Test assets in the actual Compose UI on phone-sized screens.
5. Only then expand content volume.

## First Art Batch Proposal

Batch 1 should prove the visual identity:

- Guild home background.
- Four starter hero portraits.
- Three quest cards.
- Ten loot icons.
- Resource icons.

Batch 2 should support retention and monetization:

- Loot reveal frame.
- Rewarded ad double-loot visual.
- No-ads/shop art.
- Store key art.
- App icon.

## Prompt Seed

Use this as the base prompt for future concept images:

```text
Polished 16-bit pixel art 2D mobile game asset for a comedic fantasy idle RPG called Badventurers. Cozy low-budget adventurer guild, absurd fantasy props, underqualified heroes, readable phone-screen silhouettes, warm tavern lighting, moss green and brass accents, parchment UI compatibility, crisp pixel clusters, modern polish, no readable text, no watermark, no existing franchise references.
```

