# Android Scaffold

Last updated: 2026-06-21

## Status

The repository now contains a first Android/Kotlin/Compose scaffold for the Badventurers visual slice.

This scaffold is ready to open in Android Studio, but it has not been compiled locally yet because this machine did not have Java, Gradle, ADB, or Android SDK available at the time it was created.

## Added Structure

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
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

## After Installing Android Studio And JDK

From a new PowerShell:

```powershell
java -version
adb version
```

Then open the repository root in Android Studio:

```text
C:\Users\Ayo\Documents\Project\Not Tunic\Badventurers
```

Let Android Studio sync the Gradle project.

## Gradle Wrapper Note

The Gradle build files are present, but the Gradle Wrapper has not been generated yet in this repo. Once the local Android toolchain is available, generate and commit the wrapper so everyone can use:

```powershell
.\gradlew test
.\gradlew assembleDebug
.\gradlew installDebug
```

If Android Studio does not generate it automatically, install or expose Gradle temporarily, then run:

```powershell
gradle wrapper --gradle-version 9.4.1
```

After that, the wrapper files should be committed.

