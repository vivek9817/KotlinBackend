package org.example.controller

import io.ktor.http.*
import io.ktor.server.routing.*
import org.example.service.CommonServices
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.example.model.Course
import org.example.model.User
import org.example.utils.CommonUtils.decodeJWT
import org.example.utils.CommonUtils.generateJWT
import org.example.utils.CommonUtils.isValidEmail

fun Route.userRoutes(commonServices: CommonServices) {
    var token: String? = null
    route("/users") {
        // Existing signup route
        post("/signup") {
            try {
                val rawBody = call.receiveText()
                println("Raw request body: $rawBody") // Log the raw JSON request
                val user = Json.decodeFromString<User>(rawBody) // Deserialize manually for debugging
                // Validate email format
                if (!isValidEmail(user.email)) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid email format")
                    return@post
                }
                val result = commonServices.signUp(user)
                if (result == "User created successfully") {
                    call.respond(HttpStatusCode.Created, result)
                } else {
                    call.respond(HttpStatusCode.BadRequest, result)
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest, "Invalid request payload: ${e.localizedMessage}")
            }

        }

        // route to fetch all users
        get("/user") {
            try {
                val users = commonServices.getAllUser()  // Get all users from the service
                call.respond(HttpStatusCode.OK, users)  // Respond with the list of users
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to fetch users: ${e.localizedMessage}")
            }
        }

        // Login with email and password
        post("/login") {
            try {
                val rawBody = call.receiveText()
                println("Raw request body: $rawBody") // Log the raw JSON request
                val user = Json.decodeFromString<User>(rawBody) // Deserialize manually for debugging
                // Call the service to handle the login logic
                val resultMessage = commonServices.login(user.email, user.password)
                // Retrieve the user from the database based on email
                val userFromDb = commonServices.getUserDetails(user.email)
                // Respond based on the result of the login attempt
                if (resultMessage == "Login successful") {
                    // If valid, generate JWT token
                    token = generateJWT(userFromDb!!)
                    call.respond(HttpStatusCode.OK, "$resultMessage : $token")
                } else {
                    call.respond(HttpStatusCode.BadRequest, resultMessage)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request payload: ${e.localizedMessage}")
            }
        }

        // Route to delete all users
        delete("/deleteAll") {
            val resultMessage = commonServices.deleteAllUsers()
            if (resultMessage.contains("successfully")) {
                call.respond(HttpStatusCode.OK, resultMessage)
            } else {
                call.respond(HttpStatusCode.InternalServerError, resultMessage)
            }
        }

        post("/course") {
            try {
                // Step 2: Decode the JWT token to extract role
                val decodedJWT = token?.let { it1 -> decodeJWT(it1) }
                // If decoding fails, respond with an error
                if (decodedJWT == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
                    return@post
                }
                // Step 3: Extract role from the decoded JWT
                val role = decodedJWT.getClaim("role").asString()

                if (role == "Creator") {
                    val rawBody = call.receiveText()
                    val course = Json.decodeFromString<Course>(rawBody) // Deserialize manually for debugging

                    // Step 6: Add the role (from JWT) to the course object (createdBy field)
                    val courseWithRole = course.copy(createdBy = role)

                    println("Raw request body: $courseWithRole") // Log the raw JSON request

                    val result = commonServices.addCourse(courseWithRole)
                    if (result == "Course added successfully") {
                        call.respond(HttpStatusCode.Created, result)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, result)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Only 'Creator' will allowed to add The courses")
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
                call.respond(HttpStatusCode.BadRequest, "Invalid request payload: ${e.localizedMessage}")
            }
        }

        get("/course") {
            try {
                // Step 1: Extract the search query parameter (optional)
                val search = call.request.queryParameters["search"]

                // Step 2: Fetch the courses (with or without the search filter)
                val courses = commonServices.getAllCourses(search)

                // Step 3: Respond with the list of courses
                if (courses.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, courses)
                } else {
                    call.respond(HttpStatusCode.NoContent, "No courses found.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to fetch course: ${e.localizedMessage}")
            }
        }

        post("/buy/course/{courseId}") {
            try {
                //step 1 : set Customer id
                // Step 2: Decode the JWT token to extract role
                val decodedJWT = token?.let { it1 -> decodeJWT(it1) }
                // If decoding fails, respond with an error
                if (decodedJWT == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
                    return@post
                }

                val email = decodedJWT.subject

                val customer =
                    commonServices.getUserDetails(email) // Replace with your function to fetch customer ID by email
                if (customer == null) {
                    call.respond(HttpStatusCode.BadRequest, "Customer not found.")
                    return@post
                }

                // Step 2: Validate the course ID
                val courseId = call.parameters["courseId"]?.toIntOrNull()
                if (courseId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid course ID.")
                    return@post
                }
                // Step 3: Check if the course exists
                val courseExists = commonServices.getCourseById(courseId)
                if (courseExists == null) {
                    call.respond(HttpStatusCode.NotFound, "Course not found.")
                    return@post
                }

                val result = commonServices.buyCourse(customer.id ?: 0, courseId)
                if (result == "Course buy successfully") {
                    call.respond(HttpStatusCode.Created, result)
                } else {
                    call.respond(HttpStatusCode.BadRequest, result)
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to fetch course: ${e.localizedMessage}")
            }
        }

        // Statistics API route
        get("/stats") {
            try {
//                // Extract query parameters for start and end dates
                val startDate = call.request.queryParameters["startDate"]
                val endDate = call.request.queryParameters["endDate"]

                // Validate the dates
                if (startDate.isNullOrBlank() || endDate.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Start and End dates are required.")
                    return@get
                }

                // Get statistics
                val statistics = commonServices.getCourseStatistics(startDate, endDate)

                // Respond with the statistics
                call.respond(HttpStatusCode.OK, statistics)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error fetching statistics: ${e.localizedMessage}")
            }
        }
    }
}