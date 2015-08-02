package com.github.funnygopher.crowddj.util;

public enum Property {
	PORT,
	DB_USERNAME,
	DB_PASSWORD;

	public String getValue() {
		return toString().toLowerCase();
	}
}
