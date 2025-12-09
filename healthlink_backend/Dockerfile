### Multi-stage build for Spring Boot on Render

# Build stage
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app
COPY . .
# Skip tests for faster deploy; remove -x test if you want tests in CI
RUN ./gradlew clean bootJar -x test

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

