package com.github.funnygopher.crowddj;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioFile {

    private File audioFile;
    private Format format;
    private String title, artist;
    private double duration;

    public AudioFile(File audioFile, Format format) throws TikaException, IOException, SAXException {
        this.audioFile = audioFile;
        this.format = format;
        getFileInformation();
    }

    private void getFileInformation() throws TikaException, IOException, SAXException {
        switch(format) {
            case MP3:
                getMp3Information();
                break;
            case MP4:
                getMp4Information();
                break;
            default:
                title = "Unknown";
                artist = "Unknown";
                duration = 0;
        }
    }

    private void getMp3Information() throws IOException, TikaException, SAXException {
        InputStream input = new FileInputStream(audioFile);
        ContentHandler handler = new DefaultHandler();
        Metadata metadata = new Metadata();
        Parser parser = new Mp3Parser();
        ParseContext parseCtx = new ParseContext();

        parser.parse(input, handler, metadata, parseCtx);
        input.close();

        title = metadata.get("title");
        artist = metadata.get("creator");
        duration = Double.parseDouble(metadata.get("xmpDM:duration"));
    }

    private void getMp4Information() throws IOException, TikaException, SAXException {
        InputStream input = new FileInputStream(audioFile);
        ContentHandler handler = new DefaultHandler();
        Metadata metadata = new Metadata();
        Parser parser = new MP4Parser();
        ParseContext parseCtx = new ParseContext();

        parser.parse(input, handler, metadata, parseCtx);
        input.close();

        title = metadata.get("title");
        artist = metadata.get("creator");
        duration = Double.parseDouble(metadata.get("xmpDM:duration"));
    }

    public File getAudioFile() {
        return audioFile;
    }

    public Format getFormat() {
        return format;
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

    public enum Format {
        MP3, MP4
    }
}
