# This comment will become the configuration name in IntelliJ
# <docker.imageName>my-custom-name</docker.imageName>
FROM gradle:8.6-jdk21 AS build

WORKDIR /app

# Copy gradle files first to leverage cache
COPY build.gradle .
COPY gradlew .
COPY gradle/ gradle/

# Download dependencies (this will cache them unless build files change)
RUN gradle dependencies --no-daemon

# Copy application source
COPY src ./src

# Build the application
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]