# ----- Stage 1: Build The App -----
# Use a base image with Maven and Java 17 to build our .jar file
FROM maven:3.9-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to leverage Docker layer caching
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Run the Maven package command (this creates the /app/target/*.jar file)
# We skip tests because they require a live DB
RUN mvn clean package -DskipTests

# ----- Stage 2: Create The Final Image -----
# Use a lightweight base image with just the Java 17 Runtime
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy ONLY the built JAR from the 'build' stage (Stage 1)
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]