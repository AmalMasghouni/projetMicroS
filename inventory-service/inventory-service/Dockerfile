# Étape 1 : build Maven
FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app

# Copier pom.xml et sources
COPY pom.xml .
COPY src ./src

# Build sans test pour générer le jar
RUN mvn clean package -DskipTests

# Étape 2 : image runtime Java
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/inventory-service-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 8084

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
