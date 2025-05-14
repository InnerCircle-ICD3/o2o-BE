plugins {
   id("org.springframework.boot")
}

dependencies {
   implementation("org.springframework.boot:spring-boot-starter-logging")
   implementation("net.logstash.logback:logstash-logback-encoder:7.3")
}

tasks.bootJar{
   enabled = false
}

tasks.jar {
   enabled = true
   archiveFileName.set("${project.name}.jar")
}
