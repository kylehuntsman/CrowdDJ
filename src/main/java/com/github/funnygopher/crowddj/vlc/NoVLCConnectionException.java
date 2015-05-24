package com.github.funnygopher.crowddj.vlc;

public class NoVLCConnectionException extends Exception {

	public void printError(String err) {
		System.err.append(err + "\n");
	}
}
