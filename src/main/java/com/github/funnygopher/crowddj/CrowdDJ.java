package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.database.Database;
import com.github.funnygopher.crowddj.database.SimpleDatabase;
import com.github.funnygopher.crowddj.playlist.SongCreationException;
import com.github.funnygopher.crowddj.javafx.AudioPlayer;
import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.jetty.AnybodyHomeHandler;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.jetty.PlaylistHandler;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.SimplePlaylist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.util.Property;
import com.github.funnygopher.crowddj.util.PropertyManager;
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
import java.util.ArrayList;

import static com.github.funnygopher.crowddj.database.jooq.Tables.PLAYLIST;

public class CrowdDJ {

    private Server server;
    private CrowdDJController controller; // Strictly UI management
    private PropertyManager properties; // Manages the config.properties file
    private SimpleDatabase database; // Manages calls to the database
    private SimplePlaylist playlist;

    private AudioPlayer player; // Handles the playback of audio

    public CrowdDJ() {
        this.controller = new CrowdDJController(this);

        // Sets up the properties file
		properties = new PropertyManager("config.properties");

		// Sets up the database
		String dbUsername = properties.getStringProperty(Property.DB_USERNAME);
		String dbPassword = properties.getStringProperty(Property.DB_PASSWORD);
		database = new SimpleDatabase("jdbc:h2:~/.CrowdDJ/db/crowddj", dbUsername, dbPassword);

        // Sets up the playlist
        playlist = new SimplePlaylist(new ArrayList<Song>());

        // Sets up the player
        player = new AudioPlayer(this, playlist);

        // Takes each song saved in the PLAYLIST table and adds it to the player's playlist
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn, SQLDialect.H2);
            Result<Record> results = db.select().from(PLAYLIST).fetch();

            for (Record result : results) {
                String filepath = result.getValue(PLAYLIST.FILEPATH);
                File file = new File(filepath);
                try {
                    Song song = new Song(file);
                    playlist.add(song);
                } catch (SongCreationException e) {
                    e.printError(e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
			playlistContext.setHandler(new PlaylistHandler(playlist));

            ContextHandler anybodyHomeContext = new ContextHandler();
            anybodyHomeContext.setContextPath("/anybodyhome");
            anybodyHomeContext.setHandler(new AnybodyHomeHandler());

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[]{playbackContext, playlistContext, anybodyHomeContext});
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

	public Database getDatabase() {
		return database;
	}

    public Playlist getPlaylist() {
        return playlist;
    }

    public AudioPlayer getPlayer() {
        return player;
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
