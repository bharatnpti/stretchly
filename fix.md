### Android port issues and fixes

This document lists each issue encountered while getting the Android app to build and run, the applied fix, and a brief explanation.

### 1) Android SDK path not set

- **Issue**: Gradle could not locate the Android SDK.
- **Fix**: Set `sdk.dir` in `android/local.properties`.
- **Details**:
  - `sdk.dir=/Users/bharatbhushan/Library/Android/sdk`
- **Why**: Gradle needs the SDK location to resolve build tools and platforms.

### 2) AndroidX requirements

- **Issue**: Dependencies require AndroidX; legacy support artifacts caused conflicts.
- **Fix**: Enabled AndroidX and Jetifier in `android/gradle.properties`.
- **Details**:
  - `android.useAndroidX=true`
  - `android.enableJetifier=true`
- **Why**: AndroidX replaces old support libraries; Jetifier migrates transitive artifacts.

### 3) Missing launcher icon resources

- **Issue**: Manifest referenced non-existent mipmap icons which can break packaging/installation on clean envs.
- **Fix**: Switched to the system default icon; removed `roundIcon`.
- **Details** (file `android/app/src/main/AndroidManifest.xml`):
  - Replaced `android:icon="@mipmap/ic_launcher"` with `android:icon="@android:drawable/sym_def_app_icon"`.
  - Removed `android:roundIcon`.
- **Why**: Avoids resource-not-found errors without adding a full icon set.

### 4) Notification icon and imports in `BreakSchedulerService`

- **Issue**: Notification used a missing drawable; missing import for `Notification`.
- **Fix**: Used a system icon and added the import.
- **Details** (file `android/app/src/main/java/net/hovancik/stretchly/BreakSchedulerService.kt`):
  - `setSmallIcon(android.R.drawable.ic_dialog_info)`
  - `import android.app.Notification`
- **Why**: Ensures the foreground notification is valid at runtime.

### 5) Crash on Android 14: Missing foreground service type

- **Issue**: App crashed at launch with `MissingForegroundServiceTypeException` when starting `BreakSchedulerService` (target SDK 34).
- **Fixes**:
  - Start foreground with an explicit type on Android 10+.
  - Declare the same type in the manifest.
  - Add the matching runtime permission.
- **Changes**:
  - `BreakSchedulerService.kt`:
    - Use `startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)` on API >= 29; fallback to 2-arg call on older APIs.
  - `AndroidManifest.xml`:
    - On the service: `android:foregroundServiceType="dataSync"`.
    - Permissions:
      - `android.permission.FOREGROUND_SERVICE`
      - `android.permission.FOREGROUND_SERVICE_DATA_SYNC`
- **Why**: From Android 14, every Foreground Service must declare a specific type and hold the corresponding permission.

### 6) Emulator not on PATH

- **Issue**: `emulator` CLI was not found when trying to list/start AVDs.
- **Fix**: Used the absolute path from the local SDK to start AVD `Medium_Phone_API_35`.
- **Details**:
  - `/Users/bharatbhushan/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_35`
- **Why**: Ensures a device is available to install/run the app without modifying global PATH.

### Notes

- Manifest warning about `package="net.hovancik.stretchly"` being ignored is informational. Optional cleanup would be to remove the `package` attribute and rely on the Gradle `namespace`.


