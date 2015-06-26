package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.h2.DBUtil;
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

    private List<File> playlist;

    private Properties properties;

    public CrowdDJ() {
        this(8081, "root");
    }

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;
        this.controller = new CrowdDJController(this);

        // Sets up the VLC connection
        vlc = new VLC(8080, password);

        playlist = new ArrayList<File>();

        // Creates the properties file
        properties = getProperties();
        saveProperties();

        // Creates the database and tables if they don't exist
        setupDatabase();

        // Takes each song saved in the PLAYLIST table and adds it to the playlist
        try (Connection conn = DBUtil.getConnection()) {
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
            /*
            // Takes all the songs currently in the VLC instance and adds them to the playlist
            VLCPlaylist vlcPlaylist = vlc.getPlaylist();
            for (VLCPlaylistItem vlcPlaylistItem : vlcPlaylist.getItems()) {
                File file = new File(vlcPlaylistItem.getUri());
                playlist.add(file);
            }
            */

            // Adds the songs to VLC
            for (File file : playlist) {
                controller.addFile(file);
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

    public VLC getVLC() {
        return vlc;
    }

    public VLCStatus getStatus() {
        return vlc.getStatus();
    }

    public List<File> getPlaylist() {
        return playlist;
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
            saveProperties();
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

    private Properties getProperties() {
        String filename = "config.properties";
        Properties properties = new Properties();
        InputStream input = null;

        // Try loading from current directory
        try {
            File file = new File(filename);
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            input = null;
        }

        try {
            if(input == null) {
                // Try loading from classpath
                input = getClass().getResourceAsStream(filename);
            }

            // If found, load from properties
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVLCPath(properties.getProperty("VLCPath", "C:/Program Files (x86)/VideoLAN/VLC/vlc.exe"));
        return properties;
    }

    private void saveProperties() {
        String filename = "config.properties";

        try {
            Properties properties = new Properties();
            properties.setProperty("VLCPath", vlcPath);

            File file = new File(filename);
            OutputStream output = new FileOutputStream(file);
            properties.store(output, "Configuration properties for the CrowdDJ Desktop Application");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDatabase() {
        try (Connection conn = DBUtil.getConnection()) {
            DSLContext db = DSL.using(conn);

            db.execute(DBUtil.CREATE_PLAYLIST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
