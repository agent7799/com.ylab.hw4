package io.lesson04.messagefilter;

import io.lesson04.messagefilter.interfaces.CensoredMessageSender;
import io.lesson04.messagefilter.interfaces.MessageFilter;
import io.lesson04.messagefilter.interfaces.RawMessageReceiver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MessageFilterApp {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();

        RawMessageReceiver receiver = applicationContext.getBean(RawMessageReceiver.class);

        MessageFilter filter = applicationContext.getBean(MessageFilter.class);

        CensoredMessageSender sender = applicationContext.getBean(CensoredMessageSender.class);

        int sleepPeriod = 5;
        String message;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = receiver.receiveMessage();
                    message = filter.filter(message);
                    sender.sendMessage(message);
                } catch (NullPointerException e) {
                    for (int i = sleepPeriod; i > 0; i--) {
                        String msg = "No messages! Next check in " + i + " seconds";
                        System.err.printf(msg);
                        Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
                        System.err.print("\b".repeat(msg.length()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        applicationContext.close();
    }
}

