# Digital Course Marketplace Backend

This is the backend application for a Digital Course Marketplace built using Kotlin, Ktor, and SQLite.
The system supports Admin, Creator, and Customer roles with APIs for user management, course creation,
course purchase, and platform statistics.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java JDK 11 or above**: The backend is built using Kotlin, which requires Java.
- **Kotlin**: The application is developed using Kotlin.
- **SQLite**: Used as the database for the application.
- **Gradle**: Build tool used to manage dependencies and build the project.

## Server Configuration

PORT=8080

# Database Configuration

DB_URL=jdbc:sqlite:./db/course_marketplace.db
DB_USERNAME=your-db-username (SQLite does not require this, leave empty)
DB_PASSWORD=your-db-password (SQLite does not require this, leave empty)

# JWT Secret (for authentication)

JWT_SECRET=your_jwt_secret_key

# Logging Configuration (Optional)

LOG_LEVEL=INFO

## Setting Up the Application

### 1. Clone the Repository

Clone the repository to your local machine:

git clone https://github.com/vivek9817/KotlinBackend.git
cd digital-course-marketplace-backend