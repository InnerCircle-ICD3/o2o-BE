plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // Spring Boot 기본 의존성

    implementation("org.springframework:spring-context") // REST API 지원
    implementation("org.springframework.boot:spring-boot-starter-web") // SSE Emitter 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // Transactional 사용
    // tsid 생성기
    implementation("com.github.f4b6a3:tsid-creator:5.2.6")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 테스트 의존성
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("io.mockk:mockk:1.13.10") // mockk

    // Redis
    implementation("net.javacrumbs.shedlock:shedlock-spring:5.13.0")
    // Circuit Breaker 모듈
    implementation(project(":eatngo-common:circuit-breaker"))

    // AOP 지원 (Circuit Breaker용)
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
    archiveFileName.set("${project.name}.jar")
}
