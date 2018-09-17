#!/bin/bash

set -e

if [ -z $ZOOKEEPER_SERVERS ]
then
    ZOOKEEPER_SERVERS="zookeeper:2181"
    export ZOOKEEPER_SERVERS
fi

if [ -z $KAFKA_ADVERTISED_PORT ]
then
    KAFKA_ADVERTISED_PORT=9092
    export KAFKA_ADVERTISED_PORT
fi

if [ -z $KAFKA_ADVERTISED_HOST ]
then
    KAFKA_ADVERTISED_HOST=$(hostname)
    export KAFKA_ADVERTISED_HOST
fi

# The broker id for this server.
export BROKER_ID=$(hostname -i | sed 's/\([0-9]*\.\)*\([0-9]*\)/\2/')

sed -i 's/^\(broker\.id=\).*/\1'$BROKER_ID'/' $KAFKA_HOME/config/server.properties
sed -i 's|^#\(listeners=PLAINTEXT://\)\(:9092\)|\1'`echo 0.0.0.0`'\2|' $KAFKA_HOME/config/server.properties
sed -i 's|^\(zookeeper.connect=\)\(localhost:2181\)|\1'$ZOOKEEPER_SERVERS'|' $KAFKA_HOME/config/server.properties
sed -i 's|^#\(advertised.listeners=PLAINTEXT://\)your.host.name:9092|\1'`echo $KAFKA_ADVERTISED_HOST:$KAFKA_ADVERTISED_PORT`'|' $KAFKA_HOME/config/server.properties

$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
