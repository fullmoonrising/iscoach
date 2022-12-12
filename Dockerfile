FROM openjdk:18-jdk-alpine as base
WORKDIR /app
ENTRYPOINT ["java","-jar","folobot.jar"]

FROM base as image-local
COPY /build/libs/folobot-*.jar folobot.jar

FROM base as image-cloud
COPY folobot-*.jar folobot.jar

