package com.github.funnygopher.crowddj.database;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDatabase implements Database {

	private String url;
	private String username;
	private String password;

    private String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS PLAYLIST (" +
            "ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
            "FILEPATH VARCHAR(255) NOT NULL" +
            ");";

	public SimpleDatabase(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;

		setupDatabase();
	}

	private void setupDatabase() {
		try (Connection conn = getConnection()) {
			DSLContext db = DSL.using(conn);

			// Table creation scripts go here
			db.execute(CREATE_STATEMENT);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
