config Keycloack realm :
docker ps
docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.1 start-dev
eureka.instance.instance-id=${spring.application.name}-${random.uuid}
ajoutDockerFile
ajout docker-compose.yml

lance docker-compose up -d
inserer les donner dans bd mongo
docker exec -it mongodb mongosh 11_> 27
use product-service
db.product.insertOne({
  name: "Produit exemple",
  description: "Description exemple",
  price: NumberDecimal("123.45")
});
db.product.find().pretty()(pour verifier les données)


Exemple d’ordre de démarrage idéal :
D’abord API Gateway parce que c’est le point d’entrée unique côté client. Il faut vérifier que tu peux router correctement les requêtes.

Ensuite, Discovery Server pour que tous tes microservices s’enregistrent et se découvrent entre eux. Cela permet à l’API Gateway de router dynamiquement.

Enfin, Configuration Server pour centraliser la gestion des configurations, ce qui simplifie le déploiement et la maintenance.



docker exec -it product-service sh
get token from keycloack
curl -X POST "http://keycloak:8080/realms/microservices-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=api-gateway" \
  -d "grant_type=password" \
  -d "username=amal.masghouni@value.com.tn" \
  -d "password=user123"


Voici le fichier app.js complet corrigé pour permettre au review-service de communiquer avec le product-service sécurisé par Keycloak, en utilisant le flux client_credentials.

✅ Étape 1 – Configuration dans Keycloak
Connecte-toi à Keycloak (http://localhost:8180) :

🎯 Dans le realm microservices-realm :
Va dans Clients → Créer un client

Remplis :

Client ID : internal-client

Client authentication : ✅ activé

Client type : Confidential

Root URL : tu peux laisser vide
→ Enregistrer

Dans l’onglet "Paramètres" :

Enabled ✅

Standard Flow ❌

Direct Access Grants ❌

Service Accounts ✅ (coché)
→ Enregistrer
Paramètre	Valeur à mettre
Client ID	internal-client (ou autre)
Client type	Confidential
Client authentication	✅ On
Authorization	❌ Off (désactive le fine-grained auth)
Authentication flow
🔹 Standard flow (code)	❌ Désactivé
🔹 Implicit flow	❌ Désactivé
🔹 Direct access grants	❌ Désactivé
🔹 OAuth 2.0 Device Grant	❌ Désactivé (pas utile ici)
🔹 OIDC CIBA Grant	❌ Désactivé (inutile ici)
🔹 Service accounts roles	✅ Activé (c'est ce qu'on veut)

