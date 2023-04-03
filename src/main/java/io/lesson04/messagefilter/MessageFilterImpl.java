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
        System.out.println("raw: " + message);
        String filteredMessage = message;
        if (!message.isBlank()) {
            String sqlCommand = "select word from swear_word where word ilike any(?) order by length(word);";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand)) {
                Array array = connection.createArrayOf("varchar", splitMessage(message));
                preparedStatement.setArray(1, array);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String word = resultSet.getString("word");
                    filteredMessage = filteredMessage.replaceAll("(?iu)(?<![\\p{L}\\d])" + word + "(?![\\p{L}\\d])", censorWord(word));
                }
            }
        }
        message = message.equals(filteredMessage) ? message : mergeCase(message, filteredMessage);
        System.out.println("censured: " + message);
        return message;
    }

    private String mergeCase(String raw, String censured) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) != censured.charAt(i) && censured.charAt(i) == '*') {
                result.append(censured.charAt(i));
            } else {
                result.append(raw.charAt(i));
            }
        }
        return result.toString();
    }

    private String censorWord(String word) {
        return word.length() == 2 ? "**" : word.charAt(0) + "*".repeat(word.length() - 2) + word.charAt(word.length() - 1);
    }

    private String[] splitMessage(String message) {
        return message.trim().split("[^\\wА-Яа-яЁё]+");
    }

}
