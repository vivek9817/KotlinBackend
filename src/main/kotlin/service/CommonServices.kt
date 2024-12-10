package org.example.service

import org.example.model.Course
import org.example.model.StatisticsResponse
import org.example.model.Transactions
import org.example.model.User
import org.example.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class CommonServices(private val commonRepository: UserRepository) {

    fun signUp(user: User): String {
        // Hash the password using BCrypt
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val newUser = user.copy(password = hashedPassword)

        // Call the repository to save the user
        return commonRepository.createUser(newUser)
    }

    fun getAllUser(): List<User> {
        return commonRepository.getAllUser()
    }

    fun login(email: String, password: String): String {
        val user = commonRepository.getUserByEmail(email)
        if (user != null) {
            // Check if the provided password matches the stored hashed password
            val isPasswordValid = BCrypt.checkpw(password, user.password)
            if (isPasswordValid) {
                // If valid, return success message or JWT token
                return "Login successful"
            } else {
                // Password is incorrect
                return "Invalid password"
            }
        } else {
            // User not found
            return "User with this email does not exist"
        }
    }

    fun getUserDetails(email: String): User? {
        return commonRepository.getUserByEmail(email)
    }

    // Function to delete all users
    fun deleteAllUsers(): String {
        return commonRepository.deleteAllUsers()
    }

    fun addCourse(course: Course): String {
        return commonRepository.createCourse(course)
    }

    fun getAllCourses(search: String?): List<Course> {
        return commonRepository.getAllCourses(search)
    }

    fun getCourseById(courseId: Int): Course? {
        return commonRepository.getCourseById(courseId)
    }

    fun buyCourse(customerId: Int, courseId: Int): String {
        return commonRepository.buyCourse(customerId, courseId)
    }

    fun getCourseStatistics(startDate: String, endDate: String): List<StatisticsResponse> {
        return commonRepository.getCourseStatistics(startDate, endDate)
    }
}