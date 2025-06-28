mvn clean package
docker-compose up -d --build
docker exec -it mysql-db mysql -uroot -pmysql
CREATE DATABASE order_db;