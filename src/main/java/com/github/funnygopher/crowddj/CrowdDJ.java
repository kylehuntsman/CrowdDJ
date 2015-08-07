package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.database.DatabaseManager;
import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.player.AudioPlayer;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.playlist.SongPlaylist;
import com.github.funnygopher.crowddj.server.CrowdDJServer;
import com.github.funnygopher.crowddj.util.Property;
import com.github.funnygopher.crowddj.util.PropertyManager;

import java.util.ArrayList;

public class CrowdDJ {

    private static DatabaseManager database; // Manages calls to the database
    private static PropertyManager properties; // Manages the config.properties file

    private AudioPlayer player; // Handles the playback of audio
    private SongPlaylist playlist;
    private CrowdDJController controller;
    private CrowdDJServer server;

    public CrowdDJ() {
        // Sets up the properties file
		properties = new PropertyManager("crowddj.properties");

		// Sets up the database
		String dbUsername = properties.getStringProperty(Property.DB_USERNAME);
		String dbPassword = properties.getStringProperty(Property.DB_PASSWORD);
		database = new DatabaseManager("jdbc:h2:~/.CrowdDJ/db/crowddj", dbUsername, dbPassword);

        // Sets up the playlist
        playlist = new SongPlaylist(new ArrayList<Song>());

        // Sets up the player
        player = new AudioPlayer(playlist);

        controller = new CrowdDJController(player, playlist);

        server = new CrowdDJServer(player, playlist);
        server.start();
    }

    public static DatabaseManager getDatabase() {
        return database;
    }

    public static PropertyManager getProperties() {
        return properties;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public Player getPlayer() {
        return player;
    }

    public CrowdDJController getController() {
        return controller;
    }

    public CrowdDJServer getServer() {
        return server;
    }
}
