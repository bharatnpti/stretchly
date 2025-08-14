### Android build and run guide

This document captures the edits made to get the Android app building and the exact steps to build and run it on macOS.

### Edits made

- `android/local.properties`:
  - Set the Android SDK location to your installed SDK.
  - From:
    - `sdk.dir=/opt/android-sdk`
  - To:
    - `sdk.dir=/Users/bharatbhushan/Library/Android/sdk`

- `android/gradle.properties` (new file):
  - Enabled AndroidX and Jetifier to satisfy AndroidX dependencies.
    - `android.useAndroidX=true`
    - `android.enableJetifier=true`

- `android/app/src/main/AndroidManifest.xml`:
  - Avoided missing launcher icon resources by switching to the system default.
    - Replaced `android:icon="@mipmap/ic_launcher"` with `android:icon="@android:drawable/sym_def_app_icon"`
    - Removed `android:roundIcon` attribute

- `android/app/src/main/java/net/hovancik/stretchly/BreakSchedulerService.kt`:
  - Import and icon fixes for notifications (replaced missing drawables with a system icon):
    - Added `import android.app.Notification`
    - Replaced `.setSmallIcon(R.drawable.ic_launcher_foreground)` with `.setSmallIcon(android.R.drawable.ic_dialog_info)` in two places

### Prerequisites

- Java 17 installed (Temurin 17 or equivalent)
- Android SDK installed at `~/Library/Android/sdk` (adjust if different)
  - Build-tools 34/35 and Platforms android-34 present
- Platform-tools available on PATH (`adb`)

### Build steps (macOS)

1) Terminal: set Java 17 just for this session and go to the Android module:

```bash
cd /Users/bharatbhushan/IdeaProjects/bharatnpti/stretchly/android
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

2) Generate wrapper (already done/checked in; run once if missing):

```bash
gradle wrapper --gradle-version 8.5 --distribution-type all
```

3) Build the debug APK:

```bash
./gradlew assembleDebug
```

Output APK: `android/app/build/outputs/apk/debug/app-debug.apk`

### Run on an emulator

If you already have an AVD (e.g., `Medium_Phone_API_35`):

```bash
nohup /Users/bharatbhushan/Library/Android/sdk/emulator/emulator \
  -avd Medium_Phone_API_35 -netdelay none -netspeed full >/tmp/emulator.log 2>&1 &

adb wait-for-device
until adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; do sleep 2; done

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p net.hovancik.stretchly -c android.intent.category.LAUNCHER 1
```

If you need to create an AVD (example for API 35; adjust to available system images):

```bash
sdkmanager --install "system-images;android-35;google_apis;arm64-v8a" "platforms;android-34" "build-tools;35.0.0"
echo "no" | avdmanager create avd -n Medium_Phone_API_35 -k "system-images;android-35;google_apis;arm64-v8a"
```

### Run on a physical device

1) Enable USB debugging on the device and connect over USB

```bash
adb devices   # ensure the device is listed
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p net.hovancik.stretchly -c android.intent.category.LAUNCHER 1
```

### Notes

- The manifest will print a Gradle warning recommending removing `package="net.hovancik.stretchly"` from the manifest (namespace is already set in Gradle). This is informational and does not block the build.
- If your Android SDK lives in a different location, update `android/local.properties` accordingly, or set `ANDROID_SDK_ROOT` in your environment.

