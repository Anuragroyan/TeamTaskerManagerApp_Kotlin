package com.example.teamtaskerapp.ui.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamtaskerapp.ui.models.TaskCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsPage(
    card: TaskCard,
    onBackClick: () -> Unit,
    onUpdateClick: (TaskCard) -> Unit,
    onDeleteConfirm: (String) -> Unit
) {
    // --- 1. STATES ---
    var currentTitle by remember { mutableStateOf(card.title) }
    var selectedColor by remember { mutableStateOf(Color(card.labelColor)) }
    var selectedMembers by remember { mutableStateOf(card.members) }

    var showDeleteWarning by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMemberDialog by remember { mutableStateOf(false) }

    // 🔥 DATE PICKER STATE WITH BACK-DATE BLOCKING
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (card.dueDate > 0) card.dueDate else null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Blocks dates older than today (with a 24h buffer for timezones)
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )

    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateDisplay = datePickerState.selectedDateMillis?.let {
        formatter.format(Date(it))
    } ?: "Select Due Date"

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "TaskDetails",
                onBackClick = onBackClick,
                onDeleteClick = { showDeleteWarning = true }
            )
//            TopAppBar(
//                title = { Text("Task Details", color = Color.White, fontWeight = FontWeight.Bold) },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE57373)),
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back", tint = Color.White)
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showDeleteWarning = true }) {
//                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
//                    }
//                }
//            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTION: TITLE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Title", color = Color(0xFFE57373), fontSize = 12.sp)
                    BasicTextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        decorationBox = { innerTextField ->
                            Column {
                                innerTextField()
                                HorizontalDivider(color = Color(0xFFE57373), modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    )
                }
            }

            // SECTION: CONFIGURATION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // LABEL COLOR
                    Text("Label Color", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(selectedColor)
                            .clickable { showColorPicker = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // MEMBERS
                    Text("Members (${selectedMembers.size})", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        selectedMembers.forEach { iconVector ->
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 8.dp)
                                    .clip(CircleShape)
                            )
                        }
                        IconButton(
                            onClick = { showMemberDialog = true },
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.dp, Color(0xFF2196F3), CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF2196F3))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // DUE DATE
                    Text("Due Date", color = Color.Gray, fontSize = 14.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFE57373))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateDisplay,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // UPDATE ACTION
                    Button(
                        onClick = {
                            val updatedTask = card.copy(
                                title = currentTitle,
                                labelColor = selectedColor.value.toLong(),
                                dueDate = datePickerState.selectedDateMillis ?: card.dueDate,
                                members = selectedMembers
                            )
                            onUpdateClick(updatedTask)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("UPDATE TASK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // MEMBER DIALOG
    if (showMemberDialog) {
        SearchMemberDialog(
            onDismiss = { showMemberDialog = false },
            onAdd = { email ->
                selectedMembers = selectedMembers + Icons.Default.AccountCircle
                showMemberDialog = false
            }
        )
    }

    // DELETE DIALOG
    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            title = { Text("Delete Card") },
            text = { Text("Are you sure you want to delete '$currentTitle'?") },
            confirmButton = {
                TextButton(onClick = { onDeleteConfirm(card.id) }) {
                    Text("DELETE", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWarning = false }) { Text("CANCEL") }
            }
        )
    }

    // COLOR PICKER DIALOG
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Select Label Color") },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(Color.Red, Color.Blue, Color.Green, Color(0xFFFFC107), Color.Magenta).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .clickable {
                                    selectedColor = color
                                    showColorPicker = false
                                }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    // 🔥 FIXED DATE PICKER DIALOG
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SearchMemberDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Member") },
        text = {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("User Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { if (email.isNotBlank()) onAdd(email) }) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        }
    )
}

@Composable
fun CustomTopBar(
    title: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color(0xFFE57373),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // BACK BUTTON
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // TITLE
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            // DELETE BUTTON
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    }
}