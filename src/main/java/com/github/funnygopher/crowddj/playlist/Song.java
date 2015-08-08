package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.util.XFile;
import com.mpatric.mp3agic.*;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

public class Song {

    private String filepath;
	private StringProperty title;
	private StringProperty artist;
    private StringProperty album;
	private IntegerProperty duration;
	private IntegerProperty votes;
    private ObjectProperty<Image> coverArt;

	public Song(File file) throws SongCreationException {
		this.filepath = file.getAbsolutePath();

		if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

        title = new SimpleStringProperty(this, "title", "");
        artist = new SimpleStringProperty(this, "artist", "");
        album = new SimpleStringProperty(this, "album", "");
        duration = new SimpleIntegerProperty(this, "duration", 0);
        votes = new SimpleIntegerProperty(this, "votes", 0);
        coverArt = new SimpleObjectProperty<>(this, "coverArt", null);

		getMp3Information(filepath);
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


    public ReadOnlyStringProperty titleProperty() {
        return title;
    }

    public ReadOnlyStringProperty artistProperty() {
        return artist;
    }

    public ReadOnlyStringProperty albumProperty() {
        return album;
    }

    public ReadOnlyIntegerProperty durationProperty() {
        return duration;
    }

    public ReadOnlyIntegerProperty votesProperty() {
        return votes;
    }

    public ReadOnlyObjectProperty<Image> coverArtProperty() {
        return coverArt;
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
		votes.set(votes.get() + 1);
		return votes.get();
	}

	private void getMp3Information(String filepath) throws SongCreationException {
        try {
            Mp3File song = new Mp3File(filepath);
            if (song.hasId3v2Tag()) {
                ID3v2 tag = song.getId3v2Tag();

                title.set(tag.getTitle());
                artist.set(tag.getArtist());
                album.set(tag.getAlbum());
                duration.set(tag.getLength());

                byte[] imageData = tag.getAlbumImage();
				if(imageData != null) {
                    coverArt.set(new Image(new ByteArrayInputStream(imageData)));
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
                ID3v2 tag = mp3File.getId3v2Tag();

                // Sets the new image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage img = ImageIO.read(file);
                ImageIO.write(img, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();

                tag.setAlbumImage(imageData, "image/jpeg");
                mp3File.setId3v2Tag(tag);
                mp3File.save(filepath + retagExtension);

                XFile originalFile = new XFile(filepath);
                XFile backupFile = new XFile(filepath + backupExtension);
                XFile retaggedFile = new XFile(filepath + retagExtension);
                if (backupFile.exists())
                    backupFile.delete();

                originalFile.renameTo(backupFile);
                retaggedFile.renameTo(originalFile);

                if (backupFile.exists())
                    backupFile.delete();

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
