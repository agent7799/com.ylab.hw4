package lesson04.persistentmap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Класс, методы которого надо реализовать
 */
public class PersistentMapImpl implements PersistentMap {

    private DataSource dataSource;

    private String name;

    public PersistentMapImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void init(String name) {
        this.name = name;
    }

    @Override
    public boolean containsKey(String key) throws SQLException {
        initCheck();
        String containsKeyCheck = "select KEY from persistent_map where KEY = ? and map_name = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(containsKeyCheck)) {
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    @Override
    public List<String> getKeys() throws SQLException {
        initCheck();
        String commandToGetKeys = "select key from persistent_map where map_name = ?;";
        List<String> keyList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(commandToGetKeys)) {
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                keyList.add(rs.getString("KEY"));
            }
        }
        return keyList;
    }

    @Override
    public String get(String key) throws SQLException {
        initCheck();
        String commandToGetValueByKey = "select value from persistent_map where map_name = ? and KEY = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(commandToGetValueByKey)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, key);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getString("value");
        }
    }

    @Override
    public void remove(String key) throws SQLException {
        initCheck();
        String commandToRemove = "delete from persistent_map where map_name = ? and KEY = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(commandToRemove)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, key);
            preparedStatement.execute();
        }
    }

    @Override
    public void put(String key, String value) throws SQLException {
        initCheck();
        inputCheck(key, value);
        if (this.containsKey(key)) {
            this.remove(key);
        }
        String commandToPutKeyValue = "insert into persistent_map (map_name, KEY, value) values (?, ?, ?);";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(commandToPutKeyValue)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, value);
            preparedStatement.executeUpdate();
        }

    }

    @Override
    public void clear() throws SQLException {
        String commandToClear = "delete from persistent_map where map_name = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(commandToClear)) {
            preparedStatement.setString(1, name);
            preparedStatement.execute();
        }
    }

    private void initCheck() {
        if (name.isBlank()) {
            throw new RuntimeException("This map has not been initialized!");
        }
    }

    private void inputCheck(String... s) {
        for (String s1 : s) {
            if (s1.isEmpty()) {
                throw new RuntimeException("Field can't be empty!");
            }
        }
    }
}
