package io.lesson04.eventsourcing.api;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ApiApp {
  public static void main(String[] args) {
    try {
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
      PersonApi personApi = context.getBean(PersonApi.class);

      personApi.savePerson(1L, "Ivan", "Baraban", "Lol");
      personApi.savePerson(1L, "Ivan", "Baraban", "Edit");
      personApi.savePerson(2L, "Sergey", "Sax", "Tenor");
      personApi.savePerson(3L, "Tamara", "Tuba", "Big");
      personApi.deletePerson(1L);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
