# Simplenote

Simplenote is a modern note-taking application built using Jetpack Compose. It provides users with a seamless experience for creating, managing, and synchronizing notes across devices. With Simplenote, you can easily organize your ideas and tasks, while enjoying offline functionality and AI-powered suggestions.

---

## Features

### 1. **Authentication**
- **Login and Registration**:
  - Secure login functionality using JWT tokens.
  - Register as a new user by providing basic details like name, username, email, and password.
- **Token Management**:
  - Automatic token refresh for seamless user sessions.
  - Session expiration notifications to prompt re-login.
- **Change Password**:
  - Update passwords securely for enhanced account security.
  - Logout users automatically after password changes.

---

### 2. **Note Management**
- **Create, Read, Update, Delete (CRUD)**:
  - Effortlessly add, edit, and delete notes.
  - Notes can be both stored locally and synced with the server for online access.
- **Offline Mode**:
  - Access all your notes offline.
  - Changes made offline will be synchronized when the user goes online.
- **Search and Filter Notes**:
  - Search by title or content to quickly locate the desired note.
  - Filter notes with advanced search parameters like update timestamps.
- **AI-Powered Suggestions**:
  - Generate AI-based drafts for notes using Gemini AI API.
  - Use the AI button to provide prompts and get tailored suggestions.

---

### 3. **User Interface and Navigation**
- **Navigation**:
  - A smooth and intuitive navigation setup using Jetpack Compose Navigation.
  - Includes onboarding, home, settings, and note editing screens.
- **Design**:
  - Aesthetic and user-friendly UI designed with Material 3.
  - Themes support both light and dark modes.
- **Responsive Layout**:
  - Adaptive designs for devices of varying screen sizes, ensuring an optimal user experience.

---

### 4. **Synchronization**
- **Real-Time Sync**:
  - Synchronize notes to the backend server using the Notes API.
  - Conflict resolution strategies to ensure data consistency.
- **Pending Sync Actions**:
  - Save offline changes (create, update, delete) and sync when back online.

---

### 5. **Error Handling**
- **User Feedback**:
  - Display clear error messages for network issues, validation errors, and unexpected failures.
- **Retry Mechanism**:
  - Retry logic for failed AI requests, API calls, and synchronization tasks.

---

### 6. **Security**
- **Token-Based Authentication**:
  - Uses JWT for secure access to the backend.
- **Data Encryption**:
  - Stores user tokens securely using encrypted storage.
- **Safe API Consumption**:
  - Implements OkHttp interceptors for authentication and logging.

---

## Tech Stack

### **Frontend**
- **Jetpack Compose**: Modern UI toolkit for building native Android interfaces.
- **Material 3**: UI components for consistent theming and design.

### **Backend Integration**
- **Retrofit**: HTTP client for API communication.
- **Room**: Local database for offline storage.
- **DataStore**: Android Jetpack library for storing small data securely.

### **Third-Party Libraries**
- **Sentry**: Error monitoring and reporting.
- **Koin**: Dependency injection framework.
- **Gemini API**: AI-powered content generation.

---

## Architecture

### **Clean Architecture**
The application follows the principles of Clean Architecture:
- **UI Layer**:
  - Jetpack Compose for reactive UI updates.
  - ViewModels for state management.
- **Domain Layer**:
  - Business logic is encapsulated in repositories.
- **Data Layer**:
  - Handles API and database interactions.
  - Manages offline-first functionality.

---

## Best Practices

### 1. **Code Quality**
- **Kotlin Best Practices**:
  - Use of coroutines for asynchronous operations.
  - Null safety and data classes for clean data modeling.
- **Jetpack Compose**:
  - Separation of concerns between UI and business logic.
  - State hoisting for better composability.

### 2. **Offline-First Design**
- Notes are stored locally using Room and synced with the backend.
- Conflict resolution ensures consistent data across devices.

### 3. **Dependency Injection**
- Koin is used to inject dependencies like ViewModels, repositories, and APIs.

### 4. **Error Handling**
- Different error states (e.g., HTTP errors, network errors) are handled gracefully.
- User-friendly error messages are displayed in the UI.

### 5. **Secure Data Management**
- JWT tokens are stored securely using Android's DataStore.
- Refresh tokens ensure minimal disruption to user sessions.

### 6. **Testing**
- Unit tests for ViewModels and repositories.
- Integration tests for Room database interactions.

---

## Getting Started

### Prerequisites
- Android Studio Flamingo or later.
- Kotlin 1.9.0 or higher.
- Minimum Android SDK 26.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/AliMajidi1/simplenote.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Configure the backend URLs and API keys in `AndroidManifest.xml` metadata.
5. Run the project on an emulator or physical device.

---

## Contributing

Contributions are welcome! If you find any bugs or have suggestions for new features, feel free to open an issue or submit a pull request.

---

## License

Simplenote is licensed under the [MIT License](https://github.com/AliMajidi1/simplenote/blob/master/LICENSE).

---

## Contact

For more information, feel free to reach out to the repository owner:

**Ali Majidi**  
[GitHub Profile](https://github.com/AliMajidi1)

Happy note-taking with Simplenote! 🎉
