# syntax=docker/dockerfile:1.7

# -----------------------------------------------------------------------------
# Stage 1: Build & Package
# -----------------------------------------------------------------------------
FROM eclipse-temurin:26-jdk-alpine AS build

WORKDIR /workspace

# Install curl if needed for custom tooling
RUN apk add --no-cache curl

# Copy Maven wrapper and POM configuration
COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Resolve dependencies before copying source code.
# The BuildKit cache keeps Maven artifacts available across builds.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress dependency:resolve

# Copy application source code
COPY src src

# Build and package the application artifact
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress \
    -DskipTests clean package \
    && cp target/somqr-payment-service-*.jar application.jar

# Extract jar for optimal container startup
RUN java -Djarmode=tools -jar application.jar \
    extract --destination /workspace/extracted

# -----------------------------------------------------------------------------
# Stage 2: Minimal Production Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:26-jre-alpine AS runtime

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom" \
    SERVER_PORT=8080

WORKDIR /app

# Create dedicated non-root application user and logging directory
RUN addgroup -S -g 1001 appgroup \
    && adduser -S -u 1001 -G appgroup appuser \
    && mkdir -p /app/logs \
    && chown -R appuser:appgroup /app

# Copy extracted libraries and application jar
COPY --from=build --chown=appuser:appgroup /workspace/extracted/lib/ ./lib/
COPY --from=build --chown=appuser:appgroup /workspace/extracted/application.jar ./application.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
