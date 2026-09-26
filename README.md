# Alphabet Launcher

A minimal Android home screen with an interactive A–Z sidebar. Touch the letter bar, and it bends toward your finger while the matching apps appear.

## Features

- Live clock, date, and favourite apps on the home screen
- Real installed apps with icons, loaded once via PackageManager
- A–Z sidebar with custom bend animation drawn on Canvas
- Letter bubble follows the finger while dragging
- Filtered app list per letter, persists after release
- Haptic tick on every letter change
- Can be set as the device default launcher

## Requirements

- JDK 17
- Android SDK with platform 34
- An Android device with USB debugging, or an emulator

## Run

Connect the device, then from the project root:

```bat
.\gradlew.bat installLauncher
```

This builds the debug APK, installs it, and sets Alphabet Launcher as the default HOME app.

Other useful commands:

```bat
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

To target a specific device when several are connected:

```bat
set ANDROID_SERIAL=<serial>
.\gradlew.bat installLauncher
```

## Set as default launcher manually

Settings → Default apps → Home app → Alphabet Launcher. Or press the system Home button and pick it from the chooser.

## Dependencies

| Library | Version | Why |
|---|---|---|
| Android Gradle Plugin | 8.5.2 | Builds the APK |
| Kotlin (android + compose plugins) | 2.0.21 | Language + Compose compiler |
| Compose BOM | 2024.09.00 | Pins one consistent Compose version set |
| androidx.core:core-ktx | 1.13.1 | Drawable `toBitmap` for app icons |
| androidx.activity:activity-compose | 1.9.2 | `setContent` and edge-to-edge in MainActivity |
| androidx.compose.ui | BOM | Layout, Canvas, touch input |
| androidx.compose.foundation | BOM | LazyColumn, gestures, Canvas drawing |
| androidx.compose.material3 | BOM | Text and dark theme |

## Project structure

```
app/src/main/java/com/example/alphabetlauncher/
  MainActivity.kt    Activity setup only
  HomeScreen.kt      Clock, date, favourites, filtered list, haptics
  AlphabetBar.kt     A–Z drawing, touch handling, bend math, bubble
  AppRepository.kt   PackageManager discovery, sort, group by letter
  AppInfo.kt         Installed-app data class
  AppLauncher.kt     Launch apps by package name
```

No ViewModel, DI, database, networking, or third-party animation libraries.
