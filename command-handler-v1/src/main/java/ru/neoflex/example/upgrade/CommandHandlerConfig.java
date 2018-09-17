package ru.neoflex.example.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.neoflex.example.upgrade.v1.serialization.TicketSerde;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
public class CommandHandlerConfig {

    private KafkaStreams streams;

    @Value(value = "${kafka.hosts}")
    String kafkaHosts;

    @Value(value = "${kafka.topic}")
    String kafkaTopic;

    @PostConstruct
    public void init() {
        Topology topology = new Topology();
        topology.addSource("Command", kafkaTopic);
        topology.addProcessor("CommandProc", () -> new TicketCommandProcessor(dataSource()), "Command");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "command-handler-v1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHosts);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, TicketSerde.class);
        streams = new KafkaStreams(topology, props);
        streams.start();
        log.info("Streams started");
    }

    @PreDestroy
    public void close() {
        streams.close();
        log.info("Streams closed");
    }

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
