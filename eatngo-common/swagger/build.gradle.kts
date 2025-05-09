plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    // API 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-common:2.8.5")       // Swagger 공통 기능
}