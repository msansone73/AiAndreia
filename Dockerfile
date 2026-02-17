# Estágio 1: Compilação (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copia apenas o pom.xml para baixar as dependências (cache)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copia o código fonte e gera o jar
COPY src ./src
RUN mvn clean package -DskipTests


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
