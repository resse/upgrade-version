package ru.neoflex.example.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import ru.neoflex.example.upgrade.v1.dto.Ticket;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
            String subjectUuid = modifySubject(connection, value.getSubject());
            if ("create".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tickets (uid, c_date, subject, description) VALUES (?, ?, ?, ?)");
                int index = 1;
                pstmt.setString(index++, value.getUuid());
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, subjectUuid);
                pstmt.setString(index++, value.getDescription());
                pstmt.execute();
                pstmt.close();
            } else if ("update".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("UPDATE tickets set c_date = ?, subject = ?, description = ? WHERE uid = ?");
                int index = 1;
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, subjectUuid);
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

    private String modifySubject(Connection connection, String subject) throws SQLException {
        String subjectUuid = UUID.randomUUID().toString();
        PreparedStatement pstmt = connection.prepareStatement("SELECT uid FROM subjects WHERE subject = ?");
        pstmt.setString(1, subject);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            subjectUuid = resultSet.getString("uid");
        } else {
            createSubject(connection, subject, subjectUuid);
        }
        resultSet.close();
        pstmt.close();
        return subjectUuid;
    }

    private void createSubject(Connection connection, String subject, String subjectUuid) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO subjects (uid, subject) VALUES (?, ?)");
        int index = 1;
        pstmt.setString(index++, subjectUuid);
        pstmt.setString(index++, subject);
        pstmt.execute();
        pstmt.close();
    }

    @Override
    public void punctuate(long timestamp) {
        // Ignore deprecated
    }

    @Override
    public void close() {
    }
}
