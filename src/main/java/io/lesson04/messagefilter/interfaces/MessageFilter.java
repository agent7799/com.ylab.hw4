package io.lesson04.messagefilter.interfaces;

import java.sql.SQLException;

public interface MessageFilter {
    String filter(String message) throws SQLException;
}
