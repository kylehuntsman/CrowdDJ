package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.util.XFile;
import com.mpatric.mp3agic.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

public class Song {

    private String filepath;
	private String title;
	private String artist;
	private double duration;
	private int votes;
    private Image albumArt;

	public Song(File file) throws SongCreationException {
		this.filepath = file.getAbsolutePath();

		if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

		getMp3Information(filepath);
		votes = 0;
	}

	public File getFile() {
		return new File(filepath);
	}

	public String getURI() {
        try {
            return URLEncoder.encode(filepath, "UTF-8").replaceAll("\\+", "%20").replaceAll("&", "%26");
        } catch (UnsupportedEncodingException e) {
            return String.valueOf(getFile().toURI());
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

	private void getMp3Information(String filepath) throws SongCreationException {
        try {
            Mp3File song = new Mp3File(filepath);
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

    public void changeAlbumArt(File file) {
        String retagExtension = ".retag";
        String backupExtension = ".bak";
        try {
            Mp3File mp3File = new Mp3File(filepath);

            if (mp3File.hasId3v2Tag()) {
                ID3Wrapper oldId3Wrapper = new ID3Wrapper(mp3File.getId3v1Tag(), mp3File.getId3v2Tag());
                ID3Wrapper newId3Wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());
                newId3Wrapper.setTrack(oldId3Wrapper.getTrack());
                newId3Wrapper.setArtist(oldId3Wrapper.getArtist());
                newId3Wrapper.setTitle(oldId3Wrapper.getTitle());
                newId3Wrapper.setArtist(oldId3Wrapper.getArtist());
                newId3Wrapper.setAlbum(oldId3Wrapper.getAlbum());
                newId3Wrapper.setYear(oldId3Wrapper.getYear());
                newId3Wrapper.setGenre(oldId3Wrapper.getGenre());
                newId3Wrapper.setComment(oldId3Wrapper.getComment());
                newId3Wrapper.setComposer(oldId3Wrapper.getComposer());
                newId3Wrapper.setOriginalArtist(oldId3Wrapper.getOriginalArtist());
                newId3Wrapper.setCopyright(oldId3Wrapper.getCopyright());
                newId3Wrapper.setUrl(oldId3Wrapper.getUrl());
                newId3Wrapper.setEncoder(oldId3Wrapper.getEncoder());

                // Sets the new image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage img = ImageIO.read(file);
                ImageIO.write(img, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();

                newId3Wrapper.setAlbumImage(imageData, "image/jpeg");
                newId3Wrapper.getId3v2Tag().setPadding(true);

                mp3File.setId3v1Tag(newId3Wrapper.getId3v1Tag());
                mp3File.setId3v2Tag(newId3Wrapper.getId3v2Tag());
                mp3File.save(filepath + retagExtension);

                XFile originalFile = new XFile(filepath);
                XFile backupFile = new XFile(filepath + backupExtension);
                XFile retaggedFile = new XFile(filepath + retagExtension);
                if (backupFile.exists()) {
                    backupFile.delete();
                }

                originalFile.renameTo(backupFile);
                retaggedFile.renameTo(originalFile);

                if (backupFile.exists()) {
                    backupFile.delete();
                }


                getMp3Information(filepath);
            }
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        } catch (SongCreationException e) {
            e.printStackTrace();
        }
    }
}
