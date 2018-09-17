FROM openjdk:8-jdk

ENV KAFKA_DISTRIB=kafka_2.12-1.0.0.tgz \
    KAFKA_HOME=/opt/kafka

COPY run-kafka.sh /tmp/

RUN mkdir -p $KAFKA_HOME && \
    curl -Ss http://apache-mirror.rbc.ru/pub/apache/kafka/1.0.0/$KAFKA_DISTRIB | tar xfz - -C $KAFKA_HOME --strip-components=1 && \
    mv /tmp/run-kafka.sh $KAFKA_HOME/ && \
    chmod u+x $KAFKA_HOME/run-kafka.sh

CMD $KAFKA_HOME/run-kafka.sh
