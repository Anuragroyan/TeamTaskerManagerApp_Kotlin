package com.example.teamtaskerapp.ui.models

import com.example.teamtaskerapp.R

class Board (
    val id: String = "",
    val title: String =  "",
    val author: String =  "",
    val imageUrl: String = "", // Cloud URL
    val imageRes: Int = R.drawable.user  // ✅ always valid
)