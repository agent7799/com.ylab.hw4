package lesson04.eventsourcing.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import lesson04.eventsourcing.Person;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;
public class MessageHandler {
    private ConnectionFactory connectionFactory;
    private DataSource dataSource;
    private String queueName = "queue";

    public MessageHandler(ConnectionFactory connectionFactory, DataSource dataSource) {
        this.connectionFactory = connectionFactory;
        this.dataSource = dataSource;
    }

    public void execute() throws IOException, TimeoutException, SQLException {
        while ((!Thread.currentThread().isInterrupted())) {
            readMessage(getMessage());
        }

    }

    private String[] getMessage() throws IOException, TimeoutException {
        String[] received = new String[2];
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            GetResponse message = channel.basicGet(queueName, true);
            if (message != null) {
                received = message.toString().split("=%=");
            }
        }
        return received;
    }

    private void readMessage(String[] msg) throws SQLException {
        switch (msg[0]) {
            case "delete" -> delete(msg[1]);
            case "save" -> save(msg[1]);
        }
    }

    private void delete(String s) throws SQLException {
        try {
            Long id = Long.parseLong(s);
            if (containsPerson(new Person(id, null, null, null))) {
                System.err.println("No person with id = " + id);
                return;
            }
            String sqlMessage = "delete from person where person_id = ?; ";
            try (java.sql.Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage)) {
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
            }
        } catch (NumberFormatException e) {
            System.out.println("Wrong id format");
        }
    }
    private void save(String s) throws SQLException {
        Person p = getPerson(s);
        String sqlMessage = "insert into person (first_name, last_name, middle_name) values (?,?,?);";
        if (containsPerson(p)) {
            sqlMessage = "update person (first_name, last_name, middle_name) values (?,?,?) where id = " + p.getId() + ";";
        }
        try (java.sql.Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage)){
            preparedStatement.setString(1, p.getName());
            preparedStatement.setString(2, p.getLastName());
            preparedStatement.setString(3, p.getMiddleName());
            preparedStatement.execute();
        }
    }


    private Person getPerson(String s) {
        return new ObjectMapper().convertValue(s, Person.class);
    }
    private boolean containsPerson(Person p) throws SQLException {
        String sqlMessage = "select from person (person_id) values (?);";
        try (java.sql.Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage)){
            preparedStatement.setLong(1, p.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}
