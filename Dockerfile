FROM gradle:8.6-jdk21 AS build

WORKDIR /app

# Copy gradle files first to leverage cache
COPY build.gradle .
COPY gradlew .
COPY gradle/ gradle/

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]