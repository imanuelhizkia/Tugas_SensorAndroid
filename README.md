# Sensor Android App

A comprehensive Android application built using Java that demonstrates the integration of multiple hardware sensors in a single project. The application utilizes the Camera, GPS Location services, and the Ambient Temperature sensor.

## Features

* Camera Integration
  * Capture photos using the system camera.
  * Uses the modern Activity Result API (TakePicturePreview contract).
  * Displays the captured image inside an ImageView on the screen.

* GPS and Location Tracking
  * Fetches real-time latitude, longitude, and provider info.
  * Uses FusedLocationProviderClient for optimal battery and accuracy.
  * Identifies the current status of the device GPS (Enabled or Disabled).

* Ambient Temperature Sensor
  * Monitors the surrounding ambient temperature in real time.
  * Displays real-time Celsius temperature values on a TextView.
  * Safely handles devices lacking an ambient temperature sensor with a fallback message.

* User Interface
  * Built using XML layout and ConstraintLayout.
  * Organizes sensor functions inside MaterialCardView elements.
  * Features a clean, responsive vertical scroll layout for accessibility.

## Tech Stack

* Programming Language: Java
* UI Layout: XML
* Minimum SDK: API 24 (Android 7.0)
* Target SDK: API 36 (Android 15)
* Dependency Injection and Version Catalog: gradle/libs.versions.toml
* Core Libraries:
  * AndroidX AppCompat
  * Material Components for Android
  * Google Play Services Location

## File Map

* Main logic: [MainActivity.java](file:///C:/Users/ASUS/AndroidStudioProjects/SensorAndroid/app/src/main/java/com/example/sensorandroid/MainActivity.java)
* UI Layout: [activity_main.xml](file:///C:/Users/ASUS/AndroidStudioProjects/SensorAndroid/app/src/main/res/layout/activity_main.xml)
* Permissions and Manifest: [AndroidManifest.xml](file:///C:/Users/ASUS/AndroidStudioProjects/SensorAndroid/app/src/main/AndroidManifest.xml)
* Version Catalog: [libs.versions.toml](file:///C:/Users/ASUS/AndroidStudioProjects/SensorAndroid/gradle/libs.versions.toml)

## Setup Instructions

1. Clone or download the repository to your local machine.
2. Open Android Studio (version Hedgehog or newer recommended).
3. Select "Open" and navigate to the project directory SensorAndroid.
4. Allow Android Studio to sync the project with Gradle files. This process automatically downloads the required dependencies including play-services-location.

## How to Run in Android Studio

1. Connect a physical Android device via USB debugging or start a Virtual Device (Emulator).
2. Note that some sensors like the ambient temperature sensor are typically only available on specific physical devices.
3. If using an Emulator, you can simulate coordinates via the Extended Controls (Location tab) and simulate temperature values via Virtual Sensors (Additional Sensors tab).
4. Click the "Run" button (green play icon) in the Android Studio toolbar or press Shift + F10.
5. Grant the runtime camera and location permissions when prompted by the app to start using the features.

## Author

* Senior Android Developer
