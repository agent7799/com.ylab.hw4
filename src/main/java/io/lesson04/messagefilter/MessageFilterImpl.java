package io.lesson04.messagefilter;

import io.lesson04.messagefilter.interfaces.MessageFilter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@Component
public class MessageFilterImpl implements MessageFilter {
    private final DataSource dataSource;

    public MessageFilterImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String filter(String message) throws SQLException {
        System.out.println("raw: " + message);
        if (!message.isBlank()) {
            String sqlCommand = "select word from swear_word where word ilike any(?) order by length(word) desc;";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand)) {
                Array array = connection.createArrayOf("varchar", splitMessage(message));
                preparedStatement.setArray(1, array);
                ResultSet resultSet = preparedStatement.executeQuery();
                Set<String> obsceneWords = new HashSet<>();
                if (resultSet.next()) {
                    do {
                        obsceneWords.add(resultSet.getString("word").toLowerCase());
                    } while (resultSet.next());
                    message = applyCensure(message, obsceneWords);
                }
            }
        }
        System.out.println("censured: " + message);
        return message;
    }

    private String applyCensure(String message, Set<String> obsceneWords) {
        StringBuilder messageBuilder = new StringBuilder(message);
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0, wordStart = 0; i < message.length(); i++) {
            if (String.valueOf(message.charAt(i)).matches("[\\wА-Яа-я]")) {
                currentWord.append(message.charAt(i));
                wordStart = wordStart != 0 ? wordStart : i;
            } else if (currentWord.length() > 0) {
                if (obsceneWords.contains(currentWord.toString().toLowerCase())) {
                    messageBuilder.replace(++wordStart, i - 1, "*".repeat(currentWord.length() - 2));
                }
                wordStart = 0;
                currentWord.setLength(0);
            }
        }
        return messageBuilder.toString();
    }

    private String[] splitMessage(String message) {
        return message.trim().split("[^\\wА-Яа-я]+");
    }

}
