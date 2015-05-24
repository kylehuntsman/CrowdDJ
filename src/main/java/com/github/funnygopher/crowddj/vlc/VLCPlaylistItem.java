package com.github.funnygopher.crowddj.vlc;

import java.net.URI;

public class VLCPlaylistItem {

	private String name;
	private int id;
	private int duration;
	private URI uri;

	public VLCPlaylistItem(String name, int id, int duration, URI uri) {
		this.name = name;
		this.id = id;
		this.duration = duration;
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getDuration() {
		return duration;
	}

	public URI getUri() {
		return uri;
	}
}
