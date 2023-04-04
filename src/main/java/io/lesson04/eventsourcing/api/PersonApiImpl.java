package io.lesson04.eventsourcing.api;


import com.rabbitmq.client.*;
import io.lesson04.eventsourcing.MessageCarrier;
import io.lesson04.eventsourcing.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.sql.DataSource;

@Component
public class PersonApiImpl implements PersonApi {
    private String exchangeName = "exc";
    private String queueName = "queue";
    private ConnectionFactory factory;
    private DataSource dataSource;

    public PersonApiImpl(ConnectionFactory factory, DataSource dataSource) {
        this.factory = factory;
        this.dataSource = dataSource;
    }

    @Override
    public void deletePerson(Long personId) throws IOException, TimeoutException {
        MessageCarrier carrier = new MessageCarrier("delete", new Person(personId, null, null, null));
        ObjectMapper objectMapper = new ObjectMapper();
        String msg = objectMapper.writeValueAsString(carrier);
        sendMessage(msg);
    }

    @Override
    public void savePerson(Long personId, String firstName, String lastName, String middleName) throws IOException, TimeoutException {
        Person p = new Person(personId, firstName, lastName, middleName);
        ObjectMapper objectMapper = new ObjectMapper();
        MessageCarrier carrier = new MessageCarrier("save", p);
        String msg = objectMapper.writeValueAsString(carrier);
        System.out.println(msg);
        sendMessage(msg);
    }

    @Override
    public Person findPerson(Long personId) throws SQLException {
        String sqlMessage = "select * from person where person_id = ?;";
        try (java.sql.Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage)) {
            preparedStatement.setLong(1, personId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Person(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                );
            } else return null;
        }
    }

    @Override
    public List<Person> findAll() throws SQLException {
        String sqlMessage = "select * from person;";
        List<Person> list = new ArrayList<>();
        try (java.sql.Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(new Person(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
            return list;
        }
    }

    private void sendMessage(String msg) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            AMQP.Queue.DeclareOk queue = channel.queueDeclare(queueName, true, false, false, null);
            channel.basicPublish("", queue.getQueue(), null, msg.getBytes());
            System.out.println("message sent!");
        }
    }
}

