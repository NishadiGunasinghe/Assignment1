# Use AdoptOpenJDK 17 as base image
FROM openjdk:17-alpine as builder

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file from the host to the container
COPY /build/libs/lbu_auth.jar /app/lbu_auth.jar

# Expose the port that your Spring Boot application uses (if any)
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "lbu_auth.jar"]
