package com.github.funnygopher.crowddj.vlc;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Represents a connection to an instance of a VLC media player. The VLC media player can be started by either manually opening VLC
 * media player on the computer, or by using the <code>start()</code> function. Either way, the VLC media player
 * must be started before it can be controlled by a <code>VLCController</code>.
 */
public class VLC {
    /**
     * A URL to the VLC media player crowddj.xml file. The file contains information about the current playlist.
     */
    public final String PLAYLIST;

    /**
     * A URL to the VLC media player status.xml file. The file contains information about the current state of the VLC
     * media player.
     */
    public final String STATUS;

    /**
     * A URL to the VLC media player album art of the currently playing song.
     */
    public final String ALBUM_ART;

    /**
     * The port number in which the VLC media player web server is on.
     */
    private int port;

	private String password;

    /**
     * An instance of a <code>VLCController</code>. Can be used to control the VLC media player playlist.
     */
    private VLCController controller;

    /**
     * Constructor for a <code>VLC</code> object. Uses the default port of 8080. Use this method of instantiation for
     * OS X and Linux machines, after starting VLC media player manually.
     */
    public VLC(String password) {
        this(8080, password);
    }

    /**
     * Constructor for a <code>VLC</code> object.
     * @param port The port number in which the VLC media player web server is on.
     */
    public VLC(int port, String password) {
        PLAYLIST = "http://:" + password + "@localhost:" + port + "/requests/crowddj.xml";
        STATUS = "http://:" + password + "@localhost:" + port + "/requests/status.xml";
        ALBUM_ART = "http://:" + password + "@localhost:" + port + "/art";

        this.port = port;
		this.password = password;

		// This sets the default authentication method for url connections
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("", password.toCharArray());
			}
		});

        controller = new VLCController(this);
    }

    /**
     * Starts the VLC media player and it's web server on the specified port. If there is already a web server running
     * on the specified port, nothing happens. Currently doesn't support Mac OS X or Linux.
     * @param vlcPath The file path the the VLC media player executable file
     */
    public boolean start(String vlcPath) {
        if(getStatus().isConnected())
            return false;

        File vlcExe = new File(vlcPath);
        if(!vlcExe.isFile())
            return false;

        String parameters = "\"" + vlcPath + "\"" +
				" --extraintf=http" + // Starts VLC with the web interface as an additional interface
				" --http-port=" + port + // Tells the VLC web server what port to start on
				" --http-password=" + password; // Sets the password for the VLC web server
        //     parameters = "vlc.exe" --extraintf=http --http-port=8080 --http-password=password

        try {
            Runtime.getRuntime().exec("cmd /C " + parameters); // This is potentially dangerous...
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
    public VLCPlaylist getPlaylist() throws NoVLCConnectionException {
        return new VLCPlaylist(PLAYLIST);
    }
}
