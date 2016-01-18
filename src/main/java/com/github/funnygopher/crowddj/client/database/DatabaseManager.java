package com.github.funnygopher.crowddj.client.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {
    Connection getConnection() throws SQLException;
}
