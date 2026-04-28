package com.example.teamtaskerapp.ui.views

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.teamtaskerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun CreateBoardScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var boardName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    // ✅ Full save function: upload image to Storage → save board to Firestore
    fun createBoard() {
        if (boardName.isBlank()) {
            errorMessage = "Board name cannot be empty."
            return
        }

        isLoading = true
        errorMessage = ""

        val currentUser = auth.currentUser
        val authorName = currentUser?.displayName
            ?: currentUser?.email?.substringBefore("@")
            ?: "Unknown"

        // Step 2: Save to Firestore after image is handled
        fun saveToFirestore(imageUrl: String) {
            val boardId = UUID.randomUUID().toString()
            val boardData = hashMapOf(
                "id" to boardId,
                "title" to boardName.trim(),
                "author" to authorName,
                "imageUrl" to imageUrl,
                "createdBy" to (currentUser?.uid ?: ""),
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("boards").document(boardId)
                .set(boardData)
                .addOnSuccessListener {
                    isLoading = false
                    Toast.makeText(context, "Board created!", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    errorMessage = "Failed to save: ${e.message}"
                }
        }

        if (selectedImageUri != null) {
            // Step 1a: Upload image to Firebase Storage, then save
            val imageRef = storage.reference
                .child("board_images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveToFirestore(downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    errorMessage = "Image upload failed: ${e.message}"
                }
        } else {
            // Step 1b: No image — save with empty URL (shows placeholder in list)
            saveToFirestore("")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Surface(shadowElevation = 3.dp, color = Color(0xFFE57373)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, enabled = !isLoading) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    "Create Board",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image picker avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = selectedImageUri ?: R.drawable.user,
                            contentDescription = "Board cover image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEEEEE))
                                .clickable(enabled = !isLoading) {
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.user),
                            error = painterResource(R.drawable.user)
                        )
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            color = Color(0xFFE57373)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Pick image",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedImageUri != null) "Image selected ✓" else "Tap to add a cover image",
                        fontSize = 12.sp,
                        color = if (selectedImageUri != null) Color(0xFF4CAF50) else Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = boardName,
                        onValueChange = {
                            boardName = it
                            errorMessage = ""
                        },
                        label = { Text("Board Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading,
                        isError = errorMessage.isNotEmpty(),
                        supportingText = {
                            if (errorMessage.isNotEmpty()) {
                                Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ✅ CREATE button — fully wired up
                    Button(
                        onClick = { createBoard() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE57373),
                            disabledContainerColor = Color(0xFFE57373).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("CREATE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}