package io.lesson04.messagefilter.interfaces;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RawMessageReceiver {
    String receiveMessage() throws IOException, TimeoutException;
}
