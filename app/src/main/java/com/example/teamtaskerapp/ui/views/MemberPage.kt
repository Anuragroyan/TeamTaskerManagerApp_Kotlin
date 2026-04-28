package com.example.teamtaskerapp.ui.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.teamtaskerapp.ui.models.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersPage(
    boardId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var members by remember { mutableStateOf<List<Member>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 🔥 Firestore listener
    DisposableEffect(boardId) {
        val listener: ListenerRegistration = db.collection("boards")
            .document(boardId)
            .collection("members")
            .addSnapshotListener { snapshot, _ ->

                members = snapshot?.documents?.map {
                    Member(
                        email = it.getString("email") ?: it.id,
                        createdBy = it.getString("createdBy"),
                        createdAt = it.getLong("createdAt")
                    )
                } ?: emptyList()

                isLoading = false
            }

        onDispose { listener.remove() }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = Color(0xFFE57373)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Members",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "", tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFD8E4E7))
                .padding(12.dp)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                members.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No members yet. Tap + to add one.")
                    }
                }

                else -> {
                    LazyColumn {
                        items(members) { member ->
                            MemberListItem(member)
                        }
                    }
                }
            }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            boardId = boardId,
            onDismiss = { showAddMemberDialog = false },
            onMemberAdded = {
                Toast.makeText(context, "Member added!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun MemberListItem(member: Member) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8CAFB6)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = member.email,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Text(
                    text = "Created by: ${member.createdBy ?: "Unknown"}",
                    fontSize = 12.sp,
                    color = Color.Blue
                )

                Text(
                    text = formatTime(member.createdAt),
                    fontSize = 11.sp,
                    color = Color.Green
                )
            }
        }
    }
}

@Composable
fun AddMemberDialog(
    boardId: String,
    onDismiss: () -> Unit,
    onMemberAdded: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var emailInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun addMember() {
        val email = emailInput.trim().lowercase()

        if (email.isEmpty()) {
            error = "Enter email"
            return
        }

        val createdBy = currentUser?.email ?: "Unknown"

        isLoading = true
        error = ""

        val memberRef = db.collection("boards")
            .document(boardId)
            .collection("members")
            .document(email)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(memberRef)

            if (snapshot.exists()) {
                throw Exception("Member already exists")
            }

            transaction.set(memberRef, mapOf(
                "email" to email,
                "createdBy" to createdBy,
                "createdAt" to System.currentTimeMillis()
            ))
        }.addOnSuccessListener {
            isLoading = false
            onMemberAdded()
            onDismiss()
        }.addOnFailureListener {
            isLoading = false
            error = it.message ?: "Failed to add member"
        }
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card {
            Column(Modifier.padding(20.dp)) {

                Text("Add Member", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                BasicTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        error = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Button(onClick = { addMember() }) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

// 🔥 Time formatter
fun formatTime(timestamp: Long?): String {
    if (timestamp == null) return ""

    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        else -> "${hours / 24} days ago"
    }
}