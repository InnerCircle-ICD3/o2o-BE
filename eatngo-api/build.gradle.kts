plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":eatngo-api:eatngo-customer-api"))
    implementation(project(":eatngo-api:eatngo-store-owner-api"))
}