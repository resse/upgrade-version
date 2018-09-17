#!/usr/bin/env bash

RUN_DIR=`pwd`
echo "Run from '$RUN_DIR'"

SCRIPT_DIR=$(dirname "$0")
echo "Change location to '$SCRIPT_DIR'"
cd "$SCRIPT_DIR"
SCRIPT_DIR=`pwd`

cd "$SCRIPT_DIR"

TOPIC=$1
PARTITIONS=$2
REPLICAS=$3

NUMBER_REGEXP='^[0-9]+$'

if ! [[ -n "${PARTITIONS}" && ${PARTITIONS} =~ $NUMBER_REGEXP && ${PARTITIONS} -gt 0 ]] ; then
   PARTITIONS=1
fi

if ! [[ -nz "${REPLICAS}" && ${REPLICAS} =~ $NUMBER_REGEXP && ${REPLICAS} -gt 0 ]] ; then
   REPLICAS=1
fi

if [[ -z "${TOPIC}" ]]; then
    echo "run with parameters kafka-topic.sh <topic-name> [<partitions>] [<replicas>]"
    exit 1
fi

echo "PARTITIONS = $PARTITIONS"
echo "REPLICATION_FACTOR = $REPLICAS"

docker run -it --rm --network postgresql_default \
  --link kafka:kakfa --link zookeeper:zookeeper \
  kafka:latest /opt/kafka/bin/kafka-topics.sh \
  --zookeeper zookeeper:2181 --create \
  --topic ${TOPIC} --partitions ${PARTITIONS} --replication-factor ${REPLICAS} --config retention.ms=86400000

echo "Change location back '$RUN_DIR'"
cd "$RUN_DIR"
pwd