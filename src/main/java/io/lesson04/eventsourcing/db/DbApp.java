package io.lesson04.eventsourcing.db;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class DbApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();
        MessageHandler app = applicationContext.getBean(MessageHandler.class);

        try {
            app.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        applicationContext.close();

    }
}
