package ru.neoflex.example.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import ru.neoflex.example.upgrade.v1.dto.Ticket;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class TicketCommandProcessor implements Processor<String, Ticket> {
    private final DataSource dataSource;
    private ProcessorContext context;

    public TicketCommandProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(String key, Ticket value) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if ("create".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tickets (uid, c_date, subject, description) VALUES (?, ?, ?, ?)");
                int index = 1;
                pstmt.setString(index++, value.getUuid());
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, value.getSubject());
                pstmt.setString(index++, value.getDescription());
                pstmt.execute();
                pstmt.close();
            } else if ("update".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("UPDATE tickets set c_date = ?, subject = ?, description = ? WHERE uid = ?");
                int index = 1;
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, value.getSubject());
                pstmt.setString(index++, value.getDescription());
                pstmt.setString(index++, value.getUuid());
                pstmt.execute();
                pstmt.close();
            }
            context.forward(key, value);
        } catch (Throwable e) {
            log.error("Error process data", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public void punctuate(long timestamp) {
        // Ignore deprecated
    }

    @Override
    public void close() {
    }
}
