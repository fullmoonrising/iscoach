FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY build/libs/iscoach-*.jar iscoach.jar
ENTRYPOINT ["java","-jar","iscoach.jar"]
