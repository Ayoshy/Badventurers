# Setup And Testing

Last updated: 2026-06-25

## Install First

1. Android Studio latest stable from the official Android Developers download page.
2. Git for Windows if it is not already installed.
3. A test target:
   - Android Emulator through Android Studio, or
   - a physical Android phone with developer options and USB debugging enabled.

Android Studio includes the IDE, Android SDK tooling, emulator support, Gradle integration, and a bundled JDK suitable for Android development.

## Android Studio Setup

In Android Studio:

1. Open SDK Manager.
2. Install the latest stable Android SDK Platform.
3. Install Android SDK Platform-Tools.
4. Install Android SDK Build-Tools.
5. Install Android Emulator.
6. Install at least one Google Play system image for an emulator.

Recommended emulator for early testing:

- Pixel-class phone profile.
- Recent stable Android version.
- Google Play image if testing Play Services later.

## Useful Checks

Once the Android project exists:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
.\gradlew.bat installDebug
```

For emulator or device integration tests later:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

To capture the current emulator screen into `artifacts/screenshots`:

```powershell
.\tools\capture-emulator.ps1
```

Use `-DeviceSerial` when more than one device is connected, and `-OutputDir` to change the destination folder.

## Repeatable Emulator Smoke Test

Run this checklist after UI, state, art, or gameplay-loop changes.

### 1. Prepare The Target

1. Start one emulator or connect one device.
2. Confirm exactly one ready target:

```powershell
adb devices
```

3. Build, test, and install the debug app:

```powershell
.\gradlew.bat test assembleDebug installDebug
```

4. Relaunch from a clean foreground state:

```powershell
adb shell am force-stop com.ayoshy.badventurers
adb shell monkey -p com.ayoshy.badventurers 1
```

### 2. Walk The Core Screens

Use the bottom navigation and verify each tab:

- Guild Home: Gold, Rep, Guild level, status badge, quest CTA, recommendation, and journal entries are readable.
- Quests: quest artwork is a clean landscape card; quest title, description, `Start`, and `Party` are visible above the bottom navigation.
- Heroes: recruitment desk is visible near the top; hero portraits load; roster details and rarity odds are reachable by scroll.
- Loot: selected item artwork, item details, `Equip`, `Keep`, and the inventory row are visible.
- Upgrades: upgrade names, effects, costs, next unlock, and buy CTA are readable without overlapping.

### 3. Exercise One Loop

1. Start a quest from the Quests tab.
2. Return to Guild Home and confirm the expedition state changes to running.
3. Use `Instant quest` or wait for completion.
4. Confirm Gold, Rep, loot count, and journal entries update.
5. Relaunch the app and confirm the latest resources, roster, loot, journal, and expedition state persist.

### 4. Capture Evidence

Capture at least one screenshot before and after the loop:

```powershell
.\tools\capture-emulator.ps1 -OutputDir artifacts\screenshots\smoke
```

If layout looks suspicious, capture the affected tab and keep the screenshot with a short note about device size, locale, and app state.

Optional UI dump spot check for clipped quest actions:

```powershell
adb shell uiautomator dump /sdcard/window.xml
adb pull /sdcard/window.xml artifacts\screenshots\smoke\window.xml
Select-String -Path artifacts\screenshots\smoke\window.xml -Pattern "Quests|Bandit Tax Office|Start|Party"
```

### 5. Pass Criteria

- App launches without crashing.
- Primary actions are not hidden behind the bottom navigation.
- Generated journal, loot, and hero text display through localized UI labels.
- Art assets load on every tab.
- Scrollable content remains reachable on a phone-sized viewport.
- Local state survives force-stop and relaunch.

## Later Accounts

These are not needed for the first prototype:

- Google Play Console account.
- Google AdMob account.
- Google Play Billing merchant setup.
- Firebase project, if we choose Firebase for analytics or crash reporting.

## Testing Strategy

- Unit tests for expedition formulas, loot generation, and save migration.
- Compose preview and emulator checks for UI.
- Manual smoke test for the full first loop.
- Device test before any Play Store upload.

## Official References

- Android Studio: https://developer.android.com/studio
- Install Android Studio: https://developer.android.com/studio/install
- Jetpack Compose: https://developer.android.com/compose
- Android Gradle Plugin: https://developer.android.com/build/releases/gradle-plugin
