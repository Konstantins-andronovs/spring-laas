# Start with base image
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="Konstantins Andronovs"

# Add a temporary volume
VOLUME /build/tmp

# Expose Port 8080
EXPOSE 8080

# Application Jar File
ARG JAR_FILE=build/libs/laas-0.0.1-SNAPSHOT.jar

# Add Application Jar File to the Container
ADD ${JAR_FILE} laas.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/laas.jar"]