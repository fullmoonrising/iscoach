FROM openjdk:18-jdk-alpine as base
WORKDIR /app
COPY /build/libs/iscoach-*.jar iscoach.jar
ENTRYPOINT ["java","-jar","iscoach.jar"]

