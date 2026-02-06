# Vietnamese Learning Mobile Application

## Description
A modern Android application designed to help users master Vietnamese vocabulary through an interactive flashcard system. The application follows a mobile-cloud hybrid architecture, leveraging AWS Lambda for secure authentication and on-demand audio generation. This project was developed as the final requirement for the Mobile Programming course at the Vietnamese-German University (VGU).

## Key Features
* **Secure Authentication:** User access is controlled via email-based login and temporary authentication tokens.
* **Flashcard Management:** Full CRUD support (Create, Read, Update, Delete) allowing users to build a personalized vocabulary database stored locally using Room.
* **Smart Search & Filtering:** Users can filter their cards using exact matches or partial string searches for both English and Vietnamese terms.
* **Interactive Study Mode:** A learning interface that generates 3-card lessons chosen randomly to enhance memory retention.
* **Cloud Audio Integration:** Real-time pronunciation generation using serverless AWS functions, with local caching for offline playback.

## Technology Used
**Frontend (Mobile):** Kotlin 2.2, Jetpack Compose (Declarative UI), Jetpack DataStore, Room Persistence Library, Retrofit 2.9, Media3 ExoPlayer.

**Backend (Cloud):** Java 17, AWS Lambda (Serverless), AWS SDK for Java.

**DevOps & Tools:** Android Studio (Bumblebee+), Gradle (Kotlin DSL), KSP (Kotlin Symbol Processing), GitHub.

## Project Structure
```
./app/src/main/java/com/example/vietnameselearning/
│
├── ui.theme/            # UI Styling: Colors, Typography, and Themes
├── MainActivity.kt      # Main entry point; initializes DB and Retrofit instances
├── Navigator.kt         # Navigation Host; coordinates all screen transitions
├── Routes.kt            # Type-Safe navigation route definitions
├── FlashCardEntity.kt   # Room Database Entity, DAO, and Database configuration
├── NetworkService.kt    # Retrofit interface for AWS Lambda API endpoints
├── DataTypes.kt         # DTOs (Data Transfer Objects) for network payloads
│
└── Screens/             # UI Screen Implementations
    ├── HomeScreen.kt    # Main dashboard with feature navigation
    ├── LoginScreen.kt   # Email-based authentication request
    ├── TokenScreen.kt   # Token verification and DataStore saving
    ├── AddCardScreen.kt   # Flashcard creation logic
    ├── SearchCardScreen.kt# List view with search, edit, and delete actions
    ├── EditCardScreen.kt  # Management of existing cards and audio generation
    ├── StudyCardScreen.kt # Interactive lesson and audio playback mode
    └── FilterCardsScreen.kt# Search criteria and filter input interface
```

## How To Use The Application

### 1. Download & Install (For Android Users)
📥 **Download App:** [Click here to download the latest APK](https://github.com/lethaian29062004/Vietnamese-Learning-Mobile-Application/releases/latest)

* **On Android Devices:**
    1.  Download the `.apk` file directly to your smartphone.
    2.  Tap the file to install. (If prompted, enable "Install from Unknown Sources" in your device settings).
    3.  Launch the app and start learning!
* **On PC (Windows/Mac):**
    1.  Download and install Android Studio, or an Android Emulator such as **BlueStacks**, **LDPlayer**, **NoxPlayer**.
    2.  Drag and drop the downloaded `.apk` file into the emulator window to install.

### 2. Run From Source (For Developers)
To explore the code or modify the application:
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/lethaian29062004/Vietnamese-Learning-Mobile-Application.git](https://github.com/lethaian29062004/Vietnamese-Learning-Mobile-Application.git)
    ```
2.  **Open Project:** Launch **Android Studio** and select "Open" to navigate to the cloned folder.
3.  **Setup Emulator:**
    * Go to `Device Manager` in Android Studio.
    * Create a virtual device (Recommended: Pixel 6 or higher, API 30+).
4.  **Run:** Click the green **Run** button (Shift + F10) to build and deploy the app to the emulator.

> [!IMPORTANT]
> An active internet connection is required for the **Authentication** and **Audio Generation** features to communicate with the AWS Lambda backend.
