package com.github.funnygopher.crowddj.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

    Connection getConnection() throws SQLException;
}
