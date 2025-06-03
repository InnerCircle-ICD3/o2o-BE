plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // 내부 모듈 의존성
    implementation(project(":eatngo-common:swagger"))
    implementation(project(":eatngo-auth"))

    // core 모듈 의존성
    implementation(project(":eatngo-core"))
    implementation(project(":eatngo-infra"))

    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter-web") // REST API 지원
    implementation("org.springframework.boot:spring-boot-starter-security") // Spring Security 지원
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 리플렉션 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin JSON 직렬화/역직렬화

    // API 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5") // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-common:2.8.5") // Swagger 공통 기능

    // AWS Parameter Store
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.2.1"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")
    implementation("io.awspring.cloud:spring-cloud-aws-starter")

    // 테스트 의존성
    testImplementation("io.rest-assured:rest-assured:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.10") // mockk
    testImplementation("com.appmattus.fixture:fixture:1.2.0") // Kotlin-fixture
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2") // 필요

    // h2
    runtimeOnly("com.h2database:h2")
}
