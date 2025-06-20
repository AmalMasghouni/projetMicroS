# Utilise l'image officielle Java 17
FROM eclipse-temurin:17-jdk

# Définit le dossier de travail dans le conteneur
WORKDIR /app

# Copie le fichier jar compilé dans l'image
COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar

# Expose le port sur lequel l'app écoute
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
