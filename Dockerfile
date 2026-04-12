FROM gradle:8.10-jdk23 AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew gradlew.bat settings.gradle build.gradle ./
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]