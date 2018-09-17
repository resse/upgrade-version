#!/usr/bin/env bash

RUN_DIR=`pwd`
echo "Run from '$RUN_DIR'"

SCRIPT_DIR=$(dirname "$0")
echo "Change location to '$SCRIPT_DIR'"
cd "$SCRIPT_DIR"
SCRIPT_DIR=`pwd`

docker cp ./pg-admin.py postgresql_postgres-admin_1:/tmp/pg-admin.py
docker exec -it postgresql_postgres-admin_1 python /tmp/pg-admin.py

echo "Change location back '$RUN_DIR'"
cd "$RUN_DIR"
pwd
