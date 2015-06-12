package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.NoVLCConnectionException;
import com.github.funnygopher.crowddj.vlc.VLC;
import com.github.funnygopher.crowddj.vlc.VLCPlaylist;
import com.github.funnygopher.crowddj.vlc.VLCStatus;

import java.io.File;

public class CrowdDJ {

    private VLC vlc;
    private int port;
    private String password;

    private String vlcPath;
    private boolean validVLCPath;

    private VLCPlaylist playlist;

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;

        vlc = new VLC(port, password);
        validVLCPath = false;

        playlist = new VLCPlaylist();
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
}
