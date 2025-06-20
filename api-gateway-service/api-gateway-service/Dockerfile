# Étape 1 : Utiliser l’image Java officielle
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : Créer un dossier pour l'application
WORKDIR /app

# Étape 3 : Copier le jar dans l’image
COPY target/api-gateway-*.jar app.jar

# Étape 4 : Exposer le port (si tu as un port spécifique)
EXPOSE 8083

# Étape 5 : Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]
