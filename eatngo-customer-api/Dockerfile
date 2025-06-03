FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y ca-certificates && update-ca-certificates

WORKDIR /app

# JAR 파일만 복사 (로컬에서 미리 build되어 있어야 함)
COPY eatngo-customer-api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Dhttps.protocols=TLSv1.2", "-jar", "app.jar"]