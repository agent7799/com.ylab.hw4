package io.lesson04.messagefilter;

import io.lesson04.messagefilter.interfaces.MessageFilter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class MessageFilterImpl implements MessageFilter {
    private final DataSource dataSource;

    public MessageFilterImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String filter(String message) throws SQLException {
        if (message.isBlank()) {
            return message;
        }
        String command = getSqlCommand(message);
        if (command == null) {
            return message;
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(command)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                String censured = censuringWord(word);
                message = message.replaceFirst(word, censured);
            }
        }
        return message;
    }

    private String censuringWord(String word) {
        return word.charAt(0) +
                "*".repeat(Math.max(0, word.length() - 2)) +
                word.charAt(word.length() - 1);
    }

    private String getSqlCommand(String message) {
        String[] words = splitMessage(message);
        if (words.length == 0) {
            return null;
        }
        String tableName = "swear_word";
        StringBuilder sqlCommand = new StringBuilder("select * from " + tableName + " where upper(word) in('");
        for (String word : words) {
            sqlCommand.append(word.toUpperCase()).append("','");
        }
        sqlCommand.replace(sqlCommand.length() - 2, sqlCommand.length(), ");");
        System.out.println(sqlCommand);
        return sqlCommand.toString();
    }

    private String[] splitMessage(String message) {
        return message.trim().split("[^\\wА-Яа-я]+");
    }
}
