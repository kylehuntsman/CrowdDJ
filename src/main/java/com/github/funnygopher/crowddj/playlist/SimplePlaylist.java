package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.database.DatabaseManager;
import com.github.funnygopher.crowddj.util.SearchParty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static com.github.funnygopher.crowddj.database.jooq.Tables.PLAYLIST;

public class SimplePlaylist implements Playlist {

    private ObservableList<Song> mPlaylist;
    public final String CREATE_PLAYLIST_TABLE =
            "CREATE TABLE IF NOT EXISTS PLAYLIST (" +
                    "ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "FILEPATH VARCHAR(255) NOT NULL" +
                    ");";

    public SimplePlaylist(List<Song> playlist) {
        this.mPlaylist = FXCollections.observableArrayList(playlist);
        populateFromDatabase();
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

        if(!mPlaylist.contains(song)) {
            mPlaylist.add(song);
        }
    }

    public void remove(Song song) {
        mPlaylist.remove(song);
    }

    public void clear() {
        mPlaylist.clear();
    }

    public int size() {
        return mPlaylist.size();
    }

    public SearchParty<Song> search(File file) {
        for(Song song : mPlaylist) {
            if(song.getFilePath().equals(file.getAbsolutePath())) {
                return new SearchParty<Song>(song);
            }
        }
        return new SearchParty<Song>();
    }

    public String toXML() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<playlist>");
        for(Song song : mPlaylist) {
            xmlBuilder.append(song.toXML());
        }
        xmlBuilder.append("</playlist>");
        String xml = xmlBuilder.toString().replaceAll("&", "&amp;");
        return xml;
    }

    public void updateDatabaseTable() {
        DatabaseManager database = CrowdDJ.getDatabase();
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn);

            // Drop the table and recreate to clear the table. There's probably a better way to do this...
            db.execute("DROP TABLE PLAYLIST");
            db.execute(CREATE_PLAYLIST_TABLE);

            for (Song song : mPlaylist) {
                db.insertInto(PLAYLIST, PLAYLIST.FILEPATH).values(song.getFilePath()).returning().fetch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Song> getItems() {
        return mPlaylist;
    }

    public Song getNextItem(Song song) {
        if(song == null) {
            return mPlaylist.get(0);
        }

        int nextIndex = mPlaylist.indexOf(song) + 1;
        if(nextIndex >= mPlaylist.size()) {
            return null;
        } else {
            return mPlaylist.get(nextIndex);
        }
    }

    public Song getRandomItem() {
        Random rand = new Random(System.currentTimeMillis());
        return mPlaylist.get(rand.nextInt(mPlaylist.size()));
    }

    public Song getItem(int index) {
        return mPlaylist.get(index);
    }

    public int indexOf(Song song) {
        return mPlaylist.indexOf(song);
    }

    private void populateFromDatabase() {
        DatabaseManager database = CrowdDJ.getDatabase();

        // Takes each song saved in the PLAYLIST table and adds it to the player's playlist
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn, SQLDialect.H2);
            Result<Record> results = db.select().from(PLAYLIST).fetch();

            for (Record result : results) {
                String filepath = result.getValue(PLAYLIST.FILEPATH);
                File file = new File(filepath);
                if(!file.exists())
                    return;

                try {
                    Song song = new Song(file);
                    mPlaylist.add(song);
                } catch (SongCreationException e) {
                    e.printError(e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        for (Song song : mPlaylist) {
            song.dispose();
        }
    }
}
