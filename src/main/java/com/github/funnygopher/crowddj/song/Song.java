package com.github.funnygopher.crowddj.song;

import com.github.funnygopher.crowddj.database.Entity;
import com.github.funnygopher.crowddj.player.Player;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Song implements Player.Preparable, Entity {

    private MediaPlayer mMediaPlayer;
	private SongInfo mSongInfo;

    private Long mId; // The id of the song in the database

	public Song(File file) throws SongCreationException {
        if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

        // Gets all the information about the song
		String filePath = file.getAbsolutePath();
        mSongInfo = new SongInfo(filePath);

        // Creates a media file to manipulate the playback of the song
        String fileUri;
        try {
            fileUri = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20").replaceAll("&", "%26");
        } catch (UnsupportedEncodingException e) {
            fileUri = String.valueOf(file.toURI());
        }
        Media media = new Media("file:///" + fileUri);
        mMediaPlayer = new MediaPlayer(media);
	}

    /*******************************
     * Playback Functionality
     *******************************/

    public void play() {
        mMediaPlayer.play();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void dispose() {
        mMediaPlayer.dispose();
    }

    /*******************************
     * Getters and Setters
     *******************************/

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public SongInfo getInfo() {
        return mSongInfo;
    }

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public void setId(Long id) {
        mId = id;
    }

    public double getDuration() {
        return mMediaPlayer.getTotalDuration().toSeconds();
    }

    /*******************************
     * Utility
     *******************************/

    @Override
    public void prepare(ChangeListener<MediaPlayer.Status> statusListener, ChangeListener<Duration> currTimeListener, ChangeListener<Duration> durationListener) {
        mMediaPlayer.setOnError(() -> {
            System.err.println("Media error occurred: " + mMediaPlayer.getError());
            mMediaPlayer.getError().printStackTrace();
        });

        mMediaPlayer.statusProperty().addListener(statusListener);
        mMediaPlayer.currentTimeProperty().addListener(currTimeListener);
        mMediaPlayer.totalDurationProperty().addListener(durationListener);
    }

    public String toXML() {
        String xmlString = "<song>" +
                "<id>" + getId() + "</id>" +
                "<title>" + mSongInfo.getTitle() + "</title>" +
                "<artist>" + mSongInfo.getArtist() + "</artist>" +
                "<file>" + mSongInfo.getFilePath() + "</file>" +
                "</song>";

        return xmlString;
    }

    public String toJson() {
        return "{" +
                "\"id\":" + getId() + "," +
                "\"title\":\"" + mSongInfo.getTitle() + "\"," +
                "\"artist\":\"" + mSongInfo.getArtist() + "\"," +
                "\"file\":\"" + mSongInfo.getFilePath() + "\"" +
                "}";
    }
}
