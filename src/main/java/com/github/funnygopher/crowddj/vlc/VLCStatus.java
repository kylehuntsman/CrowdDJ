package com.github.funnygopher.crowddj.vlc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

public class VLCStatus {

	public static final VLCStatus NO_CONNECTION = new VLCStatus();

    private boolean isConnected;
    private boolean isPlaying, isPaused, isStopped;
    private boolean isLooping;
    private boolean isRandom;
    private String title;
    private String artworkURL;
    private String artist;
    private double position;
    private int time;
    private int length;

    private VLCStatus() {
        isConnected = false;
    }

    public VLCStatus(String statusURL) {
        // Checks for a connection
        try {
            URL url = new URL(statusURL);
            URLConnection connection = url.openConnection();
            connection.connect();
            isConnected = true;
        } catch (IOException e) {
            isConnected = false;
        }

        // Parses the rest of the document for relevant information
        if (isConnected) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(statusURL);

                doc.getDocumentElement().normalize();
                parse(doc);
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                isConnected = false;
            } catch (ConnectException e) {
                //e.printStackTrace();
                isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
            }
        }
    }

    public VLCStatus(InputStream statusXML) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(statusXML);

            doc.getDocumentElement().normalize();
            parse(doc);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getArtworkURL() {
        return artworkURL;
    }

    public double getPosition() {
        return position;
    }

    @Override
    public String toString() {
        String string =
                "-- VLC Status --" +
                "\nisConnected: " + isConnected +
                "\nisPlaying:   " + isPlaying +
                "\nisPaused:    " + isPaused +
                "\nisStopped:   " + isStopped +
                "\nisLooping:   " + isLooping +
                "\nisRandom:    " + isRandom +
                "\nPosition:    " + position;

        if(isPlaying || isPaused)
            string += "\nCurrent Song: " + title;

        return string + "\n";
    }

    private void parse(Document doc) {
        parseState(doc);
        parseLoop(doc);
        parseRandom(doc);
        parsePosition(doc);
        parseTime(doc);
        parseLength(doc);

        if (isPlaying || isPaused) {
            parseCurrentSong(doc);
        }
    }

    private void parseState(Document doc) {
        // Gets VLC's playing state
        Node state = doc.getElementsByTagName("state").item(0);
        switch(state.getTextContent()) {
            case "playing":
                isPlaying = true;
                break;
            case "paused":
                isPaused = true;
                break;
            case "stopped":
                isStopped = true;
                break;
        }
    }

    private void parsePosition(Document doc) {
        Node node = doc.getElementsByTagName("position").item(0);
        position = Double.parseDouble(node.getTextContent().toString());
    }

    private void parseTime(Document doc) {
        Node node = doc.getElementsByTagName("time").item(0);
        time = Integer.parseInt(node.getTextContent().toString());
    }

    private void parseLength(Document doc) {
        Node node = doc.getElementsByTagName("length").item(0);
        length = Integer.parseInt(node.getTextContent().toString());
    }

    private void parseLoop(Document doc) {
        Node loop = doc.getElementsByTagName("loop").item(0);
        isLooping = loop.getTextContent().equals("true");
    }

    private void parseRandom(Document doc) {
        Node repeat = doc.getElementsByTagName("random").item(0);
        isRandom = repeat.getTextContent().equals("true");
    }

    private void parseCurrentSong(Document doc) {
        Element information = (Element) doc.getElementsByTagName("information").item(0);
        Element meta = (Element) information.getElementsByTagName("category").item(0);
        NodeList infoNodes = meta.getElementsByTagName("info");
        for(int i = 0; i < infoNodes.getLength(); i++) {
            Element info = (Element) infoNodes.item(i);
            if(info.getAttribute("name").equals("title"))
                title = info.getTextContent();
            if(info.getAttribute("name").equals("artist"))
                artist = info.getTextContent();
            if(info.getAttribute("name").equals("artwork_url"))
                artworkURL = info.getTextContent();
        }
    }

    public int getTime() {
        return time;
    }

    public int getLength() {
        return length;
    }
}
