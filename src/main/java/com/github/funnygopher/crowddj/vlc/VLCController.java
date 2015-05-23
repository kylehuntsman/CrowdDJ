package com.github.funnygopher.crowddj.vlc;

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

    private VLCStatus sendGetRequest(String endPoint, String requestParameters) throws IOException {
        String urlString = endPoint;

        // Adds the ?command= to the url string
        if (requestParameters.length() > 0) {
            urlString += "?command=" + requestParameters;
        }

        // Sends the GET request
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.connect();

        // Get the response
        return new VLCStatus(connection.getInputStream());
    }

    public VLCStatus add(File file) {
        String filePath = file.getAbsolutePath();
        try {
            filePath = URLEncoder.encode(file.getAbsolutePath(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        VLCStatus status = new VLCStatus();
        try {
            return sendGetRequest(vlc.STATUS, "in_enqueue&input=" + filePath);
        } catch (IOException e) {
            System.err.append("Could not add " + file.getName() + ". Not connected to VLC media player.\n");
            return status;
        }
    }

    public VLCStatus play() {
        VLCStatus status = new VLCStatus();
        try {
            return sendGetRequest(vlc.STATUS, "pl_play");
        } catch (IOException e) {
            System.err.append("Could not start playback. Not connected to VLC media player.\n");
            return status;
        }
    }

    public VLCStatus stop() {
        VLCStatus status = new VLCStatus();
        try {
            return sendGetRequest(vlc.STATUS, "pl_stop");
        } catch (IOException e) {
            System.err.append("Could not stop playback. Not connected to VLC media player.\n");
            return status;
        }
    }

    public VLCStatus pause() {
        VLCStatus status = new VLCStatus();
        try {
            return sendGetRequest(vlc.STATUS, "pl_pause");
        } catch (IOException e) {
            System.err.append("Could not pause playback. Not connected to VLC media player.\n");
            return status;
        }
    }

    public VLCStatus toggleRandom() {
        VLCStatus status = new VLCStatus();
        try {
            return sendGetRequest(vlc.STATUS, "pl_random");
        } catch (IOException e) {
            System.err.append("Could not toggle random playback. Not connected to VLC media player.\n");
            return status;
        }
    }
}
