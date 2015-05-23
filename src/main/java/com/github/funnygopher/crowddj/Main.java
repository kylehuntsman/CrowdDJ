package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.VLC;
import com.github.funnygopher.crowddj.vlc.VLCPlaylist;

import java.io.File;

public class Main {

    public static void main(String args[]) throws Exception {
        startVLC();
        addMusic();
        //getFirstSong();
    }

    public static void startVLC() {
        // Starts the VLC media player
        VLC vlc = new VLC(1337);
        vlc.start("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
    }

    public static void addMusic() {
        File musicFolder = new File("D:/Users/Kyle/Music/Blackmill/Blackmill - Miracle");
        VLC vlc = new VLC(1337);

        // Adds music files to the playlist
        for (File file : musicFolder.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".mp3")) {
                vlc.getController().add(file);
            }
        }
    }

    public static void getFirstSong() {
        VLC vlc = new VLC(1337);
        VLCPlaylist playlist = vlc.getPlaylist();
        try {
            File file = playlist.get(-1);
            vlc.getController().add(file);
        } catch (IndexOutOfBoundsException e) {
            System.err.append(e.getMessage());
        }
    }
}
