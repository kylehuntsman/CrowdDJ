package com.github.funnygopher.crowddj.playlist;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Song {

	private File file;
	private String title;
	private String artist;
	private double duration;
	private int votes;
    private Image albumArt;

	public Song(File file) throws SongCreationException {
		this.file = file;

		if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

		getMp3Information(file);
		votes = 0;
	}

	public File getFile() {
		return file;
	}

	public String getURI() {
        try {
            return URLEncoder.encode(file.getPath(), "UTF-8").replaceAll("\\+", "%20").replaceAll("&", "%26");
        } catch (UnsupportedEncodingException e) {
            return String.valueOf(file.toURI());
        }
    }

	public String getFileURI() {
		return "file:///" + getURI();
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

	public int getVotes() {
		return votes;
	}

    public Image getAlbumArt() {
        return albumArt;
    }

	public String toXML() {
		String xmlString = "<song>" +
				"<title>" + title + "</title>" +
				"<artist>" + artist + "</artist>" +
				"<uri>" + getURI() + "</uri>" +
				"<votes>" + votes + "</votes>" +
				"</song>";

		return xmlString;
	}

	public int vote() {
		votes += 1;
		return votes;
	}

	private void getMp3Information(File file) throws SongCreationException {
        try {
            Mp3File song = new Mp3File(file);
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                title = id3v2tag.getTitle();
                artist = id3v2tag.getArtist();
                duration = id3v2tag.getLength();

                byte[] imageData = id3v2tag.getAlbumImage();
				if(imageData != null)
                	albumArt = new Image(new ByteArrayInputStream(imageData));
				else {
					albumArt = null;
				}
            }
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
    }
}
