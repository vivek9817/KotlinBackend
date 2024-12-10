package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import org.example.controller.userRoutes
import org.example.database.DataBaseSchema
import org.example.repository.UserRepository
import org.example.service.CommonServices
import org.example.utils.DatabaseConnector

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json() // Enable JSON serialization/deserialization
    }

    install(StatusPages) {

    }

    routing {

        // Initialize database schema
        val connection = DatabaseConnector.connect()
        connection?.let {
            DataBaseSchema.createSchema(it)
        }

        val userRepository = UserRepository()
        val userService = CommonServices(userRepository)

        userRoutes(userService)
    }


}