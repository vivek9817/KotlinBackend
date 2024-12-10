package org.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.*
import org.example.model.User
import java.util.*

object CommonUtils {

    private val jwtSecret = "yourSecretKey" // Secret key for JWT
    private val jwtIssuer = "yourAppIssuer" // Issuer for JWT

    /**Check Email regex*/
    fun isValidEmail(email: String): Boolean {
        // Regex for validating an email address
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Check and store JWT Token*/
    // Method to generate JWT token
    fun generateJWT(user: User): String {
        val algorithm = Algorithm.HMAC256(jwtSecret)
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(user.email)
            .withClaim("role",user.role)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // Token expires in 1 hour
            .sign(algorithm)
    }

    fun decodeJWT(token: String): DecodedJWT? {
        try {
            val algorithm = Algorithm.HMAC256(jwtSecret)  // Use the same secret key used to sign the token
            val verifier = JWT.require(algorithm).build()
            return verifier.verify(token)
        } catch (e: Exception) {
            println("Invalid token or error decoding the token: ${e.localizedMessage}")
            return null
        }
    }

    fun getUserDataFromToken(token: String): Map<String, Any>? {
        val decodedJWT = decodeJWT(token)
        return decodedJWT?.let {
            mapOf(
                "email" to it.subject,  // Extract the subject (email)
                "role" to it.getClaim("role").asString()  // Extract the role claim
            )
        }
    }
}