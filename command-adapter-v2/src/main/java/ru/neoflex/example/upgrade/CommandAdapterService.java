package ru.neoflex.example.upgrade;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.example.upgrade.v2.dto.Ticket;

import java.util.UUID;

@RestController
public class CommandAdapterService {

    @Autowired
    KafkaProducer<String, Ticket> producer;

    @Value(value = "${kafka.topic}")
    private String topic;

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, path = "/create")
    public Ticket create(@RequestBody Ticket ticket) {
        return sendTicketCommand(ticket, "create");
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, path = "/update")
    public Ticket update(@RequestBody Ticket ticket) {
        return sendTicketCommand(ticket, "update");
    }

    private Ticket sendTicketCommand(Ticket ticket, String command) {
        if (ticket.getUuid() == null) {
            ticket.setUuid(UUID.randomUUID().toString());
        }
        ticket.setDate(System.currentTimeMillis());
        ticket.setCommand(command);
        producer.send(new ProducerRecord<>(topic, ticket.getUuid(), ticket));
        return ticket;
    }

}
