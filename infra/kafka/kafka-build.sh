#!/usr/bin/env bash

RUN_DIR=`pwd`
echo "Run from '$RUN_DIR'"

SCRIPT_DIR=$(dirname "$0")
echo "Change location to '$SCRIPT_DIR'"
cd "$SCRIPT_DIR"
SCRIPT_DIR=`pwd`

cd zookeeper
docker build -t zookeeper:uv -f Dockerfile .

cd "$SCRIPT_DIR"

cd kafka
docker build -t kafka:uv -f Dockerfile .

echo "Cleanup"
docker rm $(docker ps -a -q)
docker rmi $(docker images | grep "^<none>" | awk "{print $3}")

echo "Change location back '$RUN_DIR'"
cd "$RUN_DIR"
pwd