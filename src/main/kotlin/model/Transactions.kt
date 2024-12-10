package org.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Transactions(
    val id: Int? = 0,
    val customerId: Int,
    val courseId: Int,
    val purchase_date: String
)
