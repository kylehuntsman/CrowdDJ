package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.vlc.*;
import org.eclipse.jetty.server.Server;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.github.funnygopher.crowddj.jooq.Tables.PLAYLIST;

public class CrowdDJ {

    // The JavaFX controller for the window
    private CrowdDJController controller;

    private VLC vlc;
    private int port;
    private String password;

    private Server server;

    private String vlcPath;
    private boolean validVLCPath;

    private PropertyManager properties;
	private DatabaseManager database;
	private PlaylistManager playlist;

    public CrowdDJ() {
        this(8081, "root");
    }

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;
        this.controller = new CrowdDJController(this);

		// Sets up the properties file
		properties = new PropertyManager("config.properties");

		// Sets up the database
		String dbUsername = properties.getStringProperty(Property.DB_USERNAME);
		String dbPassword = properties.getStringProperty(Property.DB_PASSWORD);
		database = new DatabaseManager("jdbc:h2:~/.CrowdDJ/db/crowddj", dbUsername, dbPassword);

		// Sets up the playlist
		playlist = new PlaylistManager();

        // Sets up the VLC connection
		int vlcPort = properties.getIntProperty(Property.VLC_PORT);
		String vlcPass = properties.getStringProperty(Property.VLC_PASSWORD);
        vlc = new VLC(vlcPort, vlcPass);

        // Takes each song saved in the PLAYLIST table and adds it to the playlist
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn, SQLDialect.H2);
            Result<Record> results = db.select().from(PLAYLIST).fetch();

            for (Record result : results) {
                String filepath = result.getValue(PLAYLIST.FILEPATH);
                File file = new File(filepath);
				playlist.add(file);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean vlcStarted = vlc.start(vlcPath);
        if(vlcStarted) {
            // Adds the songs to VLC
            for (Song song : playlist.getItems()) {
                vlc.getController().add(song.getFile());
            }
        }

        // Starts the web server, telling it to look for playback commands
        try {
            server = new Server(port);
            server.setHandler(new PlaybackHandler(this));
            server.start();
        } catch (Exception e) {
            System.err.append("Could not start server. Something is wrong...\n");
            e.printStackTrace();
        }
    }

    public CrowdDJController getController() {
        return controller;
    }

	public PropertyManager getProperties() {
		return properties;
	}

	public DatabaseManager getDatabase() {
		return database;
	}

	public PlaylistManager getPlaylist() {
		return playlist;
	}

    public VLC getVLC() {
        return vlc;
    }

    public VLCStatus getStatus() {
        return vlc.getStatus();
    }

    public boolean isVLCConnected() {
        return vlc.getStatus().isConnected();
    }

    public boolean hasValidVLCPath() {
        return validVLCPath;
    }

    public void setVLCPath(String vlcPath) {
        File vlcExe = new File(vlcPath);
        if(vlcExe.isFile()) {
            validVLCPath = true;
            this.vlcPath = vlcPath;
        } else {
            validVLCPath = false;
        }
    }

    public boolean startVLC() {
        if(validVLCPath) {
            return vlc.start(vlcPath);
        }

        return false;
    }

    public void stopServer() {
        if(server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
