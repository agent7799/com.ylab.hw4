package io.lesson04.filesort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public interface FileSorter {
  File sort(File data) throws SQLException, IOException;
}
