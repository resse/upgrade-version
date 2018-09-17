package ru.neoflex.example.upgrade.v2.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.neoflex.example.upgrade.v2.dto.Ticket;

import java.util.Map;

public class TicketDeserializer implements Deserializer<Ticket> {
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor needed by Kafka
     */
    public TicketDeserializer() {
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }

    @Override
    public Ticket deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        Ticket data;
        try {
            data = objectMapper.readValue(bytes, Ticket.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return data;
    }

    @Override
    public void close() {

    }
}
