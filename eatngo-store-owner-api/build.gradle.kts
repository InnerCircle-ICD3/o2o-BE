plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // 내부 모듈 의존성
    implementation(project(":eatngo-common:swagger"))
    implementation(project(":eatngo-common:unit-test"))

    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter-web")  // REST API 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect")              // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화

    // API 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-common:2.8.5")       // Swagger 공통 기능

    // 테스트 의존성
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}