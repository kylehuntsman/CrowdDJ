package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.*;

import java.io.File;

public class Main {

    public static final String BLACKMILL = "D:/Users/Kyle/Music/Blackmill/Blackmill - Miracle";

    public static void main(String args[]) {
        startVLC();
        addMusic(new File(BLACKMILL));
        testContains("MySong");

		VLC vlc = new VLC(1337, "pass2");
		System.out.println("\n" + vlc.getStatus());
    }

    public static void startVLC() {
        // Starts the VLC media player
        VLC vlc = new VLC(1337, "pass2");
        vlc.start("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
    }

    public static void addMusic(File musicFolder) {
		VLC vlc = new VLC(1337, "pass2");

        // Adds music files to the playlist
        for (File file : musicFolder.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".mp3")) {
                vlc.getController().add(file);
            }
        }
    }

    public static void testContains(String name) {
        try {
            VLC vlc = new VLC(1338, "pass2");
            VLCPlaylist playlist = vlc.getPlaylist();
            SearchParty<VLCPlaylistItem> searchParty = playlist.search(name);

            if(searchParty.found()) {
                VLCPlaylistItem playlistItem = searchParty.rescue();
                System.out.println("ID of MySong: " + playlistItem.getId());
            }
        } catch (NoVLCConnectionException e) {
            e.printError("Could not retrieve the playlist. Not connected to VLC media player.");
        }
    }

	public static void play() {
		VLC vlc = new VLC(1337, "pass2");
		vlc.getController().play();
	}

	public static void play(int id) {
		VLC vlc = new VLC(1337, "pass2");
		vlc.getController().play(id);
	}

	public static void pause() {
		VLC vlc = new VLC(1337, "pass2");
		vlc.getController().pause();
	}

	public static void toggleRandom() {
		VLC vlc = new VLC(1337, "pass2");
		vlc.getController().toggleRandom();
	}
}
