package com.github.funnygopher.crowddj.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	public static final String DRIVER = "org.h2.Driver";
	public static final String URL = "jdbc:h2:~/.CrowdDJ/db/crowddj";
	public static final String USERNAME = "sa";
	public static final String PASSWORD = "";

    public static final String CREATE_PLAYLIST_TABLE =
            "CREATE TABLE IF NOT EXISTS PLAYLIST (" +
                "ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                "FILEPATH VARCHAR(255) NOT NULL" +
            ");";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
}
