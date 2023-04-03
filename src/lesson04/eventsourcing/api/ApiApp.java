package lesson04.eventsourcing.api;


import lesson04.DbUtil;
import lesson04.RabbitMQUtil;
import com.rabbitmq.client.ConnectionFactory;

import javax.sql.DataSource;

public class ApiApp {
  public static void main(String[] args) {
    try {
      ConnectionFactory connectionFactory = initMQ();
      DataSource dataSource = DbUtil.buildDataSource();
      PersonApi personApi = new PersonApiImpl(connectionFactory, dataSource);

      // Тут пишем создание PersonApi, запуск и демонстрацию работы
      personApi.savePerson(1L, "Ivan", "Baraban", "Lol");
      personApi.savePerson(1L, "Ivan", "Baraban", "Edit");
      personApi.savePerson(2L, "Sergey", "Sax", "Tenor");
      personApi.savePerson(3L, "Tamara", "Tuba", "Big");
      personApi.deletePerson(1L);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static ConnectionFactory initMQ() throws Exception {
    return RabbitMQUtil.buildConnectionFactory();
  }
}
