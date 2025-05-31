FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# JAR 파일만 복사 (로컬에서 미리 build되어 있어야 함)
COPY eatngo-customer-api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]