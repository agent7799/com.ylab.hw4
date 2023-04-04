package io.lesson04.persistentmap;

import io.lesson04.DbUtil;

import java.sql.SQLException;
import javax.sql.DataSource;


public class PersistenceMapTest {
  public static void main(String[] args) throws SQLException {
    DataSource dataSource = initDb();
    PersistentMap persistentMap = new PersistentMapImpl(dataSource);

    persistentMap.init("persistentMap");
    persistentMap.put("test", "val");
    System.out.println(persistentMap.containsKey("test"));
    persistentMap.put("test", "val1");
    persistentMap.put("key", "zzzzz");
    System.out.println(persistentMap.get("test"));
    System.out.println(persistentMap.getKeys());
    persistentMap.remove("key");
    System.out.println(persistentMap.getKeys());
    persistentMap.clear();
    System.out.println(persistentMap.getKeys());
  }
  
  public static DataSource initDb() throws SQLException {
    String createMapTable = "" 
                                + "drop table if exists persistent_map; " 
                                + "CREATE TABLE if not exists persistent_map (\n"
                                + "   map_name varchar,\n"
                                + "   KEY varchar,\n"
                                + "   value varchar\n"
                                + ");";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(createMapTable, dataSource);
    return dataSource;
  }
}
