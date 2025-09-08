# Step 1: Use an official OpenJDK base image from Docker Hub
FROM openjdk:21-jdk

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the Spring Boot JAR file into the container
COPY target/incident-0.0.1-SNAPSHOT.jar /app/incident-0.0.1-SNAPSHOT.jar

# Step 4: Copy the application.yml configuration file from the root of the project
COPY application.yaml /app/application.yaml

# Step 5: Expose the port your application runs on
EXPOSE 8080

# Step 6: Define the command to run your Spring Boot application
CMD ["java", "-jar", "/app/incident-0.0.1-SNAPSHOT.jar"]
