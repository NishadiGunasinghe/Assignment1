# Use AdoptOpenJDK 17 as base image
FROM openjdk:17-alpine as builder

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file from the host to the container
COPY D:/PROJECT/ns_assignment/bu-course/target/lbu-course-0.0.1-SNAPSHOT.jar /app/lbu-course-0.0.1-SNAPSHOT.jar

# Expose the port that your Spring Boot application uses (if any)
EXPOSE 8081

# Command to run the Spring Boot application
CMD ["java", "-jar", "lbu-course-0.0.1-SNAPSHOT.jar"]
