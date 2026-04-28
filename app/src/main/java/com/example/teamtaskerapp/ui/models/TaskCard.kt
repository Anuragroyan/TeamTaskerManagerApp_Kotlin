package com.example.teamtaskerapp.ui.models

import android.R
import androidx.compose.ui.graphics.vector.ImageVector

data class TaskCard(
    val id: String = "",
    var title: String = "",
    var labelColor: Long = 0xFF4CAF50,
    var dueDate: Long = 0L,
    val members: List<ImageVector> = emptyList(),
    val createdBy: String? = null,
    val createdAt: Long? = null,
    var listName: String = ""
)