# ========================
# Stage 1: Build the JAR
# ========================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# ========================
# Stage 2: Run the JAR
# ========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/incident-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
