plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "2.3.5"
    kotlin("plugin.serialization") version "1.9.10" // Apply Kotlin Serialization plugin
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    implementation("io.ktor:ktor-server-call-logging:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("io.ktor:ktor-server-auth:2.3.0")  // For JWT authentication
    implementation("io.ktor:ktor-server-auth-jwt:2.3.0")  // For JWT authentication
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:2.3.0")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("io.ktor:ktor-server-status-pages:2.3.5")
    implementation("io.ktor:ktor-server-auth:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}