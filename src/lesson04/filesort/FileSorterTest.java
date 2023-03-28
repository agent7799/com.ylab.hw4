package lesson04.filesort;

import lesson04.DbUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;


public class FileSorterTest {
  public static void main(String[] args) throws SQLException, IOException {
    DataSource dataSource = initDb();
    File data = new File("src/data.txt");
    FileSorter fileSorter = new FileSortImpl(dataSource);
    File res = fileSorter.sort(data);
  }
  
  public static DataSource initDb() throws SQLException {
    String createSortTable = "" 
                                 + "drop table if exists numbers;" 
                                 + "CREATE TABLE if not exists numbers (\n"
                                 + "\tval bigint\n"
                                 + ");";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(createSortTable, dataSource);
    return dataSource;
  }
}
