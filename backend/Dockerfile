# Multi-stage build for the Spring Boot backend module.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./mvnw
RUN chmod +x ./mvnw
RUN ./mvnw -B dependency:go-offline

COPY src ./src
RUN ./mvnw -B clean package -DskipTests
RUN JAR_FILE=$(find /workspace/target -maxdepth 1 -name '*.jar' ! -name '*.original' | head -n 1) \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /workspace/app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget \
    && addgroup -S portfolio \
    && adduser -S portfolio -G portfolio

COPY --from=build /workspace/app.jar /app/app.jar
RUN chown portfolio:portfolio /app/app.jar

USER portfolio
EXPOSE 8090
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8090/v3/api-docs >/dev/null || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
