package com.github.funnygopher.crowddj.managers;

import com.github.funnygopher.crowddj.SearchParty;
import com.github.funnygopher.crowddj.Song;
import com.github.funnygopher.crowddj.exceptions.SongCreationException;

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

	public void clear() {
		songs.clear();
	}

	public SearchParty<Song> search(File file) {
		for(Song song : songs) {
			if(song.getFile().equals(file)) {
				return new SearchParty<Song>(song);
			}
		}

		return new SearchParty<Song>();
	}

	public void vote(Song song) {
		int votes = song.vote();
	}
}
