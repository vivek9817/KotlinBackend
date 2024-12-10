package org.example.repository

import org.example.model.Course
import org.example.model.StatisticsResponse
import org.example.model.User
import org.example.utils.DatabaseConnector
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserRepository {

    val connection: Connection? = DatabaseConnector.connect()

    // Valid roles list to ensure the user is assigned a correct role
    private val validRoles = listOf("Admin", "Creator", "Customer")

    // Function to check if the provided email already exists in the database
    private fun emailExists(email: String): Boolean {
        val query = "SELECT COUNT(*) FROM users WHERE email = ?"
        connection?.prepareStatement(query)?.apply {
            setString(1, email)
            val resultSet = executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0  // If the count is greater than 0, the email exists
            }
        }
        return false
    }

    // Function to validate the role provided is one of the valid roles
    private fun isValidRole(role: String): Boolean {
        return validRoles.contains(role)// Check if the role is in the validRoles list
    }

    /* For Create User */
    // Method to create a new user in the system
    fun createUser(user: User): String {
        if (emailExists(user.email)) {
            return "Email is already in use"  // Email already exists, do not create the user
        }
        // Validate role
        if (!isValidRole(user.role!!)) {
            return "Invalid role. Allowed roles are: Admin, Creator, Customer"
        }

        val query = "INSERT INTO users(email,password,role) VALUES (?,?,?)"
        connection?.prepareStatement(query)?.apply {
            setString(1, user.email)
            setString(2, user.password)
            setString(3, user.role)
            return if (executeUpdate() > 0) {
                "User created successfully"
            } else {
                "Failed to create user"
            }
        }
        return "Database error occurred"
    }

    /* For View All Created Data */
    // Method to get all users with roles 'Creator' or 'Customer'
    fun getAllUser(): List<User> {
        val users = mutableListOf<User>()
        val query = "SELECT * FROM users WHERE role IN ('Creator', 'Customer')"
        val connection: Connection? = DatabaseConnector.connect()

        connection?.createStatement()?.executeQuery(query)?.apply {
            while (next()) {
                val user = User(
                    id = getInt("id"),
                    email = getString("email"),
                    password = getString("password"),
                    role = getString("role")
                )
                users.add(user)
            }
        }
        return users
    }

    /* Login */
    // Method to fetch a user by their email for login
    fun getUserByEmail(email: String): User? {
        val query = "SELECT * FROM users WHERE email = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(query)
        statement.setString(1, email)
        val resultSet: ResultSet = statement.executeQuery()
        if (resultSet.next()) {
            // Map the result set to a User object
            return User(
                id = resultSet.getInt("id"),
                email = resultSet.getString("email"),
                password = resultSet.getString("password"),
                role = resultSet.getString("role")
            )
        }
        return null // Return null if the email doesn't exist
    }

    // Function to delete all users from the database
    fun deleteAllUsers(): String {
        try {
            val query = "DELETE FROM users" // SQL query to delete all records from the users table
            connection?.createStatement()?.apply {
                executeUpdate(query)
                return "All users have been deleted successfully"
            }
        } catch (e: Exception) {
            return "Error occurred while deleting users: ${e.localizedMessage}"
        }
        return ""
    }

    // Function to add a new course to the system
    fun createCourse(course: Course): String {
        val query = "INSERT INTO course(title,description,price,createdBy) VALUES (?,?,?,?)"
        connection?.prepareStatement(query)?.apply {
            setString(1, course.title)
            setString(2, course.description)
            setFloat(3, course.price ?: 0f)
            setString(4, course.createdBy)
            return if (executeUpdate() > 0) {
                "Course added successfully"
            } else {
                "Failed to add course"
            }
        }
        return "Database error occurred"
    }

    // Method to get all courses, with optional search functionality
    fun getAllCourses(search: String?): List<Course> {
        val query = if (search.isNullOrEmpty()) {
            "SELECT * FROM course" // Fetch all courses if no search term is provided
        } else {
            // Filter by title or description if search term is provided
            "SELECT * FROM course WHERE title LIKE ? OR description LIKE ?"
        }

        val courses = mutableListOf<Course>()
        connection?.prepareStatement(query)?.apply {
            // Set parameters if search term is provided
            if (!search.isNullOrEmpty()) {
                val likeSearchTerm = "%$search%"
                setString(1, likeSearchTerm)
                setString(2, likeSearchTerm)
            }

            val resultSet = executeQuery()
            while (resultSet.next()) {
                val course = Course(
                    id = resultSet.getInt("id"),
                    title = resultSet.getString("title"),
                    description = resultSet.getString("description"),
                    price = resultSet.getFloat("price"),
                    createdBy = resultSet.getString("createdBy")
                )
                courses.add(course)
            }
        }
        return courses
    }

    // Method to get a course by its ID
    fun getCourseById(courseId: Int): Course? {
        val query = "SELECT * FROM course WHERE id = ?"
        connection?.prepareStatement(query)?.use { statement ->
            statement.setInt(1, courseId)
            statement.executeQuery().use { resultSet ->
                // If the course exists, return the data
                if (resultSet.next()) {
                    return Course(
                        id = resultSet.getInt("id"),
                        title = resultSet.getString("title"),
                        description = resultSet.getString("description"),
                        price = resultSet.getFloat("price"),
                        createdBy = resultSet.getString("createdBy")
                    )
                }
            }
        }
        return null // Return null if no course is found
    }

    // Method to allow a customer to buy a course
    fun buyCourse(customerId: Int, courseId: Int): String {
        // Create a formatter for the date in 'yyyy-MM-dd' format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now().format(formatter)  // Get current date in the required format
        val query = "INSERT INTO transactions(customerId,courseId,purchase_date) VALUES (?,?,?)"
        connection?.prepareStatement(query)?.apply {
            setInt(1, customerId)
            setInt(2, courseId)
            setString(3, currentDate)
            return if (executeUpdate() > 0) {
                "Course buy successfully"
            } else {
                "Failed to buy course"
            }
        }
        return "Database error occurred"
    }

    // Function to get statistics (total bought courses and total amount paid)
    fun getCourseStatistics(startDate: String, endDate: String): List<StatisticsResponse> {
        val statistics = mutableListOf<StatisticsResponse>()

        // SQL query to get bought courses and the total amount paid
        val query = """
                SELECT c.title, c.price, t.purchase_date, COUNT(t.courseId) AS total_courses, SUM(c.price) AS total_amount
                FROM transactions t
                JOIN course c ON t.courseId = c.id
                WHERE t.purchase_date BETWEEN ? AND ?
                GROUP BY c.id;
            """

        connection?.prepareStatement(query)?.apply {
            // Set the start and end dates as parameters
            setString(1, startDate)
            setString(2, endDate)

            val resultSet = executeQuery()

            // Process the result set
            while (resultSet.next()) {
                // Create StatisticsResponse object for each course
                val courseStats = StatisticsResponse(
                    courseTitle = resultSet.getString("title"),
                    coursePrice = resultSet.getFloat("price"),
                    totalCourses = resultSet.getInt("total_courses"),
                    totalAmount = resultSet.getFloat("total_amount"),
                    purchase_date = resultSet.getString("purchase_date")
                )
                statistics.add(courseStats)
            }
        }

        return statistics
    }
}