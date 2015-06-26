package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.h2.DBUtil;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.vlc.NoVLCConnectionException;
import com.github.funnygopher.crowddj.vlc.VLC;
import com.github.funnygopher.crowddj.vlc.VLCPlaylist;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import org.eclipse.jetty.server.Server;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class CrowdDJ {

    private VLC vlc;
    private int port;
    private String password;

    private Server server;

    private String vlcPath;
    private boolean validVLCPath;

    private VLCPlaylist playlist;

    private Properties prop;
    private OutputStream output;

    public CrowdDJ() {
        this(8081, "root");
    }

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;

        vlc = new VLC(8080, password);
        validVLCPath = false;

        // Check if the database is setup
        setupDatabase();

        playlist = new VLCPlaylist();

        try {
            server = new Server(port);
            server.setHandler(new PlaybackHandler(this));
            server.start();
        } catch (Exception e) {
            System.err.append("Could not start server. Something is wrong...\n");
            e.printStackTrace();
        }
    }

    public VLC getVLC() {
        return vlc;
    }

    public VLCStatus getStatus() {
        return vlc.getStatus();
    }

    public VLCPlaylist getPlaylist() {
        try {
            playlist = vlc.getPlaylist();
        } catch (NoVLCConnectionException e) {
            e.printError("Could not update the playlist. Not connected to VLC media player.");
        }
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
            this.vlcPath = vlcPath;
            validVLCPath = true;
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

    private void setupDatabase() {
        try (Connection conn = DBUtil.getConnection()) {
            DSLContext db = DSL.using(conn);

            db.execute(DBUtil.CREATE_PLAYLIST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
