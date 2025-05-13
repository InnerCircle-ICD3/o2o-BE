plugins {
   kotlin("jvm") version "1.9.22"
   id("org.springframework.boot")
}

dependencies {
   implementation("org.springframework.boot:spring-boot-starter-logging")
   implementation("net.logstash.logback:logstash-logback-encoder:7.3")
}