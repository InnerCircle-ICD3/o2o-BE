plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    implementation("io.kotest:kotest-runner-junit5:5.8.1")
    implementation("io.kotest:kotest-assertions-core:5.8.1")
    implementation("io.kotest:kotest-property:5.8.1")
    implementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    implementation("org.springframework.boot:spring-boot-starter-test")
} 