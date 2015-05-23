package com.github.funnygopher.crowddj.vlc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class VLCPlaylist {

    private boolean isConnected;
    private NodeList playlist;

    public VLCPlaylist(String playlistURL) {
        // Checks for a connection
        try {
            URL url = new URL(playlistURL);
            URLConnection connection = url.openConnection();
            connection.connect();
            isConnected = true;
        } catch (IOException e) {
            isConnected = false;
        }

        if(isConnected) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(playlistURL);

                doc.getDocumentElement().normalize();
                parse(doc);
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public VLCPlaylist(InputStream playlistXML) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(playlistXML);

            doc.getDocumentElement().normalize();
            parse(doc);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File get(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= playlist.getLength()) {
            throw new IndexOutOfBoundsException("The playlist contains [" + playlist.getLength() +
                    "] songs, and was asked for the song at index [" + index + "].\n");
        }

        Element playlistItem = (Element) playlist.item(index);
        try {
            URI fileURI = new URI(playlistItem.getAttribute("uri"));
            return new File(fileURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public File get(String songName) throws NullPointerException {
        for (int i = 0; i < playlist.getLength(); i++) {
            Element element = (Element) playlist.item(i);
            if(element.getAttribute("name").contains(songName)) {
                return get(i);
            }
        }

        throw new NullPointerException("A song with the name [" + songName + "] could not be found.\n");
    }

    private void parse(Document doc) {
        NodeList nodes = doc.getElementsByTagName("node");
        Element playlistElement = null;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if(element.getAttribute("name").equals("Playlist")) {
                playlistElement = element;
            }
        }

        if(playlistElement != null) {
            playlist = playlistElement.getElementsByTagName("leaf");
        }
    }
}
