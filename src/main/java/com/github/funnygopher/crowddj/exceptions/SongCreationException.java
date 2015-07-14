package com.github.funnygopher.crowddj.exceptions;

import java.io.File;

public class SongCreationException extends Exception {
	private File file;
	private String message;

	private Exception e;

	public SongCreationException(File file) {
        this(file, null);
	}

	public SongCreationException(File file, Exception e) {
        this.file = file;
        if(e == null)
            this.e = this;

        message = "There was a problem creating a song object with " + file.getName();
	}

	public File getFile() {
		return file;
	}

	public String getMessage() {
		return message;
	}

	public void printStackTrace() {
		e.printStackTrace();
	}

	public void printError(String err) {
		System.err.append(err + "\n");
	}


}
