# Competitive Research

Last updated: 2026-06-22

## Scope

This note captures a quick competitor scan for Badventurers: Android-first idle/incremental RPG, guild management, automated expeditions, funny logs, loot, upgrades, and ethical monetization.

## Closest References

### Soda Dungeon 2

Why it matters: closest neighbor. Steam tags include Free to Play, RPG, Idler, Dungeon Crawler, Pixel Graphics, Incremental, Fantasy, and Comedy.

Useful patterns:

- Tavern/home-base fantasy: the player stays safe while questionable adventurers do the dirty work.
- Repeat loop: hire party, send into danger, bring back loot, upgrade tavern/town/armory, repeat.
- AFK rewards: players earn resources while away and redeem them on return.
- Optional automation: auto-combat and custom AI patterns reduce friction while preserving setup choices.
- Trust positioning: the store page explicitly promises no timers, lives systems, paywall, or loot boxes.

Do not copy directly:

- Soda/tavern framing, Soda Script naming, Dark Lord setup, and specific character/item jokes.
- Its exact dungeon-crawler structure. Badventurers should stay more guild-manager/report-driven.

Source: https://store.steampowered.com/app/946050/Soda_Dungeon_2/

### Shop Titans

Why it matters: strongest shop/economy reference near the fantasy-hero loop.

Useful patterns:

- Economic fantasy is clear in one sentence: craft, stock, sell to heroes at a markup.
- Heroes and crafting feed each other: heroes gather materials, crafted items improve future output.
- Shop customization gives visible progression beyond numbers.
- Many item categories and hero classes create long-term collection goals.

Do not copy directly:

- Heavy shopkeeper focus. Badventurers is a guild manager, not primarily a merchant simulator.
- Aggressive monetization feel. Badventurers should use clarity and restraint as a differentiator.

Source: https://store.steampowered.com/app/1258080/Shop_Titans/

### Dungeon Village 2

Why it matters: compact pixel management reference with town growth and adventurers.

Useful patterns:

- Visible hub growth makes progression feel physical.
- Cute, readable pixel art can support management depth without becoming visually noisy.
- Recruiting adventurers and monsters gives a broader fantasy toybox.

Do not copy directly:

- Full town/city-builder scope. Badventurers should focus the MVP on guild, quests, heroes, loot, and upgrades.

Source: https://store.steampowered.com/app/1983710/Dungeon_Village_2/

### Melvor Idle / Realm Grinder

Why they matter: examples of deep idle systems.

Useful patterns:

- Long-tail progression works when each system has a clear place.
- Automation, resource chains, and skill-like tracks can create months of goals.

Do not copy directly:

- Table-heavy/minimal UI for MVP. Badventurers needs character, comedy, and art as first-class retention hooks.
- Too much early system depth. Start with a funny repeatable loop, then deepen.

Sources:

- https://store.steampowered.com/app/1267910/Melvor_Idle/
- https://store.steampowered.com/app/610080/Realm_Grinder/

### AFK Journey / Idle Champions

Why they matter: larger-scale idle/AFK RPG references with hero collection, formations, and live-service retention.

Useful patterns:

- Shared leveling/equipment reduces roster friction and encourages experimentation.
- Clear formation/party strategy makes automated combat feel like a player decision.
- Seasonal/live content can refresh the same core loop later.

Do not copy directly:

- Large roster burden, gacha pressure, confusing currencies, and live-service complexity before the core loop is fun.

Sources:

- https://store.steampowered.com/app/4195600/AFK_Journey/
- https://store.steampowered.com/app/627690/Idle_Champions_of_the_Forgotten_Realms/

## Product Opportunities

1. Make the quest result the emotional center.

The result screen should feel like an absurd guild incident report, not a plain reward receipt. It should show outcome, short journal summary, loot, injuries/complications, and a clear next action.

2. Make failure profitable and memorable.

Bad outcomes should generate jokes, pity rewards, achievements, or future unlocks. A Ridiculous Failure should make the player laugh and still want to launch the next expedition.

3. Tie traits to visible behavior.

Traits should not only modify formulas. They should appear in logs, risk notes, and result explanations so players learn the roster as a cast.

4. Show guild upgrades in the hub.

Facilities such as Notice Board, Training Rug, Accountant Stool, or similar upgrades should eventually alter the Guild Home art or badges. Visible change is stronger than a hidden percentage.

5. Make loot instantly understandable.

Every item detail should answer: what is it, why is it funny, who should use it, and why is it better or worse than the current option.

6. Make Expedition Prep a small tactical ritual.

Before launch, show party slots, success estimate, risk tags, reward preview, and one flavorful warning. The player should feel clever without doing spreadsheet work.

7. Make Offline Summary a return ritual.

Returning should feel like opening a report from a guild that barely survived the player's absence: rewards, incidents, completed expeditions, and one recommended next spend.

8. Use ethical monetization as positioning.

Badventurers can stand apart by being explicit: rewarded ads are optional boosts, no-ads is straightforward, cosmetics are safe, and power/randomized paid systems are avoided.

## Monetization Warnings

Avoid paid randomized power. The market is increasingly hostile to confusing gacha, stamina walls, multi-currency obfuscation, and paywalls. Recent coverage around gacha RPGs highlights player pushback against stamina and banners, while FTC action around Genshin Impact emphasized children, odds, real costs, and multi-tier currencies.

Design rules for Badventurers:

- Do not sell paid random hero or item pulls.
- If any random recruitment-like system exists, disclose odds clearly and make it earnable through play.
- Keep duplicate handling generous and understandable.
- Avoid multi-step premium currency conversions.
- Keep rewarded ads optional and attached to bonuses, not core recovery from frustration.
- Prefer no-ads, cosmetics, starter convenience, and direct purchases over chance-based monetization.

Relevant reading:

- https://www.gamesradar.com/games/action-rpg/a-gacha-rpg-is-cutting-all-the-gacha-garbage-so-it-can-just-be-a-better-game-were-completely-removing-all-character-and-weapon-banners/
- https://www.gamesradar.com/games/action-rpg/arknights-endfield-lead-says-good-gacha-systems-dont-impact-the-players-ability-to-enjoy-the-gameplay-and-endfield-is-trying-to-fix-its-confusing-gacha/
- https://www.theverge.com/2025/1/18/24346862/genshin-impact-developer-20-million-fine-ftc-data-lootboxes
- https://en.wikipedia.org/wiki/Loot_box

## MVP Implications

Priority order suggested by this research:

1. Dedicated Quest Result screen.
2. Expedition Prep with party choice, success estimate, risk tags, and reward preview.
3. Offline Summary.
4. Item Detail with best-hero suggestion and equip/sell/keep actions.
5. Hero Detail with trait-driven identity.
6. Visible guild upgrade effects on Guild Home.
7. Fake rewarded ad flow with strict optionality.
8. Store/no-ads/cosmetic planning only after the loop feels good.

## Positioning

Badventurers should not try to out-depth every idle RPG at MVP. It should win on return-session flavor:

> Open the app because one more upgrade is reachable, and because you want to know what profitable nonsense the party did this time.
