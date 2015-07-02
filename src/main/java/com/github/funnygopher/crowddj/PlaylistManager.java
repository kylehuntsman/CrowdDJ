package com.github.funnygopher.crowddj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {

	private List<Song> songs;

	public PlaylistManager() {
		songs = new ArrayList<Song>();
	}

	public List<Song> getItems() {
		return songs;
	}

	public void add(File file) {
		try {
			Song song = new Song(file);
			songs.add(song);
		} catch(SongCreationException e) {
			e.printError(e.getMessage());
		}
	}

	public void add(Song song) {
		songs.add(song);
	}

	public void remove(Song song) {
		songs.remove(song);
	}

	public SearchParty<Song> search(File file) {
		for(Song song : songs) {
			if(song.getFile().equals(file)) {
				return new SearchParty<Song>(song);
			}
		}

		return new SearchParty<Song>();
	}
}
