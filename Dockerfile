# Use an official OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR into the container
COPY target/callpluto-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port (adjust if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]