package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.database.DatabaseManager;
import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.player.SimplePlayer;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.playlist.SimplePlaylist;
import com.github.funnygopher.crowddj.server.CrowdDJServer;
import com.github.funnygopher.crowddj.util.Property;
import com.github.funnygopher.crowddj.util.PropertyManager;
import javafx.scene.control.TextInputDialog;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Optional;

public class CrowdDJ {

    private static DatabaseManager database; // Manages calls to the database
    private static PropertyManager properties; // Manages the config.properties file

    private SimplePlayer player; // Handles the playback of audio
    private SimplePlaylist playlist;
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
        playlist = new SimplePlaylist(new ArrayList<Song>());

        // Sets up the player
        player = new SimplePlayer(playlist);

        controller = new CrowdDJController(player, playlist);

        server = new CrowdDJServer(player, playlist);
        try {
            server.start();
        } catch (BindException e) {
            e.printStackTrace();
        }
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

    private void showUsedPortDialog() {
        int port = CrowdDJ.getProperties().getIntProperty(Property.PORT);

        TextInputDialog dialog = new TextInputDialog(String.valueOf(port));
        dialog.setTitle("Server Error");
        dialog.setHeaderText("The port " + port + " is already in use. You should try another port number.");
        dialog.setContentText("Port number:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        CrowdDJ.getProperties().setProperty(Property.PORT, result.get());
        CrowdDJ.getProperties().saveProperties();

        server.stop();
        server = new CrowdDJServer(player, playlist);
    }
}
