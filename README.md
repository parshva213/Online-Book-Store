# OnlineBookstore

Android app "OnlineBookstore" — a simple bookstore app project built with Android Studio and Gradle.

## Project structure
- Root contains Gradle configuration and the `app/` module which holds the Android application.

## Prerequisites
- Java JDK 11 or later
- Android Studio (recommended) with matching Android SDK
- Gradle (wrapper included)

## Build & Run
Open the project in Android Studio and run the `app` module on an emulator or device.

From the repository root (Windows PowerShell):

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Or on Unix/macOS (if you use WSL or similar):

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Tests
If unit or instrumentation tests are present, run:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Contributing
Feel free to open issues or pull requests. Follow typical Android/Kotlin style and include small, focused changes.

## License
Add a license file or state the license here.

## Contact
Project owner / maintainer: update this README with your name and contact details.
