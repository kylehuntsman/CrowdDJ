package com.github.funnygopher.crowddj;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

public class Song {

	private File file;
	private String title;
	private String artist;
	private double duration;

	public Song(File file) throws SongCreationException {
		this.file = file;

		if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

		getMp3Information(file);
	}

	public File getFile() {
		return file;
	}

	public String getFilePath() {
		return file.getPath();
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public double getDuration() {
		return duration;
	}

	private void getMp3Information(File file) throws SongCreationException {
		try {
			InputStream input = new FileInputStream(file);
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();

			parser.parse(input, handler, metadata, parseCtx);
			input.close();

			title = metadata.get("title");
			artist = metadata.get("creator");
			//duration = Double.parseDouble(metadata.get("xmpDM:duration"));

		} catch(FileNotFoundException e) {
			throw new SongCreationException(file, e);
		} catch(TikaException e) {
			throw new SongCreationException(file, e);
		} catch(SAXException e) {
			throw new SongCreationException(file, e);
		} catch(IOException e) {
			throw new SongCreationException(file, e);
		}
	}
}
