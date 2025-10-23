# Use a base image with Java 17
FROM openjdk:21-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
# Assumes you run `mvn package` first
COPY target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]