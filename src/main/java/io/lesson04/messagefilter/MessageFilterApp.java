package io.lesson04.messagefilter;

import com.rabbitmq.client.ConnectionFactory;
import io.lesson04.messagefilter.interfaces.CensoredMessageSender;
import io.lesson04.messagefilter.interfaces.FileUploaderToDb;
import io.lesson04.messagefilter.interfaces.MessageFilter;
import io.lesson04.messagefilter.interfaces.RawMessageReceiver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MessageFilterApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();

        FileUploaderToDb uploader = new FileUploaderToDbImpl(applicationContext.getBean(DataSource.class),
                applicationContext.getBean(File.class));

        RawMessageReceiver receiver = new RawMessageReceiverImpl(
                applicationContext.getBean(ConnectionFactory.class));

        MessageFilter filter = new MessageFilterImpl(applicationContext.getBean(DataSource.class));

        CensoredMessageSender sender = new CensoredMessageSenderImpl(
                applicationContext.getBean(ConnectionFactory.class));

        int sleepPeriod = 5;
        String message;
        try {
            uploader.fillTable();
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

