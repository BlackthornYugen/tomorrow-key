# Use the official Gradle image to build the application
FROM gradle:7.5.0-jdk11 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# Use the official OpenJDK image to run the application
FROM openjdk:11-jre-slim
VOLUME /tmp
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]