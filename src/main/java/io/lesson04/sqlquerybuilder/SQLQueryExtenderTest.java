package io.lesson04.sqlquerybuilder;

import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SQLQueryExtenderTest {
  public static void main(String[] args) throws SQLException {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
    applicationContext.start();
    SQLQueryBuilder queryBuilder = applicationContext.getBean(SQLQueryBuilder.class);
    List<String> tables = queryBuilder.getTables(null); // 'null' to get all tables or 'table_name' to get exact one.
    // вот так сгенерируем запросы для всех таблиц что есть в БД
    for (String tableName : tables) {
      System.out.println(queryBuilder.queryForTable(tableName));
    }
    applicationContext.close();
  }
}
