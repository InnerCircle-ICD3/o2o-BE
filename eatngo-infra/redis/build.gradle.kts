plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot")
}

dependencies {
    // 내부 모듈 의존성
    implementation(project(":eatngo-common:secrets"))
    implementation(project(":eatngo-core"))

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
