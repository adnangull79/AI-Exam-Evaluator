package com.example.aiexamevaluator.data.model

data class UserModel(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
