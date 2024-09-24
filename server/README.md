# Start project

## Run db under docker
```bash
docker run --detach --name multeract-maria -p 3300:3306 --env MARIADB_ROOT_PASSWORD="isThisSecret?"  mariadb:latest
```