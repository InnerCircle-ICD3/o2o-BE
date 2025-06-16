plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.jpa")
    id("org.springframework.boot")
}

dependencies {
    // core 모듈 의존성
    implementation(project(":eatngo-core"))
//    implementation(project(":eatngo-common"))

    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter-web") // REST API 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // 테스트 의존성
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")

    // AWS S3 의존성
    implementation("software.amazon.awssdk:s3:2.23.15")

    // 코루틴 의존성 추가
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-property:5.8.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("io.mockk:mockk:1.13.10") // mockk
    testImplementation("com.appmattus.fixture:fixture:1.2.0") // Kotlin-fixture

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // PostgreSQL
    implementation("org.postgresql:postgresql:42.7.3") // PG 17 호환

    // h2
    runtimeOnly("com.h2database:h2")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
    archiveFileName.set("${project.name}.jar")
}
