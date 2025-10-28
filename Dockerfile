# --------------------------
# Stage 1: Build the JAR
# --------------------------
FROM maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# --------------------------
# Stage 2: Create runtime image
# --------------------------
FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run with production profile
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
