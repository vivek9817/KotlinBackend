package org.example.utils

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnector {
    private const val DB_URL = "jdbc:sqlite:./digital_course_marketplace.db"  // SQLite file-based DB

    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(DB_URL).apply {
                println("Database connected successfully!")
            }
        } catch (e: Exception) {
            println("Error connecting to the database: ${e.localizedMessage}")
            null
        }
    }
}