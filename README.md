# Stream4-API

## Create database locally

```bash
docker run --name stream4-db -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=stream4 -e MYSQL_USER=admin -e MYSQL_PASSWORD=password -p 3306:3306 -d mysql:8
```