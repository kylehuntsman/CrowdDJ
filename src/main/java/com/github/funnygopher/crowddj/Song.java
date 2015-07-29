package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.exceptions.SongCreationException;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
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
        //return "<song title=\"" + title + "\" artist=\"" + artist + "\" votes=\"" + votes + "\" uri=\"" + getURI() + "\"/>";
	}

    private Media toMedia() {
        return new Media(getFileURI());
    }

	public int vote() {
		votes += 1;
		return votes;
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

			if(title == null) {
				String title = file.getName();
				if(title.endsWith(".mp3")) {
					this.title = title.substring(0, title.length() - 4);
				}
			}
			if(artist == null) {
				artist = "";
			}
		} catch(FileNotFoundException e) {
			throw new SongCreationException(file, e);
		} catch(TikaException e) {
			throw new SongCreationException(file, e);
		} catch(SAXException e) {
			throw new SongCreationException(file, e);
		} catch(IOException e) {
			throw new SongCreationException(file, e);
		}

        try {
            Mp3File song = new Mp3File(file);
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                byte[] imageData = id3v2tag.getAlbumImage();
                albumArt = new Image(new ByteArrayInputStream(imageData));
            }
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
