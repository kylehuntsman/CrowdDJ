package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.database.DatabaseManager;
import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.player.SimplePlayer;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.SimplePlaylist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.server.CrowdDJServer;
import com.github.funnygopher.crowddj.util.Property;
import com.github.funnygopher.crowddj.util.PropertyManager;
import com.github.funnygopher.crowddj.voting.SimpleVotingBooth;
import com.github.funnygopher.crowddj.voting.VotingBooth;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.net.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CrowdDJ {

    private static DatabaseManager database; // Manages calls to the database
    private static PropertyManager properties; // Manages the config.properties file

    private SimplePlayer player; // Handles the playback of audio
    private SimplePlaylist playlist;
    private CrowdDJController controller;
    private CrowdDJServer server;
    private VotingBooth votingBooth;

    private final String serverCode;
    private boolean validPort;

    public CrowdDJ() throws UnknownHostException, SocketException {
        // Sets up the properties file
		properties = new PropertyManager("crowddj.properties");

		// Sets up the database
		String dbUsername = properties.getStringProperty(Property.DB_USERNAME);
		String dbPassword = properties.getStringProperty(Property.DB_PASSWORD);
		database = new DatabaseManager("jdbc:h2:~/.CrowdDJ/db/crowddj", dbUsername, dbPassword);

        playlist = new SimplePlaylist(new ArrayList<Song>());
        votingBooth = new SimpleVotingBooth();
        player = new SimplePlayer(playlist, votingBooth);
        server = new CrowdDJServer(player, playlist, votingBooth);

        validPort = false;
        do {
            try {
                server.start();
                validPort = true;
            } catch (BindException e) {
                server.forceStop();
                showUsedPortDialog();
            }
        } while(!validPort);

        serverCode = createServerCode();
        String fullIP = "crowddjmobileapp://" + getIpAddress() + ":" + properties.getIntProperty(Property.PORT);
        controller = new CrowdDJController(player, playlist, fullIP);
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
        int newPort = port + 1;
        TextInputDialog dialog = new TextInputDialog(String.valueOf(newPort));
        dialog.setTitle("Server Error");
        dialog.setHeaderText("The port " + port + " is already in use. Cancelling will stop the server.");
        dialog.setContentText("New port number:");

        try {
            Optional<String> result = dialog.showAndWait();
            CrowdDJ.getProperties().setProperty(Property.PORT, result.get());
            CrowdDJ.getProperties().saveProperties();

            server = new CrowdDJServer(player, playlist, votingBooth);
        } catch (NoSuchElementException e) {
            server.forceStop();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Server Information");
            alert.setHeaderText("Server functionality will not be active");
            alert.setContentText("To enable server functionality, restart the application.");
            alert.showAndWait();
            validPort = true;
        }
    }

    private String getIpAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    private String createServerCode() throws UnknownHostException, SocketException {
        String ipAddress = getIpAddress();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost());
        int mask = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();

        String[] octets = ipAddress.split("\\.");
        String lengthData = "";
        String unhashedServerCode = "";

        // Creates the serverCode and the lengthData
        for(int i = 0; i <= 24; i += 8) {
            if(i <= mask && mask < (i + 8)) {
                int index = i / 8;
                unhashedServerCode += octets[index];
                lengthData += octets[index].length();
            }
        }
        lengthData = String.format("%0"+(4-lengthData.length())+"d%s", 0, lengthData);

        int port = CrowdDJ.getProperties().getIntProperty(Property.PORT);
        String hashedPort = Integer.toString(port, 36).toUpperCase();

        unhashedServerCode += lengthData;
        String hashedServerCode = Long.toString(Long.valueOf(unhashedServerCode), 36).toUpperCase();
        hashedServerCode += "-" + hashedPort;

        System.out.println(unhashedServerCode);
        System.out.println(hashedServerCode);

        return hashedServerCode;
    }
}
