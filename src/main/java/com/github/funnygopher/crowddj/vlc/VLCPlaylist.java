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
import java.util.List;

public class VLCPlaylist {

	private List<VLCPlaylistItem> playlist;

    public VLCPlaylist(String playlistURL) throws NoVLCConnectionException {
        // Checks for a connection
        try {
            URL url = new URL(playlistURL);
            URLConnection connection = url.openConnection();
            connection.connect();
        } catch (IOException e) {
			throw new NoVLCConnectionException();
        }

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
        if(index < 0 || index >= playlist.size()) {
            throw new IndexOutOfBoundsException("The playlist contains [" + playlist.size() +
                    "] songs, and was asked for the song at index [" + index + "].\n");
        }

        VLCPlaylistItem vlcPlaylistItem = playlist.get(index);
		return new File(vlcPlaylistItem.getUri());
    }

    public File get(String songName) throws NullPointerException {
		for(VLCPlaylistItem vlcPlaylistItem : playlist) {
			if(vlcPlaylistItem.getName().equals(songName)) {

			}
		}

        throw new NullPointerException("A song with the name [" + songName + "] could not be found.\n");
    }

	public int getLength() {
		return playlist.size();
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
            NodeList playlistItems = playlistElement.getElementsByTagName("leaf");

			for(int i = 0; i < playlistItems.getLength(); i++) {
				Element item = (Element) playlistItems.item(i);
				try {
					String name = item.getAttribute("name");
					int id = Integer.parseInt(item.getAttribute("id"));
					int duration = Integer.parseInt(item.getAttribute("duration"));
					URI uri = new URI(item.getAttribute("uri"));

					VLCPlaylistItem vlcPlaylistItem = new VLCPlaylistItem(
							name, id, duration, uri
					);
					playlist.add(vlcPlaylistItem);
				} catch(URISyntaxException e) {
					e.printStackTrace();
				}

			}
		}
    }
}
