package io.lesson04.messagefilter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import io.lesson04.messagefilter.interfaces.RawMessageReceiver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
@Component
public class RawMessageReceiverImpl implements RawMessageReceiver {
    private final ConnectionFactory factory;

    public RawMessageReceiverImpl(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public String receiveMessage() throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            GetResponse response = channel.basicGet("input", true);
            return new String(response.getBody(), StandardCharsets.UTF_8);
        }
    }
}
