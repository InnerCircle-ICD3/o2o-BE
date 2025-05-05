plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "eatngo"

include(
    "eatngo-core",
    "eatngo-api",
    "eatngo-infra",
    "eatngo-common",
    "eatngo-api:eatngo-customer-api",
    "eatngo-api:eatngo-store-owner-api"
)

project(":eatngo-api:eatngo-customer-api").projectDir = file("eatngo-api/eatngo-customer-api")
project(":eatngo-api:eatngo-store-owner-api").projectDir = file("eatngo-api/eatngo-store-owner-api")