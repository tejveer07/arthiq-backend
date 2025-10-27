# --------------------------
# Stage 1: Build the JAR
# --------------------------
FROM maven:3.9.6-eclipse-temurin-22 AS build

# Set working directory
WORKDIR /app

# Copy Maven files and source code
COPY pom.xml .
COPY src ./src

# Build the Spring Boot JAR without running tests
RUN mvn clean package -DskipTests

# --------------------------
# Stage 2: Create runtime image
# --------------------------
FROM eclipse-temurin:22-jdk

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the JAR with production profile
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
