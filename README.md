# Upgrade Version for database with CQRS

This is demo code for test and understand 
upgrade version of database with 0 downtime.

## How to start

You need docker and docker-compose installed on machine and git or unzip

1. Clone this project or download as zip and extract
2. Run preferred shell and navigate to directory with source code of this project 
(next commands are for linux environment and windows alternatives) 

### External services

This services will run all time required configurations can be done online

#### PostgreSQL
 
* Linux or Mac
```bash
./infra/postgresql/postgresql-start.sh

./infra/postgresql/pg-admin-init.sh
```
* Windows
```
docker-compose -f postgresql-compose.yml up -d
docker cp ./pg-admin.py postgresql_db-admin_1:/tmp/pg-admin.py
docker exec -it postgresql_db-admin_1 python /tmp/pg-admin.py
``` 
    
#### Kafka + Zookeeper

* Linux or Mac
```bash
./infra/kafka/kafka-start.sh
```
* Windows
```
set HOSTNAME=<machine_ip_address>
docker-compose -f kafka-compose.yml up -d
```
  
Initial setup of external services is complete continue to create application

### Create application

Backend application has two version we compile them and create 
docker images at the same time but start when we really need them.

Frontend application work with any combinations of running back applications

#### Backend

Build applications

* Linux, Mac or Windows
```bash
docker run -it --rm --name upgrade-version-build \
 -v "$PWD":/usr/src/ -w /usr/src/ maven:3.5.2-jdk-8 \
 mvn clean package
```

Build Docker images

* Linux, Mac or Windows
```bash
docker build -t cmd-v1-handler-v2 -f cmd-v1-handler-v2/Dockerfile ./cmd-v1-handler-v2
docker build -t cmd-v2-handler-v1 -f cmd-v2-handler-v1/Dockerfile ./cmd-v2-handler-v1
docker build -t command-adapter-v1 -f command-adapter-v1/Dockerfile ./command-adapter-v1
docker build -t command-adapter-v2 -f command-adapter-v2/Dockerfile ./command-adapter-v2
docker build -t command-handler-v1 -f command-handler-v1/Dockerfile ./command-handler-v1
docker build -t command-handler-v2 -f command-handler-v2/Dockerfile ./command-handler-v2
docker build -t query-adapter-v1 -f query-adapter-v1/Dockerfile ./query-adapter-v1
docker build -t query-adapter-v2 -f query-adapter-v2/Dockerfile ./query-adapter-v2
```

#### Frontend

* Linux, Mac or Windows
```bash
docker build -t web-ui:uv -f web-ui/Dockerfile ./web-ui
```

### Run application

#### Init database v1

* Linux or Mac
```bash
./infra/postgresql/postgresql-script.sh ./infra/postgresql/scripts/create_db_v1.sql
./infra/postgresql/postgresql-script.sh ./infra/postgresql/scripts/create_tables_v1.sql ticket ticket ticket_v1
```
* Windows
```
// TODO
``` 

#### Init database v2

* Linux or Mac
```bash
./infra/postgresql/postgresql-script.sh ./infra/postgresql/scripts/create_db_v2.sql
./infra/postgresql/postgresql-script.sh ./infra/postgresql/scripts/create_tables_v2.sql ticket ticket ticket_v2
```
* Windows
```
// TODO
``` 

#### Init topics 

* Linux or Mac
```bash
./infra/kafka/kafka-topic.sh ticket-v1
./infra/kafka/kafka-topic.sh ticket-v2
```
* Windows
```
// TODO
``` 

#### Run app 

* Linux or Mac
```bash
docker-compose -f appV1-compose.yaml -f appV2-compose.yaml -f transform-compose.yaml -f web-ui-compose.yaml up -d
```
* Windows
```
// TODO
``` 

## Test upgrade

```bash
docker-compose -f appV1-compose.yaml -f appV2-compose.yaml -f transform-compose.yaml stop cmd-v2-handler-v1 cmd-v1-handler-v2 command-adapter-v2 command-handler-v2 query-adapter-v2

docker-compose -f appV1-compose.yaml -f appV2-compose.yaml -f transform-compose.yaml start command-adapter-v2 command-handler-v2 query-adapter-v2

docker-compose -f appV1-compose.yaml -f appV2-compose.yaml -f transform-compose.yaml start cmd-v2-handler-v1 cmd-v1-handler-v2 
```