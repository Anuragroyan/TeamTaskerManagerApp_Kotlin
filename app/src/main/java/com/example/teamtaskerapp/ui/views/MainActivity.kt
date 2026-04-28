package com.example.teamtaskerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.teamtaskerapp.ui.models.TaskCard
import com.example.teamtaskerapp.ui.theme.TeamTaskerAppTheme
import com.example.teamtaskerapp.ui.views.*
import com.google.firebase.FirebaseApp

enum class AppScreen {
    Welcome, SignIn, SignUp, Home, MyProfile, CreateBoard, TaskDetails, Members, CardDetails
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            TeamTaskerAppTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.Welcome) }
                var selectedTask by remember { mutableStateOf<TaskCard?>(null) }
                var selectedBoardName by remember { mutableStateOf("") }
                var selectedBoardId by remember { mutableStateOf("") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // ✅ FIX: Apply innerPadding here at the top-level Box,
                    //    not buried inside one Crossfade branch.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = currentScreen,
                            label = "MainNavigation"
                        ) { screen ->
                            when (screen) {
                                AppScreen.Welcome -> TeamTaskerWelcomeScreen(
                                    onGetStarted = { currentScreen = AppScreen.SignIn }
                                )
                                AppScreen.SignIn -> AuthScreen(
                                    isSignUp = false,
                                    onNavigateToSignIn = { currentScreen = AppScreen.SignIn },
                                    onNavigateToSignUp = { currentScreen = AppScreen.SignUp },
                                    onBackToWelcome = { currentScreen = AppScreen.Welcome },
                                    onSuccess = { currentScreen = AppScreen.Home }
                                )
                                AppScreen.SignUp -> AuthScreen(
                                    isSignUp = true,
                                    onNavigateToSignIn = { currentScreen = AppScreen.SignIn },
                                    onNavigateToSignUp = { currentScreen = AppScreen.SignUp },
                                    onBackToWelcome = { currentScreen = AppScreen.Welcome },
                                    onSuccess = { currentScreen = AppScreen.Home }
                                )
                                AppScreen.Home -> AppHomePage(
                                    onProfileClick = { currentScreen = AppScreen.MyProfile },
                                    onCreateBoardClick = { currentScreen = AppScreen.CreateBoard },
                                    onSignOutClick = { currentScreen = AppScreen.Welcome },
                                    onBoardClick = { board ->
                                        selectedBoardName = board.title
                                        selectedBoardId = board.id
                                        currentScreen = AppScreen.TaskDetails
                                    }
                                )
                                AppScreen.CardDetails -> {
                                    selectedTask?.let { task ->
                                        CardDetailsPage(
                                            card = task,
                                            onBackClick = { currentScreen = AppScreen.TaskDetails },
                                            onUpdateClick = { currentScreen = AppScreen.TaskDetails },
                                            onDeleteConfirm = { currentScreen = AppScreen.TaskDetails }
                                        )
                                    } ?: run {
                                        // Optional: Handle what happens if selectedTask is null
                                        currentScreen = AppScreen.TaskDetails
                                    }
                                }
                                AppScreen.TaskDetails -> TaskBoardPage(
                                    boardId = selectedBoardId, // ✅ REQUIRED
                                    boardTitle = selectedBoardName,
                                    onBackClick = { currentScreen = AppScreen.Home },
                                    onMembersClick = { currentScreen = AppScreen.Members },
                                    onCardClick = { taskId ->
                                            selectedTask = TaskCard(id = taskId)  // ✅ FIX
                                        currentScreen = AppScreen.CardDetails
                                    }
                                )
                                AppScreen.MyProfile -> UserProfilePage(
                                    onBackClick = { currentScreen = AppScreen.Home }
                                )
                                // ✅ FIX: No more mainPadding passed here
                                AppScreen.CreateBoard -> CreateBoardScreen(
                                    onBackClick = { currentScreen = AppScreen.Home }
                                )
                                AppScreen.Members -> MembersPage(
                                    boardId = "members_Id",
                                    onBackClick = { currentScreen = AppScreen.TaskDetails }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}