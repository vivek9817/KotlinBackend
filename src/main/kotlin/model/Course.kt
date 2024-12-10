package org.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: Int? = 0,
    val title: String,
    val description: String,
    val price: Float? = 0f,
    val createdBy: String ? = null
)
