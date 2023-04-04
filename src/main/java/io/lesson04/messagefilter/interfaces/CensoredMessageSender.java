package io.lesson04.messagefilter.interfaces;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface CensoredMessageSender {
 void sendMessage(String message) throws IOException, TimeoutException;
}
