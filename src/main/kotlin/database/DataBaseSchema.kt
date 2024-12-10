package org.example.database

import java.sql.Connection
import java.sql.SQLException

object DataBaseSchema {

    private const val createUserTable = """CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL CHECK(role IN ('Admin', 'Creator', 'Customer')));"""

    private const val createCoursesTable = """CREATE TABLE IF NOT EXISTS course (id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT NOT NULL,
            price REAL,
            createdBy TEXT );
        """

    private const val createTransitionTable = """CREATE TABLE IF NOT EXISTS transactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            customerId INTEGER NOT NULL,
            courseId INTEGER NOT NULL,
            purchase_date TEXT NOT NULL,
            FOREIGN KEY (customerId) REFERENCES users(id), -- 'users' table has 'id' as the primary key
            FOREIGN KEY (courseId) REFERENCES courses(id) -- 'courses' table has 'id' as the primary key
        );"""

//    val createCoursesTable ="""DROP TABLE IF EXISTS course"""

    fun createSchema(connection: Connection) {
        try {
            val statement = connection.createStatement()
            statement.execute(createUserTable)
            statement.execute(createCoursesTable)
            statement.execute(createTransitionTable)
            println("All table created successfully.")
        } catch (e: SQLException) {
            println("Error creating tables: ${e.message}")
        }
    }
}