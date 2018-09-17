#!/bin/bash

# check arguments is a comma-separated list of all ZooKeeper servers and ZooKeeper node number:
if [ $# -eq 0 ]
then
    ZOOKEEPER_SERVERS=zookeeper
    ZOOKEEPER_MY_ID=1
else
    ZOOKEEPER_SERVERS=$1
    ZOOKEEPER_MY_ID=$2
fi

# the first argument provided is a comma-separated list of all ZooKeeper servers in the ensemble:
export ZOOKEEPER_SERVERS

# the second argument provided is vat of this ZooKeeper node:
export ZOOKEEPER_MY_ID

# create data and blog directories:
mkdir -p $ZOOKEEPER_DATA_DIR
mkdir -p $ZOOKEEPER_DATA_LOG_DIR

# create myID file:
echo "$ZOOKEEPER_MY_ID" | tee $ZOOKEEPER_DATA_DIR/myid

# get current IP address
IPADDRESS=`ip -4 addr show scope global dev eth0 | grep inet | awk '{print \$2}' | cut -d / -f 1`

# now build the ZooKeeper configuration file:
ZOOKEEPER_CONFIG=
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"tickTime=$ZOOKEEPER_TICK_TIME"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataDir=$ZOOKEEPER_DATA_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataLogDir=$ZOOKEEPER_DATA_LOG_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"clientPort=$ZOOKEEPER_CLIENT_PORT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"initLimit=$ZOOKEEPER_INIT_LIMIT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"syncLimit=$ZOOKEEPER_SYNC_LIMIT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"maxClientCnxns=$ZOOKEEPER_MAX_CLIENT_CNXNS"

# Put all ZooKeeper server IPs into an array:
IFS=', ' read -r -a ZOOKEEPER_SERVERS_ARRAY <<< "$ZOOKEEPER_SERVERS"
export ZOOKEEPER_SERVERS_ARRAY=$ZOOKEEPER_SERVERS_ARRAY

# now append information on every ZooKeeper node in the ensemble to the ZooKeeper config:
for index in "${!ZOOKEEPER_SERVERS_ARRAY[@]}"
do
    ZKID=$(($index+1))
    ZKIP=${ZOOKEEPER_SERVERS_ARRAY[index]}
    if [ $ZKID == $ZOOKEEPER_MY_ID ]
    then
        # if IP's are used instead of hostnames, every ZooKeeper host has to specify itself as follows
        ZKIP=$IPADDRESS
    fi
    ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"server.$ZKID=$ZKIP:2888:3888"
done

# Finally, write config file:
echo "$ZOOKEEPER_CONFIG" | tee $ZOOKEEPER_HOME/conf/zoo.cfg

# start the server:
$ZOOKEEPER_HOME/bin/zkServer.sh start-foreground
