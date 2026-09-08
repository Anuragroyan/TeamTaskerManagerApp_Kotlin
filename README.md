📋 TaskMaster

TaskMaster is a Trello-inspired task management app that helps users organize projects, assign tasks to team members, and track progress efficiently. Built with Kotlin, Jetpack Compose, and Firebase, it provides a modern collaborative workspace with customizable task cards and real-time data management.

🎯 Why This Project?

TaskMaster was built to demonstrate how a modern Android task management application can handle project organization, task CRUD operations, team collaboration, authentication, and real-time data synchronization using Firebase.

It showcases practical Android development concepts including Jetpack Compose, MVVM architecture, Firebase integration, state management, and Kotlin Coroutines.

🚀 Features

* 🔐 User authentication with Firebase Authentication
* 👤 User account management
* 📁 Create and manage projects
* 📝 Create, update, and delete tasks
* 🎴 Customizable task cards
* 👥 Assign tasks to team members
* 📊 Track task progress and status
* 🏷️ Add task labels/categories
* 📅 Manage task deadlines
* 🔄 Real-time Firestore data synchronization
* 📱 Modern Jetpack Compose UI
* ⚡ Reactive UI state management
* ☁️ Cloud-based data management with Firebase

🔄 Application Workflow

Create Account → Create Project → Add Tasks → Customize Task Cards → Assign Members → Update Status → Track Progress → Complete Tasks

🧩 Main Modules

🔐 Authentication

* User registration
* User login
* Firebase Authentication
* User session management

📁 Project Management

* Create projects
* View projects
* Update project information
* Delete projects

📝 Task Management

* Create tasks
* Edit tasks
* Delete tasks
* Assign team members
* Set task status
* Set deadlines
* Add labels/categories

🎴 Task Cards

Each task can be represented through a customizable card containing information such as:

Task Title → Description → Assignee → Status → Priority/Label → Deadline

📊 Progress Tracking

Users can monitor project progress by updating task states and tracking completed and pending tasks.

🏗️ Architecture

Jetpack Compose UI → ViewModel → Repository → Firebase Authentication / Cloud Firestore

Architecture Components

* Jetpack Compose — UI development
* ViewModel — UI state and business logic
* Repository — Data access abstraction
* Firebase Authentication — User authentication
* Cloud Firestore — Cloud database and real-time synchronization
* Kotlin Coroutines — Asynchronous operations

🛠️ Tech Stack

Kotlin • Jetpack Compose • Android SDK • Firebase Authentication • Firebase Cloud Firestore • Kotlin Coroutines • MVVM • Repository Pattern

📂 Project Structure

TaskMaster/ → app/ → src/main/ → java/... → ui/ • screens/ • components/ • viewmodel/ • repository/ • model/ → res/ • AndroidManifest.xml → google-services.json → build.gradle.kts → README.md

⚙️ Getting Started

1. Clone the Repository

git clone <repository-url>
cd TaskMaster

2. Configure Firebase

Create/configure a Firebase project and add:

google-services.json

Place it inside:

app/google-services.json

Enable:

* Firebase Authentication
* Cloud Firestore

3. Build the Project

./gradlew build

4. Run on Android

./gradlew installDebug

Or open the project in Android Studio and run it on an emulator or physical Android device.

🔄 Data Flow

User Action → Compose UI → ViewModel → Repository → Firebase → Updated State → Compose UI

🔒 Security

* Firebase Authentication handles user authentication.
* Firestore security rules control database access.
* User-specific data can be protected through authenticated access.
* Firebase credentials/configuration are managed through the project configuration.

🌍 Real-World Use Case

TaskMaster can be used by:

* 👨‍💻 Software development teams
* 🎨 Design teams
* 📢 Marketing teams
* 🎓 Student project groups
* 🧑‍💼 Freelancers
* 🏢 Small businesses

A team can create a project, break it into tasks, assign responsibilities, update task statuses, and track overall project progress from one workspace.
