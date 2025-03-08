FROM openjdk:17-alpine
COPY build/libs/*.jar app.jar
COPY src/main/resources/application.yml application.yml
COPY src/main/resources/application-prod.yml application-prod.yml
COPY src/main/resources/application-secret.yml application-secret.yml
ENTRYPOINT ["java", "-jar", "app.jar"]
