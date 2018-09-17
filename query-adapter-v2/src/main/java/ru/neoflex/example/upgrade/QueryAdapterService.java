package ru.neoflex.example.upgrade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.example.upgrade.v2.dto.Subject;
import ru.neoflex.example.upgrade.v2.dto.Ticket;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class QueryAdapterService {

    @Autowired
    DataSource dataSource;

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, path = "/tickets")
    public List<Ticket> tickets() throws Exception {
        Connection connection = null;
        try {
            List<Ticket> tickets = new ArrayList<>();
            connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT t.uid uid, t.c_date c_date" +
                    ", s.uid subject_uid, s.subject subject, t.description description " +
                    "FROM tickets t, subjects s where t.subject = s.uid ORDER BY c_date DESC ");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next() && tickets.size() < 20) {
                Ticket ticket = new Ticket();
                ticket.setUuid(resultSet.getString("uid"));
                ticket.setDate(resultSet.getLong("c_date"));
                Subject subject = new Subject();
                subject.setUuid(resultSet.getString("subject_uid"));
                subject.setSubject(resultSet.getString("subject"));
                ticket.setSubject(subject);
                ticket.setDescription(resultSet.getString("description"));
                tickets.add(ticket);
            }
            return tickets;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, path = "/subjects")
    public List<Subject> subjects() throws Exception {
        Connection connection = null;
        try {
            List<Subject> subjects = new ArrayList<>();
            connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT uid, subject " +
                    "FROM subjects ORDER BY subject");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Subject subject = new Subject();
                subject.setUuid(resultSet.getString("uid"));
                subject.setSubject(resultSet.getString("subject"));
                subjects.add(subject);
            }
            return subjects;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }


}
