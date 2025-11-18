# ---------- STAGE 1: Build ----------
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first and download dependencies (caching)
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy the source code
COPY src ./src

# Build the JAR file
RUN mvn -q -e -DskipTests clean package


# ---------- STAGE 2: Run ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot will run on
EXPOSE 8080

# Spring uses this port dynamically from Render
ENV PORT=8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]