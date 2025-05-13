plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")         // API
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")           // 내부 구현체
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // 테스트 의존성
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
    archiveFileName.set("${project.name}.jar")
}