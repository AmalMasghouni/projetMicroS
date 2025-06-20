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

