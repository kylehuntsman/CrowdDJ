package com.github.funnygopher.crowddj.database;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseManager implements DatabaseManager {

    private String mUrl;
    private String mUsername;
    private String mPassword;

    private final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS PLAYLIST (" +
            "ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
            "FILEPATH VARCHAR(255) NOT NULL" +
            ");";

    public H2DatabaseManager(String url, String username, String password) {
        this.mUrl = url;
        this.mUsername = username;
        this.mPassword = password;

        create();
    }

    private void create() {
        try (Connection conn = getConnection()) {
            DSLContext db = DSL.using(conn);

            // Table creation scripts go here
            db.execute(CREATE_STATEMENT);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(mUrl, mUsername, mPassword);
    }
}
