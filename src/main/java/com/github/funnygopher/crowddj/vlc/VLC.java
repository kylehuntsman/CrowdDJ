package com.github.funnygopher.crowddj.vlc;

import java.io.IOException;

/**
 * Represents a connection to an instance of a VLC media player. The VLC media player can be started by either manually opening VLC
 * media player on the computer, or by using the <code>start()</code> function. Either way, the VLC media player
 * must be started before it can be controlled by a <code>VLCController</code>.
 */
public class VLC {
    /**
     * A URL to the VLC media player playlist.xml file. The file contains information about the current playlist.
     */
    public final String PLAYLIST;

    /**
     * A URL to the VLC media player status.xml file. The file contains information about the current state of the VLC
     * media player.
     */
    public final String STATUS;

    /**
     * The port number in which the VLC media player web server is on.
     */
    private int port;

    /**
     * An instance of a <code>VLCController</code>. Can be used to control the VLC media player playlist.
     */
    private VLCController controller;

    /**
     * Constructor for a <code>VLC</code> object. Uses the default port of 8080. Use this method of instantiation for
     * OS X and Linux machines, after starting VLC media player manually.
     */
    public VLC() {
        this(8080);
    }

    /**
     * Constructor for a <code>VLC</code> object.
     * @param port The port number in which the VLC media player web server is on.
     */
    public VLC(int port) {
        PLAYLIST = "http://localhost:" + port + "/requests/playlist.xml";
        STATUS = "http://localhost:" + port + "/requests/status.xml";

        this.port = port;

        controller = new VLCController(this);
    }

    /**
     * Starts the VLC media player and it's web server on the specified port. If there is already a web server running
     * on the specified port, nothing happens. Currently doesn't support Mac OS X or Linux.
     * @param vlcPath The file path the the VLC media player executable file
     */
    public void start(String vlcPath) {
        if(getStatus().isConnected())
            return;

        String parameters = "\"" + vlcPath + "\"" + " --http-port=" + port;
        //     parameters = "vlc.exe" --http-port=8080

        try {
            Runtime.getRuntime().exec("cmd /C " + parameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return A <code>VLCController</code> object, which can be used to control the VLC media player playlist.
     */
    public VLCController getController() {
        return controller;
    }

    /**
     * @return A <code>VLCStatus</code> object, which can be used to get information about the current state of the VLC
     * media player.
     */
    public VLCStatus getStatus() {
        return new VLCStatus(STATUS);
    }

    /**
     * @return A <code>VLCPlaylist</code> object, which can be used to get information about the current playlist.
     */
    public VLCPlaylist getPlaylist() {
        return new VLCPlaylist(PLAYLIST);
    }
}
