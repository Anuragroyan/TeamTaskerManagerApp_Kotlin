package com.example.teamtaskerapp.ui.models

data class Member(
    val uid: String = "",
    val name: String? = "",
    val email: String = "",
    val createdBy: String? = null,
    val createdAt: Long? = null
)
