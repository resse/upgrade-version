#!/usr/bin/env bash

RUN_DIR=`pwd`
echo "Run from '$RUN_DIR'"

SCRIPT_DIR=$(dirname "$0")
echo "Change location to '$SCRIPT_DIR'"
cd "$SCRIPT_DIR"
SCRIPT_DIR=`pwd`

TARGET_SQL=$1

if [[ -z "${TARGET_SQL}" ]]; then
    echo "run with parameters postgresql-script.sh <script-file> [<user>] [<password>] [<database>] [<host>] [<port>]"
    exit 1
fi

FILE_TO_RUN="${RUN_DIR}/${TARGET_SQL}"

if [ ! -f "${FILE_TO_RUN}" ]; then
    echo "File not found!"
    echo "run with parameters postgresql-script.sh <script-file> [<user>] [<password>] [<database>] [<host>] [<port>]"
    exit 1
fi

USER=$2
if [[ -z "${USER}" ]]; then
    USER="postgres"
fi

PASSWORD=$3
if [[ -z "${PASSWORD}" ]]; then
    PASSWORD="example"
fi

DATABASE=$4
if [[ -z "${DATABASE}" ]]; then
    DATABASE="postgres"
fi

DB_HOST=$5
if [[ -z "${DB_HOST}" ]]; then
    DB_HOST="postgres"
fi

DB_PORT=$5
if [[ -z "${DB_PORT}" ]]; then
    DB_PORT="5432"
fi

docker run -it --rm --network postgresql_default --link db:db \
  -v ${FILE_TO_RUN}:/tmp/commands.sql \
  postgres:alpine \
  sh -c "echo \"${DB_HOST}:${DB_PORT}:${DATABASE}:${USER}:${PASSWORD}\" > ~/.pgpass && chmod 600 ~/.pgpass && /usr/local/bin/psql -h ${DB_HOST} -U ${USER} -d ${DATABASE} -f /tmp/commands.sql"

echo "Change location back '$RUN_DIR'"
cd "$RUN_DIR"
pwd