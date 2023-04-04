package io.lesson04.messagefilter;

import io.lesson04.messagefilter.interfaces.FileUploaderToDb;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FileUploaderToDbImpl implements FileUploaderToDb {
    private final DataSource dataSource;
    private final File file;

    public FileUploaderToDbImpl(DataSource dataSource, File file) {
        this.dataSource = dataSource;
        this.file = file;
    }

    private boolean doesTableExist() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String tableName = "swear_word";
            ResultSet tables = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"});
            return tables.next() && tableName.equals(tables.getString("TABLE_NAME"));
        }
    }

    @PostConstruct
    @Override
    public void fillTable() throws SQLException, IOException {
        String sqlOnLaunchCommand = "create table swear_word (word varchar(20));";
        if (doesTableExist()) {
            sqlOnLaunchCommand = "truncate table swear_word;";
        }
        String sqlFillTableCommand = "insert into swear_word (word) values (?);";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
             Connection connection = dataSource.getConnection();
             PreparedStatement creatorStatement = connection.prepareStatement(sqlOnLaunchCommand);
             PreparedStatement fillerStatement = connection.prepareStatement(sqlFillTableCommand)) {
            creatorStatement.executeUpdate();
            connection.setAutoCommit(false);
            while (bufferedReader.ready()) {
                fillerStatement.setString(1, bufferedReader.readLine());
                fillerStatement.addBatch();
            }
            fillerStatement.executeBatch();
            connection.commit();
        }
    }
}

