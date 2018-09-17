package ru.neoflex.example.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import ru.neoflex.example.upgrade.v2.dto.Subject;
import ru.neoflex.example.upgrade.v2.dto.Ticket;

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
            modifySubject(connection, value.getSubject());
            if ("create".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tickets (uid, c_date, subject, description) VALUES (?, ?, ?, ?)");
                int index = 1;
                pstmt.setString(index++, value.getUuid());
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, value.getSubject().getUuid());
                pstmt.setString(index++, value.getDescription());
                pstmt.execute();
                pstmt.close();
            } else if ("update".equals(value.getCommand())) {
                PreparedStatement pstmt = connection.prepareStatement("UPDATE tickets set c_date = ?, subject = ?, description = ? WHERE uid = ?");
                int index = 1;
                pstmt.setLong(index++, value.getDate());
                pstmt.setString(index++, value.getSubject().getUuid());
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

    private void modifySubject(Connection connection, Subject subject) throws SQLException {
        if (subject.getUuid() == null){
            PreparedStatement pstmt = connection.prepareStatement("SELECT uid FROM subjects WHERE subject = ?");
            pstmt.setString(1, subject.getSubject());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String subjectUuid = resultSet.getString("uid");
                subject.setUuid(subjectUuid);
            } else {
                subject.setUuid(UUID.randomUUID().toString());
                createSubject(connection, subject);
            }
        }
        PreparedStatement stmt = connection.prepareStatement("SELECT subject FROM subjects WHERE uid = ?");
        stmt.setString(1, subject.getUuid());
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            String subjectValue = resultSet.getString("subject");
            if (subjectValue == null || !subjectValue.equals(subject.getSubject())) {
                updateSubject(connection, subject);
            }
        } else {
            createSubject(connection, subject);
        }
    }

    private void createSubject(Connection connection, Subject subject) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO subjects (uid, subject) VALUES (?, ?)");
        int index = 1;
        pstmt.setString(index++, subject.getUuid());
        pstmt.setString(index++, subject.getSubject());
        pstmt.execute();
        pstmt.close();
    }

    private void updateSubject(Connection connection, Subject subject) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("UPDATE subjects set subject = ? WHERE uid = ?");
        int index = 1;
        pstmt.setString(index++, subject.getSubject());
        pstmt.setString(index++, subject.getUuid());
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
