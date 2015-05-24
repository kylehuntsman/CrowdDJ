package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.VLC;

import java.io.File;

public class Main {

    public static void main(String args[]) {
        //startVLC();
        addMusic();
		//play(23);

		VLC vlc = new VLC(1337, "pass2");
		System.out.println("\n" + vlc.getStatus());
    }

    public static void startVLC() {
        // Starts the VLC media player
        VLC vlc = new VLC(1337, "pass2");
        vlc.start("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
    }

    public static void addMusic() {
        File musicFolder = new File("C:/Users/Kyle/Music/Logic - Young Sinatra");
		VLC vlc = new VLC(1337, "pass2");

		File song = new File(musicFolder, "MySong.mp3");
		vlc.getController().add(song);
		/*
        // Adds music files to the playlist
        for (File file : musicFolder.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".mp3")) {
                vlc.getController().add(file);
            }
        }
        */
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
