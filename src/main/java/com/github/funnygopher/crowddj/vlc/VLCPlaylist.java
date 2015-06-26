package com.github.funnygopher.crowddj.vlc;

import com.github.funnygopher.crowddj.SearchParty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class VLCPlaylist {

	private List<VLCPlaylistItem> playlist;

    public VLCPlaylist() {
        playlist = new ArrayList<VLCPlaylistItem>();
    }

    public VLCPlaylist(String playlistURL) throws NoVLCConnectionException {
        // Checks for a connection
        try {
            URL url = new URL(playlistURL);
            URLConnection connection = url.openConnection();
            connection.connect();
        } catch (IOException e) {
			throw new NoVLCConnectionException();
        }

        playlist = new ArrayList<VLCPlaylistItem>();

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

	public List<VLCPlaylistItem> getItems() {
		return playlist;
	}

	public SearchParty<VLCPlaylistItem> search(File file) {
		for (VLCPlaylistItem vlcPlaylistItem : playlist) {
			File songFile = new File(vlcPlaylistItem.getUri());
			if(songFile.equals(file)) {
				return new SearchParty<VLCPlaylistItem>(vlcPlaylistItem);
			}
		}

		return new SearchParty<VLCPlaylistItem>();
	}

	public int size() {
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
