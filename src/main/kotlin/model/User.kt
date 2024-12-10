package org.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = 0,
    val email: String,
    val password: String,
    val role: String? = null
)
