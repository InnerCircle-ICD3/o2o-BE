plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // 내부 모듈 의존성
    implementation(project(":eatngo-common:secrets"))

    // PostgreSQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.3") // PG 17 호환
}
