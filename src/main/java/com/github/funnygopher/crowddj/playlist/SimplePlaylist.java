package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.database.Database;
import com.github.funnygopher.crowddj.util.SearchParty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.github.funnygopher.crowddj.database.jooq.Tables.PLAYLIST;

public class SimplePlaylist implements Playlist {

    private ObservableList<Song> thePlaylist;
    public final String CREATE_PLAYLIST_TABLE =
            "CREATE TABLE IF NOT EXISTS PLAYLIST (" +
                    "ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "FILEPATH VARCHAR(255) NOT NULL" +
                    ");";

    public SimplePlaylist(List<Song> thePlaylist) {
        this.thePlaylist = FXCollections.observableArrayList(thePlaylist);
    }

    public void add(File file) {
        if(file.isDirectory()) {
            for (File fileInDir : file.listFiles()) {
                add(fileInDir);
            }
        } else {
            try {
                Song song = new Song(file);
                add(song);
            } catch (SongCreationException e) {
                e.printError(e.getMessage());
            }
        }
    }

    public void add(Song song) throws NullPointerException {
        if(song == null) {
            throw new NullPointerException();
        }

        if(!thePlaylist.contains(song)) {
            thePlaylist.add(song);
        }
    }

    public void add(List<Song> songs) {
        for (Song song : songs) {
            add(song);
        }
    }

    public void remove(Song song) {
        thePlaylist.remove(song);
    }

    public void clear() {
        thePlaylist.clear();
    }

    public int size() {
        return thePlaylist.size();
    }

    public int vote(File file) {
        SearchParty<Song> party = search(file);
        if(party.found()) {
            Song song = party.rescue();
            return song.vote();
        }
        return 0;
    }

    public int vote(Song song) {
        if(thePlaylist.contains(song)) {
            return song.vote();
        }
        return 0;
    }

    public SearchParty<Song> search(File file) {
        for(Song song : thePlaylist) {
            if(song.getFile().equals(file)) {
                return new SearchParty<Song>(song);
            }
        }
        return new SearchParty<Song>();
    }

    public String toXML() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<playlist>");
        for(Song song : thePlaylist) {
            xmlBuilder.append(song.toXML());
        }
        xmlBuilder.append("</playlist>");
        String xml = xmlBuilder.toString().replaceAll("&", "&amp;");
        return xml;
    }

    public void updateDbTable(Database database) {
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn);

            // Drop the table and recreate to clear the table. There's probably a better way to do this...
            db.execute("DROP TABLE PLAYLIST");
            db.execute(CREATE_PLAYLIST_TABLE);

            for (Song song : thePlaylist) {
                db.insertInto(PLAYLIST, PLAYLIST.FILEPATH).values(song.getFile().getPath()).returning().fetch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Song> getObservableList() {
        return thePlaylist;
    }
}
