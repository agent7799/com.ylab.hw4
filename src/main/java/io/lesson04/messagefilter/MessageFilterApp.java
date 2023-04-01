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
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();
        FileUploaderToDb uploader = new FileUploaderToDbImpl(applicationContext.getBean(DataSource.class),
                applicationContext.getBean(File.class));
        RawMessageReceiver receiver = new RawMessageReceiverImpl(
                applicationContext.getBean(ConnectionFactory.class));
        MessageFilter filter = new MessageFilterImpl(applicationContext.getBean(DataSource.class));
        CensoredMessageSender sender = new CensoredMessageSenderImpl(
                applicationContext.getBean(ConnectionFactory.class));

        while (!Thread.currentThread().isInterrupted()) {
            try {
                uploader.fillTable();
                String message;
                message = receiver.receiveMessage();
                message = filter.filter(message);
                sender.send(message);

            } catch (NullPointerException e) {
                int period = 20;
                System.err.printf("No messages! Next check in %d seconds\n", period);
                Thread.sleep(Duration.of(period, ChronoUnit.SECONDS));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        applicationContext.close();

    }
}

