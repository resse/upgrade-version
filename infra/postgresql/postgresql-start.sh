#!/usr/bin/env bash

RUN_DIR=`pwd`
echo "Run from '$RUN_DIR'"

SCRIPT_DIR=$(dirname "$0")
echo "Change location to '$SCRIPT_DIR'"
cd "$SCRIPT_DIR"
SCRIPT_DIR=`pwd`

. ./postgresql-env.sh

docker-compose -f postgresql-compose.yml up -d

echo "Change location back '$RUN_DIR'"
cd "$RUN_DIR"
pwd