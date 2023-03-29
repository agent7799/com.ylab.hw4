package lesson04.eventsourcing.db;

import lesson04.DbUtil;
import lesson04.RabbitMQUtil;
import com.rabbitmq.client.ConnectionFactory;

import java.sql.SQLException;
import javax.sql.DataSource;


public class DbApp {
  public static void main(String[] args) {
    try {
      DataSource dataSource = initDb();
      ConnectionFactory connectionFactory = initMQ();
      MessageHandler handler = new MessageHandler(connectionFactory, dataSource);
      handler.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // тут пишем создание и запуск приложения работы с БД
  }
  
  private static ConnectionFactory initMQ() throws Exception {
    return RabbitMQUtil.buildConnectionFactory();
  }
  
  private static DataSource initDb() throws SQLException {
    String ddl = "" 
                     + "drop table if exists person;" 
                     + "create table if not exists person (\n"
                     + "person_id bigint primary key,\n"
                     + "first_name varchar,\n"
                     + "last_name varchar,\n"
                     + "middle_name varchar\n"
                     + ")";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(ddl, dataSource);
    return dataSource;
  }
}
