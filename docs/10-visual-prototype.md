# Visual Prototype

Last updated: 2026-06-21

## Status

Batch 01 is approved as the current art direction target.

The next step is to use the generated art in a visual prototype before cutting final Android assets. This reduces the risk of building a Compose UI around assets that look good in isolation but fail at phone scale.

## Prototype File

- `docs/mockups/mobile-flow-art.html`

This file is a clickable HTML mockup using Batch 01 artwork:

- Guild Home background
- starter hero sheet
- quest card sheet
- loot icon sheet
- resource/status icon sheet

## Why This Step

The original `mobile-flow.html` validates screen logic. The new art-integrated mockup validates:

- visual density;
- contrast and readability;
- whether the guild background supports UI overlays;
- whether the art direction feels mobile-game-ready;
- how much slicing/cleanup the generated sheets need before Android integration.

## Android Environment Check

Current machine check:

- `java`: missing
- `gradle`: missing
- `adb`: missing
- `ANDROID_HOME`: missing
- `ANDROID_SDK_ROOT`: missing

Android scaffolding can still be planned, but local build/test needs Android Studio or equivalent SDK/JDK setup first.

## Implementation Notes

For the Android prototype:

- use Kotlin and Jetpack Compose;
- keep game/domain logic outside UI code;
- use Kotlin 2.x with the Compose Compiler Gradle plugin;
- use Android Gradle Plugin 9.2+ once the installed Android Studio version supports it;
- target portrait phone layout first.

## Next Asset Work

Before final app integration:

1. Slice the hero sheet into four portraits.
2. Slice the quest sheet into three quest cards.
3. Slice the loot sheet into separate item icons.
4. Slice the resource sheet into separate UI icons.
5. Normalize output sizes and file names.
6. Compress PNG/WebP assets for Android.
7. Test every asset inside Compose at phone scale.

