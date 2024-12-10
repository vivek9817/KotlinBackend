package org.example.model

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val courseTitle: String,
    val coursePrice: Float,
    val totalCourses: Int,
    val totalAmount: Float,
    val purchase_date : String
)
