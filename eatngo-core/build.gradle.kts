plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter-web")  // REST API 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect")              // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화

    // 테스트 의존성
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = true
    archiveFileName.set("${project.name}.jar")
}