package com.github.funnygopher.crowddj;

public enum Property {

	VLC_PATH, VLC_PORT, VLC_PASSWORD,
	PORT,
	DB_USERNAME, DB_PASSWORD;

	public String getValue() {
		return toString().toLowerCase();
	}
}
