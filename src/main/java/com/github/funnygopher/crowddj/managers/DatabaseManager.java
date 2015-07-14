package com.github.funnygopher.crowddj.managers;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

	private static final String DRIVER = "org.h2.Driver";
	public static final String USERNAME = "sa";
	public static final String PASSWORD = "";

	private String url;
	private String username;
	private String password;

	public static final String CREATE_PLAYLIST_TABLE =
			"CREATE TABLE IF NOT EXISTS PLAYLIST (" +
					"ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
					"FILEPATH VARCHAR(255) NOT NULL" +
			");";

	public DatabaseManager(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;

		setupDatabase();
	}

	public void setupDatabase() {
		try (Connection conn = getConnection()) {
			DSLContext db = DSL.using(conn);

			// Table creation scripts go here
			db.execute(CREATE_PLAYLIST_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute(String sql) {
		try (Connection conn = getConnection()) {
			DSLContext db = DSL.using(conn);
			db.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
