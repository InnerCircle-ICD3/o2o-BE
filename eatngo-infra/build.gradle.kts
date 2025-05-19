plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.jpa")
    id("org.springframework.boot")
}

dependencies {
    // core 모듈 의존성
    implementation(project(":eatngo-core"))

    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter-web")  // REST API 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect")              // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화

    // 테스트 의존성
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")

    // AWS S3 의존성
    implementation("software.amazon.awssdk:s3:2.23.15")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-property:5.8.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("io.mockk:mockk:1.13.10")                        // mockk
    testImplementation("com.appmattus.fixture:fixture:1.2.0")           // Kotlin-fixture

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = true
    archiveFileName.set("${project.name}.jar")
}