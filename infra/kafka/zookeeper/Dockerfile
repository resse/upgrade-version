FROM openjdk:8-jdk

ENV ZOOKEEPER_DISTRIB=zookeeper-3.4.10.tar.gz \
    ZOOKEEPER_HOME=/opt/zookeeper \
    ZOOKEEPER_CLIENT_PORT=2181 \
    ZOOKEEPER_DATA_DIR=/var/lib/zookeeper/ \
    ZOOKEEPER_DATA_LOG_DIR=/var/log/zookeeper/ \
    ZOOKEEPER_TICK_TIME=2000 \
    ZOOKEEPER_INIT_LIMIT=5 \
    ZOOKEEPER_SYNC_LIMIT=2 \
    ZOOKEEPER_MAX_CLIENT_CNXNS=60

# add startup script
COPY run-zookeeper.sh /tmp/

RUN mkdir -p $ZOOKEEPER_HOME $ZOOKEEPER_DATA_DIR $ZOOKEEPER_DATA_LOG_DIR && \
    curl -Ss http://apache-mirror.rbc.ru/pub/apache/zookeeper/zookeeper-3.4.10/$ZOOKEEPER_DISTRIB | tar xfz - -C $ZOOKEEPER_HOME --strip-components=1 && \
    mv /tmp/run-zookeeper.sh $ZOOKEEPER_HOME && \
    chmod +x $ZOOKEEPER_HOME/run-zookeeper.sh

CMD $ZOOKEEPER_HOME/run-zookeeper.sh
