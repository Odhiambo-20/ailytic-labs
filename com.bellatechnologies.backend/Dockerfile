FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
COPY src src
RUN mvn -B -DskipTests package

FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS runtime
RUN groupadd --system bella && useradd --system --gid bella --home-dir /app bella
WORKDIR /app
COPY --from=build --chown=bella:bella /workspace/target/backend-1.0.0.jar app.jar
USER bella
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/urandom"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
