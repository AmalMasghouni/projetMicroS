# Utilise une image Java officielle légère
FROM openjdk:17-jdk-slim

# JAR à copier (à adapter selon ton build Maven/Gradle)
ARG JAR_FILE=target/discovery-server-0.0.1-SNAPSHOT.jar

# Copier le jar dans le container
COPY ${JAR_FILE} app.jar

# Exposer le port 8761 (port Eureka)
EXPOSE 8761

# Lancer l'application
ENTRYPOINT ["java","-jar","/app.jar"]
