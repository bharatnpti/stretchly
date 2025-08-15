#!/usr/bin/env bash
set -euo pipefail

APP_ID="net.hovancik.stretchly"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/debug/app-debug.apk"
AVD_NAME_DEFAULT="Medium_Phone_API_35"

print_usage() {
  echo "Usage: $(basename "$0") [--avd NAME] [--create-avd]"
  echo
  echo "Builds the debug APK, ensures an emulator is running, installs, and launches the app."
  echo
  echo "Options:"
  echo "  --avd NAME      Use a specific AVD name (default: $AVD_NAME_DEFAULT)"
  echo "  --create-avd    Create the AVD if it doesn't exist (API 35 google_apis arm64-v8a)"
}

AVD_NAME="${AVD_NAME:-$AVD_NAME_DEFAULT}"
CREATE_AVD="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --avd)
      AVD_NAME="$2"; shift 2 ;;
    --create-avd)
      CREATE_AVD="true"; shift ;;
    -h|--help)
      print_usage; exit 0 ;;
    *)
      echo "Unknown option: $1" >&2
      print_usage; exit 1 ;;
  esac
done

ensure_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Error: required command '$1' not found in PATH" >&2
    exit 1
  fi
}

# Resolve Android SDK path
SDK_DIR=""
if [[ -f "$SCRIPT_DIR/local.properties" ]]; then
  SDK_DIR=$(grep -E '^sdk.dir=' "$SCRIPT_DIR/local.properties" | sed 's#sdk.dir=##')
fi
SDK_DIR=${SDK_DIR:-${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}}
if [[ -z "${SDK_DIR}" ]]; then
  echo "Error: Could not determine Android SDK dir. Set ANDROID_SDK_ROOT or add sdk.dir in android/local.properties" >&2
  exit 1
fi

EMULATOR_BIN="$SDK_DIR/emulator/emulator"
ADB_BIN="${ADB_BIN:-adb}"
SDKMANAGER_BIN="${SDKMANAGER_BIN:-sdkmanager}"
AVDMANAGER_BIN="${AVDMANAGER_BIN:-avdmanager}"

ensure_cmd "$ADB_BIN"
if [[ ! -x "$EMULATOR_BIN" ]]; then
  echo "Warning: $EMULATOR_BIN not found or not executable. Falling back to 'emulator' from PATH."
  EMULATOR_BIN="emulator"
  ensure_cmd "$EMULATOR_BIN"
fi

# Ensure Java 17 for Gradle
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"

echo "[1/5] Building debug APK..."
"$SCRIPT_DIR/gradlew" assembleDebug

if [[ ! -f "$APK_PATH" ]]; then
  echo "Error: APK not found at $APK_PATH" >&2
  exit 1
fi

avd_exists() {
  "$EMULATOR_BIN" -list-avds | grep -Fxq "$AVD_NAME"
}

running_emulator_serial() {
  "$ADB_BIN" devices | awk '/^emulator-/{print $1; exit}'
}

start_emulator() {
  echo "Starting emulator: $AVD_NAME"
  nohup "$EMULATOR_BIN" -avd "$AVD_NAME" -netdelay none -netspeed full \
    > /tmp/emulator.log 2>&1 &
}

wait_for_boot() {
  echo "Waiting for device..."
  "$ADB_BIN" wait-for-device
  echo "Waiting for Android to finish booting..."
  until "$ADB_BIN" shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; do sleep 2; done
}

maybe_create_avd() {
  if [[ "$CREATE_AVD" != "true" ]]; then
    return
  fi
  if avd_exists; then
    echo "AVD '$AVD_NAME' already exists."
    return
  fi
  echo "Creating AVD '$AVD_NAME' (this may take a while)..."
  ensure_cmd "$SDKMANAGER_BIN"
  ensure_cmd "$AVDMANAGER_BIN"
  "$SDKMANAGER_BIN" --install "system-images;android-35;google_apis;arm64-v8a" "platforms;android-34" "build-tools;35.0.0"
  echo "no" | "$AVDMANAGER_BIN" create avd -n "$AVD_NAME" -k "system-images;android-35;google_apis;arm64-v8a"
}

echo "[2/5] Ensuring emulator is running..."
maybe_create_avd

serial="$(running_emulator_serial || true)"
if [[ -z "$serial" ]]; then
  if ! avd_exists; then
    echo "Error: AVD '$AVD_NAME' not found. Re-run with --create-avd or create one manually." >&2
    echo "Hint: $SDKMANAGER_BIN --install 'system-images;android-35;google_apis;arm64-v8a' && echo 'no' | $AVDMANAGER_BIN create avd -n $AVD_NAME -k 'system-images;android-35;google_apis;arm64-v8a'" >&2
    exit 1
  fi
  start_emulator
  wait_for_boot
else
  echo "Found running emulator: $serial"
fi

echo "[3/5] Installing APK..."
"$ADB_BIN" install -r "$APK_PATH"

echo "[4/5] Launching app ($APP_ID)..."
"$ADB_BIN" shell monkey -p "$APP_ID" -c android.intent.category.LAUNCHER 1

echo "[5/5] Done. Logs: /tmp/emulator.log"


