#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Function to print status messages
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

# Function to print error messages
print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

# Function to print warning messages
print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Make the script executable (in case it isn't already)
chmod +x "$0"

# Check if adb is available
if ! command -v adb &> /dev/null; then
    print_error "ADB is not installed or not in PATH. Please install Android SDK platform tools."
fi

print_status "ADB is available."

# Check if any Android device is connected
DEVICE_COUNT=$(adb devices | grep -v "List" | grep -v "^$" | wc -l)
if [ "$DEVICE_COUNT" -eq 0 ]; then
    print_error "No Android devices connected. Please connect a device via USB and enable USB debugging."
fi

# Get the device name for better output messages
DEVICE_NAME=$(adb devices -l | grep "device product" | head -1 | awk '{print $6}' | cut -d':' -f2)
if [ -z "$DEVICE_NAME" ]; then
    DEVICE_NAME="device"
fi

print_status "Found Android device: $DEVICE_NAME"

# Build the debug variant
print_status "Building debug variant..."
if ! ./gradlew assembleDebug; then
    print_error "Failed to build the debug variant."
fi

print_status "Build completed successfully."

# Install the app
print_status "Installing app on device..."
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    print_error "Debug APK not found at $APK_PATH. Build may have failed."
fi

if ! adb install -r "$APK_PATH"; then
    print_error "Failed to install the app on the device."
fi

print_status "App installed successfully."

# Launch the app
print_status "Launching app..."
if ! adb shell am start -n "ng.mint.ocrscanner/.MainActivity"; then
    print_warning "Failed to launch the app. The activity name might be different."
    print_status "Trying alternative launch method..."
    
    # Try launching using the package name only
    if ! adb shell monkey -p ng.mint.ocrscanner -c android.intent.category.LAUNCHER 1; then
        print_error "Failed to launch the app."
    fi
fi

print_status "App launched successfully!"
print_status "Development workflow completed."

