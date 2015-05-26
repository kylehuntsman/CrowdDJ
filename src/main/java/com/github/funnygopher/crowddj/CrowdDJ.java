package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.VLC;

public class CrowdDJ {

    private VLC vlc;
    private int port;
    private String password;
    private String vlcPath;

    public CrowdDJ(int port, String password) {
        this.port = port;
        this.password = password;

        vlc = new VLC(port, password);
    }

    public VLC getVLC() {
        return vlc;
    }

    public void setVLCPath(String vlcPath) {
        this.vlcPath = vlcPath;
    }

    public boolean startVLC() {
        if(vlcPath != null) {
            // There should probably be a check here for an invalid path
            vlc.start(vlcPath);
            return true;
        }

        return false;
    }
}
