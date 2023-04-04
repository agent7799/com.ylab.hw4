package io.lesson04.messagefilter.interfaces;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface FileUploaderToDb {

    void fillTable() throws SQLException, IOException;
}
