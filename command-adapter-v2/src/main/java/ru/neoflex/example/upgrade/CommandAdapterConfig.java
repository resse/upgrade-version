package ru.neoflex.example.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.neoflex.example.upgrade.v2.dto.Ticket;
import ru.neoflex.example.upgrade.v2.serialization.TicketSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Slf4j
@Configuration
public class CommandAdapterConfig {

    private KafkaProducer<String, Ticket> producer;

    @Value(value = "${kafka.hosts}")
    String kafkaHosts;

    @PostConstruct
    public void init(){
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHosts);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        props.put("value.serializer", TicketSerializer.class);
        producer = new KafkaProducer<>(props);
        log.info("Producer created");
    }

    @PreDestroy
    public void close(){
        producer.close();
        log.info("Producer closed");
    }

    @Bean
    public KafkaProducer<String, Ticket> producer(){
        return producer;
    }
}
