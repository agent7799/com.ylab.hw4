package lesson04.filesort;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class FileSortImpl implements FileSorter {
  private DataSource dataSource;

  public FileSortImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public File sort(File data) throws SQLException, IOException {
    String insertCommand = "insert into numbers (val) values (?);";
    String getOrderedCommand = "select val from numbers order by val desc;";
    File result = new File("src/result.txt");
    try (BufferedReader readerFromSource = new BufferedReader(new FileReader(data));
         PrintWriter printWriter = new PrintWriter(result);
         Connection uploadConnection = dataSource.getConnection();
         Connection downloadConnection = dataSource.getConnection();
         PreparedStatement prUpload = uploadConnection.prepareStatement(insertCommand);
         PreparedStatement prDownload = downloadConnection.prepareStatement(getOrderedCommand)) {
      uploadConnection.setAutoCommit(false);
      while (readerFromSource.ready()) {
        prUpload.setLong(1, Long.parseLong(readerFromSource.readLine()));
        prUpload.addBatch();
      }
      prUpload.executeBatch();
      uploadConnection.commit();
      ResultSet resultSet = prDownload.executeQuery();
      while (resultSet.next()) {
        printWriter.println(resultSet.getLong(1));
      }
    }
    return result;
  }
}
