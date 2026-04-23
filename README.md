# QFix - Civic Complaint Management System

![QFix Banner](app/src/main/res/drawable/splash_background.xml)

**Report it. Track it. Fix it.**

QFix is a comprehensive civic complaint management Android application that bridges the gap between citizens and municipal authorities. Citizens can easily report issues like potholes, drainage problems, broken street lights, etc., while authorities can efficiently manage, track, and resolve these complaints.

## Features

### For Citizens
- **Easy Registration & Login**: Simple sign-up process with email or Google
- **Multi-step Complaint Reporting**: Capture photos, add details, and tag location
- **Real-time Tracking**: Monitor complaint status with live updates
- **Feedback System**: Rate and provide feedback on resolved complaints
- **Multilingual Support**: Available in English and Hindi
- **Dark Mode**: Eye-friendly dark theme option

### For Authorities
- **Dashboard Analytics**: Visualize complaint statistics and trends
- **Complaint Management**: Efficiently assign, update, and resolve complaints
- **Status Updates**: Keep citizens informed with regular progress updates
- **Department Assignment**: Automatic routing to appropriate departments
- **Performance Metrics**: Track resolution times and team performance
- **Multilingual Support**: Available in English and Hindi
- **Dark Mode**: Eye-friendly dark theme option

## Tech Stack

- **Language**: Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: XML Layouts (Not Jetpack Compose)
- **Database**: 
  - Firebase Firestore (Cloud)
  - Room Database (Local Cache)
- **Authentication**: Firebase Authentication
- **Storage**: Firebase Storage (for images)
- **Dependency Injection**: None (Manual DI)
- **Asynchronous Programming**: LiveData, ViewModel
- **Networking**: Firebase SDK
- **Image Loading**: Glide
- **Animations**: Lottie, Custom XML animations

## Screenshots

| Splash Screen | Onboarding | Role Selection |
|:-------------:|:----------:|:--------------:|
| ![Splash](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png) | ![Onboarding](app/src/main/res/raw/splash_animation.json) | ![Role](app/src/main/res/drawable/signup_header_gradient.xml) |

| Citizen Home | Complaint Reporting | Authority Dashboard |
|:------------:|:-------------------:|:-------------------:|
| ![Home](app/src/main/res/drawable/citizen_card_gradient.xml) | ![Report](app/src/main/res/drawable/photo_indicator_background.xml) | ![Dashboard](app/src/main/res/drawable/authority_card_gradient.xml) |

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Android Studio Flamingo or later
- JDK 8 or later
- Minimum SDK version: 24 (Android 7.0)
- Target SDK version: 34 (Android 14)
- Firebase account
- Google Services JSON file

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/qfix.git
cd qfix
```

### 2. Firebase Configuration

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Add an Android app to your Firebase project
4. Use `com.qfix` as the package name
5. Download the `google-services.json` file
6. Place the file in `app/src/main/` directory

### 3. Enable Firebase Services

In the Firebase Console, enable the following services:

- **Authentication**: Enable Email/Password and Google sign-in methods
- **Cloud Firestore**: Create database in locked mode
- **Cloud Storage**: Set up default bucket

### 4. Configure Firestore Security Rules

Deploy the following security rules to your Firestore database:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Complaints collection
    match /complaints/{complaintId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null && 
        (request.resource.data.diff(resource.data).affectedKeys()
         .hasOnly(['status', 'updates', 'assignedTo', 'resolvedAt']));
    }
    
    // Feedback collection
    match /feedback/{feedbackId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 5. Configure Storage Security Rules

Deploy the following security rules to your Cloud Storage:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    // Images folder
    match /images/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

### 6. Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Build the project:

```bash
./gradlew build
```

4. Run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/qfix/
│   │   │   ├── data/
│   │   │   │   ├── local/       # Room Database
│   │   │   │   ├── model/       # Data Models
│   │   │   │   ├── remote/      # Firebase Services
│   │   │   │   └── repository/  # Data Repositories
│   │   │   ├── ui/
│   │   │   │   ├── auth/        # Authentication Screens
│   │   │   │   ├── citizen/     # Citizen Screens
│   │   │   │   ├── authority/   # Authority Screens
│   │   │   │   └── shared/      # Shared Components
│   │   │   ├── utils/           # Utility Classes
│   │   │   ├── viewmodel/       # ViewModels
│   │   │   └── QFixApplication.java
│   │   └── res/
│   │       ├── anim/            # Custom Animations
│   │       ├── drawable/        # Drawables and Vector Assets
│   │       ├── layout/          # XML Layouts
│   │       ├── values/          # Strings, Colors, Styles
│   │       ├── values-hi/       # Hindi Translations
│   │       └── raw/             # Raw Assets (Lottie files)
│   └── google-services.json     # Firebase Configuration
├── build.gradle                 # Module-level Gradle file
└── ...
```

## Key Components

### Data Layer
- **Models**: User, Complaint, Feedback, Update, Category
- **Repositories**: AuthRepository, ComplaintRepository
- **Local**: Room Database with DAOs
- **Remote**: Firebase services

### UI Layer
- **Activities**: Splash, Onboarding, Role Selection, Login, Signup, Dashboards
- **Fragments**: Profile, Complaint Reporting Wizard, Complaint Lists, etc.
- **Adapters**: RecyclerView adapters for lists
- **Custom Views**: Bottom sheets, dialogs

### Utility Classes
- **ThemeHelper**: Dark mode management
- **ImageUtils**: Image processing utilities
- **DateUtils**: Date formatting utilities
- **NetworkUtils**: Network connectivity utilities

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Icons by [Material Design Icons](https://material.io/resources/icons/)
- Animations by [LottieFiles](https://lottiefiles.com/)
- UI Components by [Material Components for Android](https://github.com/material-components/material-components-android)

## Contact

For any queries, please contact the development team at [support@qfix.app](mailto:support@qfix.app).

---

Made with ❤️ for better civic engagement