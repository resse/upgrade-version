package ru.neoflex.example.upgrade.v1.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import ru.neoflex.example.upgrade.v1.dto.Ticket;

import java.util.Map;

public class TicketSerde implements Serde<Ticket> {
    final private Serializer<Ticket> serializer = new TicketSerializer();
    final private Deserializer<Ticket> deserializer = new TicketDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<Ticket> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Ticket> deserializer() {
        return deserializer;
    }
}
