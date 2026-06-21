# Setup And Testing

Last updated: 2026-06-21

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
.\gradlew test
.\gradlew assembleDebug
.\gradlew installDebug
```

For emulator or device integration tests later:

```powershell
.\gradlew connectedDebugAndroidTest
```

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

