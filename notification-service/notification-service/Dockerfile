FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/notification-service-*.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "app.jar"]