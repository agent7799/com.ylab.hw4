package io.lesson04.messagefilter;

import com.rabbitmq.client.*;
import io.lesson04.messagefilter.interfaces.CensoredMessageSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
@Component
public class CensoredMessageSenderImpl implements CensoredMessageSender {
    private final ConnectionFactory factory;

    public CensoredMessageSenderImpl(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void sendMessage(String message) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()){
            channel.queueDeclare("output", false, false, false, null);
            channel.basicPublish("", "output", null, message.getBytes());
        }
    }
}
