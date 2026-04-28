package com.example.teamtaskerapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamtaskerapp.ui.models.BoardList
import com.example.teamtaskerapp.ui.models.TaskCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBoardPage(
    boardId: String,
    boardTitle: String,
    onBackClick: () -> Unit,
    onMembersClick: () -> Unit,
    onCardClick: (String) -> Unit
) {
    // --- DO NOT EDIT START ---
    val boardLists = listOf(
        BoardList("First Draft", emptyList()),
        BoardList("UI List", emptyList()),
        BoardList("Developer List", emptyList()),
        BoardList("Deployment List", emptyList()),
        BoardList("Testing List", emptyList())
    )
    // --- DO NOT EDIT END ---

    val db = FirebaseFirestore.getInstance()
    var tasks by remember { mutableStateOf<List<TaskCard>>(emptyList()) }

    // 🔥 FIRESTORE LISTENER
    DisposableEffect(boardId) {
        val listener: ListenerRegistration = db.collection("boards")
            .document(boardId)
            .collection("tasks")
            .addSnapshotListener { snapshot, _ ->
                tasks = snapshot?.documents?.map { doc ->
                    TaskCard(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        listName = doc.getString("listName") ?: "First Draft",
                        labelColor = doc.getLong("color") ?: 0xFF4CAF50,
                        dueDate = doc.getLong("dueDate") ?: 0L,
                        members = emptyList(),
                        createdBy = doc.getString("createdBy"),
                        createdAt = doc.getLong("createdAt")
                    )
                } ?: emptyList()
            }
        onDispose { listener.remove() }
    }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = Color(0xFFE57373)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White)
                    }

                    Text(
                        boardTitle,
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Members") },
                                onClick = {
                                    showMenu = false
                                    onMembersClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF73B1BA)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(boardLists) { list ->
                BoardColumnFirebase(
                    boardTitle = list.title,
                    tasks = tasks,
                    boardId = boardId,
                    onCardClick = onCardClick
                )
            }
        }
    }
}

@Composable
fun BoardColumnFirebase(
    boardTitle: String,
    tasks: List<TaskCard>,
    boardId: String,
    onCardClick: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    // Adding state variables that were missing/misplaced
    var isAddingCard by remember { mutableStateOf(false) }
    var newCardName by remember { mutableStateOf("") }

    // Operations
    fun deleteTask(taskId: String) {
        db.collection("boards").document(boardId)
            .collection("tasks").document(taskId).delete()
    }

    fun updateTaskColor(taskId: String, newColor: Long) {
        db.collection("boards").document(boardId)
            .collection("tasks").document(taskId)
            .update("color", newColor)
    }

    fun addTask(title: String) {
        val task = mapOf(
            "title" to title,
            "listName" to boardTitle,
            "color" to 0xFF4CAF50,
            "createdBy" to (user?.email ?: "Unknown"),
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("boards").document(boardId).collection("tasks").add(task)
    }

    Card(
        modifier = Modifier.width(300.dp).fillMaxHeight().padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFAAD5AC))
    ) {
        Column {
            Text(boardTitle, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))

            Column(
                modifier = Modifier.weight(1f).padding(8.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Filter tasks to only show ones belonging to THIS column
                tasks.filter { it.listName == boardTitle }.forEach { task ->
                    Box {
                        TaskItemCard(
                            task = task,
                            onClick = { onCardClick(task.id) },
                            onColorChange = { newColor -> updateTaskColor(task.id, newColor) }
                        )

                        IconButton(
                            onClick = { deleteTask(task.id) },
                            modifier = Modifier.align(Alignment.TopEnd).size(30.dp)
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // ADD CARD SECTION
            Box(modifier = Modifier.padding(8.dp)) {
                if (isAddingCard) {
                    Column(modifier = Modifier.background(Color.White).padding(8.dp).fillMaxWidth()) {
                        BasicTextField(
                            value = newCardName,
                            onValueChange = { newCardName = it },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            IconButton(onClick = { isAddingCard = false; newCardName = "" }) {
                                Icon(Icons.Default.Cancel, null, tint = Color.Red)
                            }
                            IconButton(onClick = {
                                if (newCardName.isNotEmpty()) {
                                    addTask(newCardName)
                                    newCardName = ""
                                    isAddingCard = false
                                }
                            }) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { isAddingCard = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFCFC))
                    ) {
                        Text("Add Card", color = Color(0xFFE57373))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskCard,
    onClick: () -> Unit,
    onColorChange: (Long) -> Unit
) {
    val colorOptions = listOf(0xFF4CAF50, 0xFFF44336, 0xFF2196F3, 0xFFFF9800)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(task.labelColor)))

            Column(modifier = Modifier.padding(12.dp)) {
                Text(task.title, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .clickable { onColorChange(color) }
                        )
                    }
                }
            }
        }
    }
}