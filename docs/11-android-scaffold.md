# Android Scaffold

Last updated: 2026-06-21

## Status

The repository contains a first Android/Kotlin/Compose scaffold for the Badventurers visual slice.

This scaffold now compiles locally with the Gradle Wrapper. Unit tests and the debug APK build have passed on the local Windows Android setup.

## Added Structure

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
- `gradlew` / `gradlew.bat`
- `gradle/wrapper/*`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/ayoshy/badventurers/MainActivity.kt`
- `app/src/main/java/com/ayoshy/badventurers/ui/BadventurersApp.kt`
- `app/src/main/java/com/ayoshy/badventurers/game/*`
- `app/src/test/java/com/ayoshy/badventurers/game/*`
- `app/src/main/res/drawable-nodpi/*`
- `app/src/main/res/values/*`
- `app/src/main/res/values-fr/*`

## Current App Behavior

The app is a Compose visual prototype with:

- portrait-only activity;
- Guild / Quests / Heroes / Loot / Upgrades tabs;
- Batch 01 artwork loaded from Android resources;
- English and French string resources;
- starter heroes and quest data;
- an expedition resolution engine;
- unit tests for high success and failure pity rewards.

## Versions

- Android Gradle Plugin: `9.2.0`
- Kotlin: `2.4.0`
- Compose BOM: `2026.06.00`
- Activity Compose: `1.13.0`
- Compile SDK: `37`
- Min SDK: `26`
- Target SDK: `37`
- JDK toolchain: `17`
- Gradle Wrapper: `9.4.1`

## SDK Notes

The local setup initially had `android-36.1` installed. During the first Gradle build, the Android Gradle Plugin automatically installed the missing Android SDK Platform `android-37.0` through the configured SDK.

No manual SDK downgrade was needed.

## Verified Commands

From the repository root:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

Both commands passed locally.

Generated debug APK:

```text
app\build\outputs\apk\debug\app-debug.apk
```

To install on a connected device or running emulator:

```powershell
.\gradlew installDebug
```