package io.lesson04.eventsourcing.db;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;


public class DbApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();
        MessageHandler app = new MessageHandler(applicationContext.getBean(ConnectionFactory.class), applicationContext.getBean(DataSource.class));

        try {
            app.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
