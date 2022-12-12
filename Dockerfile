FROM openjdk:18-jdk-alpine
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ADD folobot-*.jar folobot.jar

ENTRYPOINT ["java","-jar","folobot.jar"]