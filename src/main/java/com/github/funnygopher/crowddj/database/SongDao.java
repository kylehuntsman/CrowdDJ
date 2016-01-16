package com.github.funnygopher.crowddj.database;

import com.github.funnygopher.crowddj.song.Song;
import com.github.funnygopher.crowddj.song.SongCreationException;

import java.io.File;
import java.sql.*;

public class SongDao extends AbstractJooqDao<Song> {

    public SongDao(DatabaseManager databaseManager) {
        super(Song.class, databaseManager);
    }

    @Override
    public Song create(Song song) {
        String sql = "INSERT INTO SONG(FILE_PATH) VALUES(?)";
        try (
            Connection conn = mDatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, song.getInfo().getFilePath());

            int affectedRow = statement.executeUpdate();
            if(affectedRow == 0) {
                throw new SQLException("Creating song failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    song.setId(generatedKeys.getLong("id"));
                } else {
                    throw new SQLException("Creating song failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Song get(Long id) {
        String sql = "SELECT FILE_PATH FROM SONG WHERE ID = ?";
        try (
            Connection conn = mDatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            statement.setLong(1, id);

            try (ResultSet results = statement.executeQuery(sql)) {
                if(results.next()) {
                    String filePath = results.getString("file_path");
                    File file = new File(filePath);
                    return new Song(file);
                } else {
                    throw new SQLException("Fetching song failed, no record with ID of " + id + " found.");
                }
            } catch (SongCreationException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(Song song) {
        String sql = "UPDATE SONG SET FILE_PATH = ? WHERE ID = ?";
        try (
            Connection conn = mDatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            statement.setString(1, song.getInfo().getFilePath());
            statement.setLong(2, song.getId());

            int affectedRow = statement.executeUpdate();
            if(affectedRow == 0) {
                throw new SQLException("Updating song failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Song song) {
        String sql = "DELETE SONG WHERE ID = ?";
        try (
            Connection conn = mDatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            statement.setLong(1, song.getId());

            int affectedRow = statement.executeUpdate();
            if(affectedRow == 0) {
                throw new SQLException("Deleting song failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
