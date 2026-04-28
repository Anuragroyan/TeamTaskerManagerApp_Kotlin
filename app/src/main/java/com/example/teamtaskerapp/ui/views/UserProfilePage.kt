package com.example.teamtaskerapp.ui.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

// Model matching Firestore document structure
data class ProfileUiState(
    var name: String = "",
    var email: String = "",
    var mobile: String = "",
    var imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfilePage(onBackClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var uiState by remember { mutableStateOf(ProfileUiState()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }

    // --- Image Picker Launcher ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    // --- Fetch User Data on Startup ---
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        uiState = doc.toObject(ProfileUiState::class.java) ?: ProfileUiState()
                    } else {
                        uiState = uiState.copy(email = auth.currentUser?.email ?: "")
                    }
                    isLoading = false
                }
                .addOnFailureListener { isLoading = false }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = Color.White)
                    }
                    Text(
                        text = "My Profile",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE57373))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF9F9F9))
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Profile Image Section (FIXED: replaced drawable with Material Icon) ---
                // --- Profile Image Section ---
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    val hasImage = selectedImageUri != null || uiState.imageUrl.isNotEmpty()

                    // ✅ clickable on the image box itself — reliable, covers main tap area
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasImage) {
                            AsyncImage(
                                model = selectedImageUri ?: uiState.imageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Default Profile",
                                tint = Color.Gray,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // ✅ Camera badge also independently clickable
                    Surface(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }, // ✅ Added here
                        shape = CircleShape,
                        color = Color(0xFFE57373),
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
//                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.BottomEnd) {
//                    val hasImage = selectedImageUri != null || uiState.imageUrl.isNotEmpty()
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(CircleShape)
//                            .background(Color(0xFFE0E0E0)) // Light gray fallback background
//                            .clickable { imagePickerLauncher.launch("image/*") },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        if (hasImage) {
//                            AsyncImage(
//                                model = selectedImageUri ?: uiState.imageUrl,
//                                contentDescription = "Profile Picture",
//                                modifier = Modifier.fillMaxSize(),
//                                contentScale = ContentScale.Crop
//                            )
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.AccountCircle,
//                                contentDescription = "Default Profile",
//                                tint = Color.Gray,
//                                modifier = Modifier.fillMaxSize()
//                            )
//                        }
//                    }
//
//                    Surface(
//                        modifier = Modifier.size(28.dp),
//                        shape = CircleShape,
//                        color = Color(0xFFE57373),
//                        shadowElevation = 2.dp
//                    ) {
//                        Icon(
//                            Icons.Default.CameraAlt,
//                            null,
//                            tint = Color.White,
//                            modifier = Modifier.padding(6.dp)
//                        )
//                    }
//                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Form Card ---
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileInputField(
                            label = "Name",
                            value = uiState.name,
                            onValueChange = { uiState = uiState.copy(name = it) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ProfileInputField(
                            label = "Email",
                            value = uiState.email,
                            onValueChange = { uiState = uiState.copy(email = it) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ProfileInputField(
                            label = "Mobile",
                            value = uiState.mobile,
                            onValueChange = { uiState = uiState.copy(mobile = it) }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- Update Logic ---
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = Color(0xFFE57373)
                            )
                        } else {
                            Button(
                                onClick = {
                                    val uid = auth.currentUser?.uid ?: return@Button
                                    isUploading = true

                                    if (selectedImageUri != null) {
                                        val fileRef = storage.reference.child("users/$uid/profile.jpg")
                                        fileRef.putFile(selectedImageUri!!)
                                            .addOnSuccessListener {
                                                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                                    uiState = uiState.copy(imageUrl = downloadUrl.toString())
                                                    updateFirestore(db, uid, uiState, scope, snackbarHostState) {
                                                        isUploading = false
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                isUploading = false
                                                scope.launch { snackbarHostState.showSnackbar("Image upload failed") }
                                            }
                                    } else {
                                        updateFirestore(db, uid, uiState, scope, snackbarHostState) {
                                            isUploading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                            ) {
                                Text("UPDATE PROFILE", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to save data to Firestore
fun updateFirestore(
    db: FirebaseFirestore,
    uid: String,
    state: ProfileUiState,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHost: SnackbarHostState,
    onFinished: () -> Unit
) {
    db.collection("users").document(uid).set(state)
        .addOnSuccessListener {
            onFinished()
            scope.launch { snackbarHost.showSnackbar("Profile saved successfully!") }
        }
        .addOnFailureListener {
            onFinished()
            scope.launch { snackbarHost.showSnackbar("Error saving data") }
        }
}

@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Medium)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                Column {
                    innerTextField()
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        )
    }
}