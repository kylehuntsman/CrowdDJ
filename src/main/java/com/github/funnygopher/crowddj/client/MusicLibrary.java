package com.github.funnygopher.crowddj.client;

import com.github.funnygopher.crowddj.client.database.DatabaseManager;
import com.github.funnygopher.crowddj.client.database.SongDao;
import com.github.funnygopher.crowddj.client.song.Song;
import com.github.funnygopher.crowddj.client.song.SongCreationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MusicLibrary {

    private ObservableList<Song> mSongList;
    private DatabaseManager mDatabaseManager;
    private SongDao mSongDao;

    public MusicLibrary(DatabaseManager databaseManager) {
        mSongList = FXCollections.observableArrayList();
        mDatabaseManager = databaseManager;
        mSongDao = new SongDao(mDatabaseManager);

        fetchFromDatabase();
    }

    public boolean add(File file) {
        if (file.isDirectory()) {
            for (File fileInDir : file.listFiles()) {
                add(fileInDir);
            }
        } else {
            try {
                Song song = new Song(file);
                if (!mSongList.contains(song)) {
                    mSongList.add(song);
                    mSongDao.create(song);
                }
            } catch (SongCreationException e) {
                e.printError(e.getMessage());
            }
        }

        return true;
    }

    public void remove(Song song) {
        mSongList.remove(song);
        mSongDao.delete(song);
    }

    public void clear() {
        for(Song song : mSongList) {
            mSongDao.delete(song);
        }
        mSongList.clear();
    }

    public int size() {
        return mSongList.size();
    }

    public Song get(int index) {
        return mSongList.get(index);
    }

    public ObservableList<Song> getSongs() {
        return mSongList;
    }

    public Song getNextSong(Song song) {
        if(song == null) {
            return mSongList.get(0);
        }

        int nextIndex = mSongList.indexOf(song) + 1;
        if(nextIndex >= mSongList.size()) {
            return null;
        } else {
            return mSongList.get(nextIndex);
        }
    }

    private void fetchFromDatabase() {
        String sql = "SELECT ID, FILE_PATH FROM SONG";
        try (
                Connection conn = mDatabaseManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            try (ResultSet results = statement.executeQuery(sql)){
                while(results.next()) {
                    Long id = results.getLong("id");
                    String filePath = results.getString("file_path");

                    File file = new File(filePath);
                    if(!file.exists())
                        continue;

                    Song song = new Song(file);
                    song.setId(id);

                    mSongList.add(song);
                }
            } catch (SongCreationException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        for (Song song : mSongList) {
            song.dispose();
        }
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"songs\": [");
        for(int i = 0; i < mSongList.size(); i++) {
            Song song = mSongList.get(i);
            jsonBuilder.append(song.toJson());
            if(i != mSongList.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }
}
