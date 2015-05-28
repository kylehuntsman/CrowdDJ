package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.VLC;

import java.io.File;

public class CrowdDJ {

    private VLC vlc;
    private int port;
    private String password;

    private String vlcPath;
    private boolean validVLCPath;

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;

        vlc = new VLC(port, password);
        validVLCPath = false;
    }

    public VLC getVLC() {
        return vlc;
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
            vlc.start(vlcPath);
            return true;
        }

        return false;
    }
}
