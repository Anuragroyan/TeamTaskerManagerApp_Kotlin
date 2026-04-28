package com.example.teamtaskerapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- CRITICAL IMPORTS START ---
import com.google.firebase.auth.FirebaseAuth
// If you use 'ktx' versions, this is the standard import
// --- CRITICAL IMPORTS END ---
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    isSignUp: Boolean,
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBackToWelcome: () -> Unit,
    onSuccess: () -> Unit
) {
    // The rest of your code is logic-perfect,
    // it just needs the library to be linked.
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = Color(0xFFE57373),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToWelcome) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = Color.White)
                    }
                    Text(
                        text = if (isSignUp) "CREATE ACCOUNT" else "SIGN IN",
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9))
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSignUp)
                    "Enter your name, email id and password, to register with us."
                else
                    "Please sign in to continue your collaborative planning.",
                modifier = Modifier.padding(vertical = 32.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 22.sp,
                fontSize = 15.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isSignUp) {
                        CustomTextField(value = name, onValueChange = { name = it }, label = "Name")
                    }

                    CustomTextField(value = email, onValueChange = { email = it }, label = "Email")

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color(0xFFE57373)
                        )
                    } else {
                        BlueGradientButton(
                            text = if (isSignUp) "REGISTER" else "LOGIN",
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    isLoading = true
                                    if (isSignUp) {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                scope.launch {
                                                    if (task.isSuccessful) {
                                                        snackbarHostState.showSnackbar("Registration Successful!")
                                                        onSuccess()
                                                    } else {
                                                        snackbarHostState.showSnackbar("Error: ${task.exception?.message}")
                                                    }
                                                }
                                            }
                                    } else {
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                scope.launch {
                                                    if (task.isSuccessful) {
                                                        snackbarHostState.showSnackbar("Logged in successfully!")
                                                        onSuccess()
                                                    } else {
                                                        snackbarHostState.showSnackbar("Login Failed: ${task.exception?.message}")
                                                    }
                                                }
                                            }
                                    }
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Please fill all fields") }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable {
                        if (isSignUp) onNavigateToSignIn() else onNavigateToSignUp()
                    }
            )
        }
    }
}