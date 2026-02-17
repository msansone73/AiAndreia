# ==============================================================
# Stage 1 — Build
# ==============================================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper & POM first (layer-caching for dependencies)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build the fat JAR
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ==============================================================
# Stage 2 — Runtime
# ==============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
