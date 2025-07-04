import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22" apply false
    kotlin("plugin.jpa") version "1.9.22" apply false
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.stack_oder_flow.eatngo"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")


    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1")
        }
    }
    // 실행 모듈 목록
    val executableModules = listOf("eatngo-customer-api", "eatngo-store-owner-api")
    val enableJarModules = listOf("common")

    if (name in executableModules) {
        // 실행 모듈: jar, bootJar, bootRun 모두 활성화
        tasks.withType<Jar> { enabled = true }
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = true }
        tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> { enabled = true }
    } else if (name in enableJarModules) {
        // 공통 모듈: jar만 활성화, bootJar/bootRun 비활성화
        tasks.withType<Jar> { enabled = true }
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
        tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> { enabled = false }
    } else {
        // 나머지 모듈: 모두 비활성화
        tasks.withType<Jar> { enabled = false }
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
        tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> { enabled = false }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("kotest.tags.exclude", "integration")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
