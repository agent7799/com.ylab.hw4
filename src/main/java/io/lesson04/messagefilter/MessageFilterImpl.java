package io.lesson04.messagefilter;

import io.lesson04.messagefilter.interfaces.MessageFilter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MessageFilterImpl implements MessageFilter {
    private final DataSource dataSource;

    public MessageFilterImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String filter(String message) throws SQLException {
        String censoredNoCase = message;
        if (!message.isBlank()) {
            String sqlCommand = "select word from swear_word where word ilike any(?);";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand)) {
                Array array = connection.createArrayOf("varchar", splitMessage(message));
                preparedStatement.setArray(1, array);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String word = resultSet.getString("word");
                    String censured = censuringWord(word);
                    censoredNoCase = censoredNoCase.replaceAll("(?iu)" + word, censured);
                }
            }
        }
        return applyCase(message, censoredNoCase);
    }

    private String applyCase(String message, String censured) {
        StringBuilder temp = new StringBuilder(message);
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) != censured.charAt(i) && censured.charAt(i) == '*') {
                temp.setCharAt(i, '*');
            }
        }
        return temp.toString();
    }

    private String censuringWord(String word) {
        return word.charAt(0) +
                "*".repeat(word.length() - 2) +
                word.charAt(word.length() - 1);
    }

    private String[] splitMessage(String message) {
        return message.trim().split("[^\\wА-Яа-я]+");
    }

}
