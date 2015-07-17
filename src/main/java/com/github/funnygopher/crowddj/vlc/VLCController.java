package com.github.funnygopher.crowddj.vlc;

import com.github.funnygopher.crowddj.exceptions.NoVLCConnectionException;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class VLCController {

    private VLC vlc;

    public VLCController(VLC vlc) {
        this.vlc = vlc;
    }

    private VLCStatus sendGetRequest(String endPoint, String requestParameters) throws NoVLCConnectionException {
        String urlString = endPoint;

        // Adds the ?command= to the url string
        if (requestParameters.length() > 0) {
            urlString += "?command=" + requestParameters;
        }

		try {
			// Sends the GET request
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();

			// Get the response
			return new VLCStatus(connection.getInputStream());
		} catch (IOException e) {
			// If connection.connect() can't connect, throw an error
			throw new NoVLCConnectionException();
		}
    }

    public VLCStatus add(File file) {
		System.out.println("Adding " + file.getName() + ".");

		// Checks if the file is an actual file, as well as an MP3
		if(!file.getName().endsWith(".mp3")) {
			System.err.append("Could not add " + file.getName() + ".\n");
			return vlc.getStatus();
		}

		// Prepares the file path for HTTP by encoding it
        String filePath = file.getAbsolutePath();
        try {
            filePath = URLEncoder.encode(file.getAbsolutePath(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            System.err.append("There was an error while encoding a file path.\n");
			e.printStackTrace();
        }

		// Attempts to send the GET request
        try {
			return sendGetRequest(vlc.STATUS, "in_enqueue&input=" + filePath);
        } catch (NoVLCConnectionException e) {
            e.printError("Could not add " + file.getName() + ". Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public Image getAlbumArt() {
        VLCStatus status = vlc.getStatus();

        if(status.isConnected()) {
            try {
                Image albumArt = new Image(status.getArtworkURL());
                return albumArt;
            } catch(NullPointerException e) {
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

    public VLCStatus play() {
		System.out.println("Starting playback.");

		// Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_play");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not start playback. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

	public VLCStatus play(int id) {
		System.out.println("Starting playback of song ID " + id + ".");

		// Attempts to send the GET request
		try {
            VLCPlaylist playlist = vlc.getPlaylist();
            for (VLCPlaylistItem item : playlist.getItems()) {
                if(item.getId() == id) {
                    return sendGetRequest(vlc.STATUS, "pl_play&id=" + id);
                }
            }
            throw new IndexOutOfBoundsException();
		} catch (NoVLCConnectionException e) {
			e.printError("Could not start playback. Not connected to VLC media player.");
			return VLCStatus.NO_CONNECTION;
		} catch (IndexOutOfBoundsException e) {
			System.err.append("Cancelled playback. Playlist does not contain a song with an ID of " + id + ".\n");
			return vlc.getStatus();
		}
	}

    public VLCStatus stop() {
		System.out.println("Stopping playback.");

		// Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_stop");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not stop playback. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public VLCStatus pause() {
		System.out.println("Pausing playback.");

		// Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_pause");
        } catch (NoVLCConnectionException e) {
			e.printError("Could not pause playback. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public VLCStatus next() {
        System.out.println("Skipping to next song.");

        // Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_next");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not skip to next song. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public VLCStatus clearPlaylist() {
        System.out.println("Clearing playlist.");

        // Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_empty");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not clear playlist. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public VLCStatus toggleRandom() {
        System.out.println("Toggling looping playback.");

        // Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_random");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not toggle random playback. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }

    public VLCStatus toggleLoop() {
        System.out.println("Toggling random playback.");

        // Attempts to send the GET request
        try {
            return sendGetRequest(vlc.STATUS, "pl_loop");
        } catch (NoVLCConnectionException e) {
            e.printError("Could not toggle looping playback. Not connected to VLC media player.");
            return VLCStatus.NO_CONNECTION;
        }
    }
}
