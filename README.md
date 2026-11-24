# Silicon

**System-wide screen orientation control for Android — fast, minimal, and zero-bloat.**

Silicon lets you force and lock any of Android's four screen orientations across every app without root, using a lightweight `AccessibilityService` overlay, a Quick Settings tile, interactive Glance home screen widgets, and smart call/app-switch automation.

---

## Features

- **All Four Orientations**: Lock your screen to **Portrait**, **Landscape**, **Reverse Portrait**, or **Reverse Landscape** across any app — even apps that normally restrict rotation.
- **Quick Settings Tile**: Toggle orientation lock straight from the Android notification shade (with one-tap tile addition on Android 13+).
- **Home Screen Widgets**:
  - **Silicon Bar (4×1)** — compact horizontal strip with the Silicon launcher emblem and all four direct orientation buttons.
  - **Silicon Grid (2×2)** — compact pad showing the active orientation status and a 2×2 control grid.
  - Tap any orientation to lock immediately; tap the active orientation again to unlock.
- **Smart Automation**:
  - **Return on calls** — automatically switches back to portrait when an incoming or active phone call is detected (requires `READ_PHONE_STATE`).
  - **Stop on app switch** — automatically disables the orientation lock when you leave the anchored foreground app.
- **Active Lock Notification**: Optional ongoing notification while locked with quick actions to switch orientation or turn off the lock.
- **Adaptive Theming**: Supports **System**, **Light**, and **Dark** themes built with Material 3 and offline bundled typography (Inter & Newsreader).
- **Lightweight Release Builds**: R8 code and resource shrinking enabled for a compact APK footprint with zero ads, trackers, or network permissions.

---

## How It Works

Silicon uses an invisible `0×0` `TYPE_ACCESSIBILITY_OVERLAY` window managed by `SiliconAccessibilityService` with `WindowManager.LayoutParams.screenOrientation` set to the chosen `ActivityInfo.SCREEN_ORIENTATION_*` constant. Because the overlay is non-focusable and non-touchable, it enforces the requested orientation system-wide without interfering with touch input or requiring `SYSTEM_ALERT_WINDOW` ("Display over other apps") permission.

---

## Permissions & Privacy

Silicon requests **no internet permission** and collects **zero telemetry**. Every permission is strictly functional:

| Permission / Capability | Required | Purpose |
| :--- | :---: | :--- |
| **Accessibility Service** (`BIND_ACCESSIBILITY_SERVICE`) | Yes | Creates the `0×0` non-interactive orientation overlay and optionally detects app switches when **Stop on app switch** is enabled. |
| **Phone State** (`READ_PHONE_STATE`) | Optional | Detects ringing/active calls to switch back to portrait when **Return on calls** is enabled. |
| **Notifications** (`POST_NOTIFICATIONS`, Android 13+) | Optional | Displays quick controls in the notification shade while orientation lock is active. |

---

## Tech Stack

- **Language**: Kotlin (`2.4.21`, JVM 17)
- **UI**: Jetpack Compose + Material 3 (`compose-bom 2026.11.01`)
- **Widgets**: Jetpack Glance (`1.2.0`)
- **Concurrency**: Kotlin Coroutines (`1.11.0`)
- **Build System**: Gradle `9.9.2` with Android Gradle Plugin `9.5.1`
- **SDK Targets**: `minSdk 26` (Android 8.0+) · `compileSdk 37` · `targetSdk 37`

---

## Project Structure

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/silicon/
│   ├── MainActivity.kt                  # Entry point & splash screen integration
│   ├── SiliconApp.kt                    # Application initialization
│   ├── data/
│   │   ├── OrientationMode.kt           # 4 orientation modes & ActivityInfo mapping
│   │   ├── RotationState.kt             # Immutable UI/service state model
│   │   ├── SiliconPreferences.kt        # SharedPreferences persistence
│   │   └── ThemeMode.kt                 # System / Light / Dark theme modes
│   ├── service/
│   │   ├── CallStateMonitor.kt          # TelephonyCallback / PhoneStateListener wrapper
│   │   ├── RotationController.kt        # Central StateFlow controller & sync coordinator
│   │   ├── SiliconAccessibilityService.kt # Main-thread overlay & app-switch monitor
│   │   ├── SiliconActionReceiver.kt     # Notification quick-action BroadcastReceiver
│   │   ├── SiliconNotificationManager.kt # Active lock notification builder
│   │   └── SiliconTileService.kt        # Quick Settings TileService
│   ├── ui/
│   │   ├── SiliconNavApp.kt             # Navigation, permissions, and banner host
│   │   ├── components/                  # Reusable cards, phone icon, and logo
│   │   ├── screens/                     # HomeScreen, SettingsScreen, SetupScreen
│   │   └── theme/                       # Color schemes, typography, and theme setup
│   ├── util/
│   │   └── SystemIntegration.kt         # Accessibility, permissions, tile & widget pinning
│   └── widget/
│       ├── SiliconBarWidget.kt          # 4×1 Glance Bar widget
│       ├── SiliconSquareWidget.kt       # 2×2 Glance Grid widget
│       ├── SiliconWidgetActions.kt      # Glance ActionCallback orientation handler
│       ├── SiliconWidgetTheme.kt        # Glance Material 3 color providers
│       └── SiliconWidgetUpdater.kt      # Async widget state synchronizer
└── res/
    ├── drawable/                        # Vector emblems, phone icons, widget backgrounds
    ├── font/                            # Bundled Inter & Newsreader fonts
    ├── layout/                          # RemoteViews widget preview layouts
    ├── values/ & values-night/          # Strings, themes, and adaptive widget colors
    └── xml/                             # Accessibility service & widget provider configs
```

---

## Building Locally

### Prerequisites

- **JDK 21** (Temurin or equivalent)
- **Android SDK** with API 37 platform and build tools installed
- **Gradle 9.9.2** (or Android Studio)

### Debug Build

```bash
git clone https://github.com/ixmuted/Silicon.git
cd Silicon
gradle :app:assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Signed Release Build

Set the signing environment variables and run `:app:assembleRelease`:

```bash
export KEYSTORE_PATH=/path/to/release.jks
export KEYSTORE_PASSWORD="your-store-password"
export KEY_ALIAS="your-key-alias"
export KEY_PASSWORD="your-key-password"

gradle :app:assembleRelease
```

The minified, resource-shrunk release APK is generated at `app/build/outputs/apk/release/app-release.apk`.

---

## CI/CD (GitHub Actions)

Every push to `main` (or manual `workflow_dispatch`) triggers `.github/workflows/build.yml`, which builds and uploads `Silicon.apk` as a workflow artifact:

- When repository secrets `SIGNING_KEYSTORE_BASE64`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, and `SIGNING_KEY_PASSWORD` are configured, CI decodes the keystore and builds a signed `:app:assembleRelease` APK.
- Otherwise, CI automatically falls back to building `:app:assembleDebug`.

---

## Third-Party Assets

Font licensing details for bundled typography are documented in [THIRD_PARTY_FONTS.md](THIRD_PARTY_FONTS.md).

---

## License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.
