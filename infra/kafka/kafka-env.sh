#!/usr/bin/env bash

if [ -z $HOSTNAME ]
then
    HOSTNAME=`hostname -s`
    echo "HOSTNAME variable is not set, trying to set with command 'hostname -f' -> ${HOSTNAME}"
fi
echo "HOSTNAME variable -> ${HOSTNAME}"
export HOSTNAME
