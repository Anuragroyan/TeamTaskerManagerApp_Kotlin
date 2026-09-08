📝 TaskMaster

TaskMaster is a Trello-inspired task management application built with Kotlin and Jetpack Compose, designed to help individuals and teams organize projects, assign tasks, and track progress from a centralized workspace.

The application provides a visual task-management experience where users can create projects, organize tasks into customizable cards, assign work to team members, and monitor progress in real time using Firebase Authentication and Firebase Cloud Firestore.

The project focuses on building a practical collaborative productivity application while demonstrating modern Android development, real-time data synchronization, authentication, CRUD operations, and customizable task management.

🎯 Why This Project?

Managing projects across multiple tasks and team members can become difficult without a centralized workspace.

TaskMaster provides a Trello-inspired workflow where projects and tasks can be organized visually, making it easier for teams to understand what needs to be done, what is currently in progress, and what has been completed.

The project demonstrates how a native Android application can combine Kotlin, Jetpack Compose, Firebase Authentication, real-time Firestore data, CRUD operations, and collaborative task management into a complete productivity solution.

✨ Features

* 🔐 User registration and authentication
* 👤 User profile management
* 📁 Create and manage projects
* ➕ Create new tasks
* ✏️ Edit task details
* 🗑️ Delete tasks
* 📋 Customizable task cards
* 👥 Assign tasks to team members
* 🔄 Update task status
* 📊 Track project progress
* 🏷️ Organize tasks using labels/categories
* 📅 Manage task deadlines
* 🔥 Firebase Authentication
* ☁️ Firebase Cloud Firestore
* ⚡ Real-time data synchronization
* 🔒 User-specific project and task data
* 📱 Modern Jetpack Compose UI
* 🧩 Trello-inspired task organization

🔄 Task Management Workflow

Create Account → Create Project → Add Tasks → Customize Task Cards → Assign Members → Update Status → Track Progress → Complete Tasks

🧩 Main Modules

🔐 Authentication

Users can register and log in securely using Firebase Authentication.

Authentication provides access to user-specific projects and task-management data.

📁 Project Management

Users can create and manage projects that act as centralized workspaces for related tasks.

Projects can contain information such as:

* Project name
* Description
* Members
* Tasks
* Status
* Deadlines

📋 Task Management

Tasks represent individual pieces of work within a project.

Users can perform complete CRUD operations:

Create → Read → Update → Delete

Task information can include:

* Task title
* Description
* Status
* Priority
* Assignee
* Due date
* Labels
* Additional details

🃏 Customizable Task Cards

Task cards provide a visual representation of individual tasks.

Users can customize task information to make important details easier to identify while working through a project.

👥 Team Assignment

Projects can include multiple team members, allowing tasks to be assigned to specific users.

Project → Team Members → Assigned Tasks → Individual Progress

🔄 Task Status Tracking

Tasks can move through different stages of completion.

To Do → In Progress → Completed

This provides a simple visual workflow for tracking project progress.

🔥 Firebase Integration

Firebase provides the backend services required for authentication and real-time application data.

Android App → Firebase Authentication + Cloud Firestore → Real-Time Project & Task Data

Firebase Authentication

Used for:

* User registration
* User login
* User authentication
* Managing authenticated sessions

Cloud Firestore

Used for storing and synchronizing:

* Users
* Projects
* Tasks
* Team members
* Task assignments
* Project information

Firestore’s real-time capabilities allow changes to project and task data to be reflected across connected clients.

🏗️ Application Architecture

The application follows a modern Android architecture designed to separate UI, state management, business logic, and data operations.

Jetpack Compose UI → ViewModel → Repository → Firebase Authentication / Firestore

This structure helps keep the application modular, maintainable, and easier to extend.

🛠️ Tech Stack

Kotlin • Jetpack Compose • Android SDK • Firebase Authentication • Firebase Cloud Firestore • Kotlin Coroutines • MVVM • Repository Pattern

📁 Project Structure

TaskMaster/
 → app/src/main/java/ • ui/ • screens/ • components/ • viewmodel/ • repository/ • model/
 → app/src/main/res/ • drawable/ • mipmap/ • values/
 → AndroidManifest.xml • build.gradle.kts • settings.gradle.kts • README.md

🚀 Getting Started

Prerequisites

Make sure you have the following installed:

* Android Studio
* JDK
* Android SDK
* Android Emulator or physical Android device
* Firebase account/project

Installation

Clone the repository:

git clone <your-repository-url>

Navigate to the project:

cd TaskMaster

Open the project in Android Studio and allow Gradle to sync.

Firebase Configuration

Create or select a Firebase project and connect the Android application to it.

Add the Firebase configuration file:

google-services.json

Place it inside:

app/

Enable the required Firebase services, such as:

* Firebase Authentication
* Cloud Firestore

Make sure your Firebase package/application ID matches the Android project configuration.

Run the Application

From Android Studio, select an emulator or connected Android device and click:

Run ▶

Alternatively, from the project directory:

./gradlew installDebug

To build a debug APK:

./gradlew assembleDebug

🔒 Authentication & Data Security

Firebase Authentication is used to authenticate users, while Firestore stores project and task information.

User-specific data can be associated with authenticated users, allowing the application to control access to projects and tasks.

Firestore Security Rules should be configured to ensure users can only read or modify data they are authorized to access.

💡 Real-World Use Case

A development team is working on a mobile application.

The project manager can:

1. Create a project.
2. Add team members.
3. Create tasks for different development activities.
4. Assign tasks to individual team members.
5. Set priorities and deadlines.
6. Move tasks from To Do to In Progress.
7. Mark completed work as Completed.
8. Monitor the overall project progress.

This creates a complete:

Project → Team → Tasks → Assignment → Progress → Completion

workflow.

🎯 Project Goals

The main goals of this project are:

* Build a practical Trello-inspired task management application.
* Develop a native Android application using Kotlin.
* Create a modern UI using Jetpack Compose.
* Implement user authentication with Firebase.
* Implement real-time data management using Firestore.
* Implement complete project and task CRUD operations.
* Support team-based task assignment.
* Track task and project progress.
* Practice MVVM and Repository-based architecture.
* Work with Kotlin Coroutines and asynchronous operations.
* Build a complete real-world productivity application for a development portfolio.

📌 Portfolio Highlights

This project demonstrates practical experience with:

Kotlin • Jetpack Compose • Android Development • MVVM • Repository Pattern • Firebase Authentication • Cloud Firestore • Real-Time Data • Kotlin Coroutines • CRUD Operations • State Management • User Authentication • Project Management • Task Management • Team Collaboration • Task Assignment • Progress Tracking • Modern Android Architecture
