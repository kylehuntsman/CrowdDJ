package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.jetty.PlaylistHandler;
import com.github.funnygopher.crowddj.managers.DatabaseManager;
import com.github.funnygopher.crowddj.managers.PlaylistManager;
import com.github.funnygopher.crowddj.managers.PropertyManager;
import com.github.funnygopher.crowddj.managers.StatusManager;
import com.github.funnygopher.crowddj.vlc.VLC;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static com.github.funnygopher.crowddj.jooq.Tables.PLAYLIST;

public class CrowdDJ {

    private VLC vlc;
    private Server server;

    private CrowdDJController controller;
    private PropertyManager properties;
    private DatabaseManager database;
	private PlaylistManager playlist;
    private StatusManager statusManager;

    public CrowdDJ() {
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

        // Sets up the status manager for a current VLC status
        statusManager = new StatusManager(this);

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

        // Starts VLC
        boolean vlcStarted = startVLC();
        if(vlcStarted) {
            // Adds the songs to VLC
            for (Song song : playlist.getItems()) {
                vlc.getController().add(song.getFile());
            }
        }

        // Starts the web server, telling it to look for playback commands
        try {
            int port = properties.getIntProperty(Property.PORT);
            server = new Server(port);
			ContextHandler playbackContext = new ContextHandler();
			playbackContext.setContextPath("/playback");
			playbackContext.setHandler(new PlaybackHandler(this));

			ContextHandler playlistContext = new ContextHandler();
			playlistContext.setContextPath("/playlist");
			playlistContext.setHandler(new PlaylistHandler(this));

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[]{playbackContext, playlistContext});
			server.setHandler(handlers);

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

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public VLC getVLC() {
        return vlc;
    }

    public boolean isVLCConnected() {
        return vlc.getStatus().isConnected();
    }

    public boolean hasValidVLCPath() {
        String vlcPath = properties.getStringProperty(Property.VLC_PATH);
        return isValidVLCPath(vlcPath);
    }

    public boolean isValidVLCPath(String vlcPath) {
        if(!vlcPath.endsWith("vlc.exe"))
            return false;

        File vlc = new File(vlcPath);
        if(!vlc.isFile())
            return false;

        return true;
    }

    public void setVLCPath(String vlcPath) {
        properties.setProperty(Property.VLC_PATH, vlcPath);
    }

    public boolean startVLC() {
        String vlcPath = properties.getStringProperty(Property.VLC_PATH);
        if(isValidVLCPath(vlcPath))
            return vlc.start(vlcPath);

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
