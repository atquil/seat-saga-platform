# --- STAGE 1: Extractor ---
# Use a lightweight JRE base image (Eclipse Temurin, Java 21, Alpine Linux)
FROM eclipse-temurin:21-jre-alpine AS builder

# Set working directory inside the container for build artifacts
WORKDIR /builder

# Copy the JAR file built by Gradle into the container
# (Gradle typically outputs to build/libs/)
COPY build/libs/seat-saga-platform.jar seat-saga-api.jar

# Use Spring Boot's layertools to extract the JAR into layers
# This splits dependencies, loader, snapshots, and application code
RUN java -Djarmode=layertools -jar seat-saga-api.jar extract


# --- STAGE 2: Runner ---
# Use the same lightweight JRE base image for running the app
FROM eclipse-temurin:21-jre-alpine

# Set working directory for the running application
WORKDIR /application

# SECURITY: Create a non-root user
# If the app is compromised, the attacker won't have root host access
RUN adduser -D springuser
USER springuser

# Copy extracted layers from the builder stage
# Order matters: copy least frequently changing layers first
# This maximizes Docker layer caching and speeds up rebuilds
COPY --from=builder /builder/dependencies/ ./
COPY --from=builder /builder/spring-boot-loader/ ./
COPY --from=builder /builder/snapshot-dependencies/ ./
COPY --from=builder /builder/application/ ./

# Define the container entrypoint
# Use Spring Boot's JarLauncher to start the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]
