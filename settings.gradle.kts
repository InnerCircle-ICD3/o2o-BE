plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "eatngo"

include(
    "eatngo-core",
    "eatngo-customer-api",
    "eatngo-store-owner-api",
    "eatngo-infra",
    "eatngo-common",
    "eatngo-auth",
)

include(":eatngo-common:swagger")
include(":eatngo-common:p6spy")
include(":eatngo-common:logging")
include(":eatngo-common:secrets")
