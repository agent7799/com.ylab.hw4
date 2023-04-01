package io.lesson04.movie;

import java.io.*;
import java.sql.SQLException;

public interface MovieLoader {
  void loadData(File file) throws IOException, SQLException;

}
