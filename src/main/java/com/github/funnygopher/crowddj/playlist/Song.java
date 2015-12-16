package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.voting.Voteable;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Song implements Comparable<Voteable>, Voteable, Player.Preparable {

    private Media mMedia;
    private MediaPlayer mMediaPlayer;

	private SongInformation mSongInfo;
    private IntegerProperty votes;

	public Song(File file) throws SongCreationException {
        if(!file.getName().endsWith(".mp3"))
            throw new SongCreationException(file);

        // Gets all the information about the song
		String filePath = file.getAbsolutePath();
        mSongInfo = new SongInformation(filePath);

        // Creates a media file to manipulate the playback of the song
        String fileUri;
        try {
            fileUri = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20").replaceAll("&", "%26");
        } catch (UnsupportedEncodingException e) {
            fileUri = String.valueOf(file.toURI());
        }
        mMedia = new Media("file:///" + fileUri);
        mMediaPlayer = new MediaPlayer(mMedia);

        votes = new SimpleIntegerProperty(this, "votes", 0);
	}

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

    // TODO: Change to JSON
	public String toXML() {
		String xmlString = "<song>" +
				"<title>" + getTitle() + "</title>" +
				"<artist>" + getArtist() + "</artist>" +
				"<uri>" + getFilePath() + "</uri>" +
				"<votes>" + votes.get() + "</votes>" +
				"</song>";

		return xmlString;
	}

	public void vote() {
		votes.set(votes.get() + 1);
	}

    public void unvote() {
        if(votes.get() > 0) {
            votes.set(votes.get() - 1);
        }
    }

    public void clearVotes() {
        votes.set(0);
    }

    public void changeCoverArt(File file) {
        mSongInfo.changeCoverArt(file);
    }

    @Override
    public int compareTo(Voteable voteable) {
        if(this.votesProperty().get() == voteable.votesProperty().get())
            return 0;

        if(this.votesProperty().get() < voteable.votesProperty().get())
            return -1;

        if(this.votesProperty().get() > voteable.votesProperty().get())
            return 1;

        return 0;
    }

    public String getTitle() {
        return mSongInfo.getTitle();
    }

    public StringProperty titleProperty() {
        return mSongInfo.titleProperty();
    }

    public String getArtist() {
        return mSongInfo.getArtist();
    }

    public StringProperty artistProperty() {
        return mSongInfo.artistProperty();
    }

    public String getAlbum() {
        return mSongInfo.getAlbum();
    }

    public StringProperty albumProperty() {
        return mSongInfo.albumProperty();
    }

    public int getDuration() {
        return mSongInfo.getDuration();
    }

    public IntegerProperty durationProperty() {
        return mSongInfo.durationProperty();
    }

    public Image getCoverArt() {
        return mSongInfo.getCoverArt();
    }

    public ObjectProperty<Image> coverArtProperty() {
        return mSongInfo.coverArtProperty();
    }

    public String getFilePath() {
        return mSongInfo.getFilePath();
    }

    public ReadOnlyIntegerProperty votesProperty() {
        return votes;
    }

    @Override
    public void prepare(ChangeListener<MediaPlayer.Status> statusListener, ChangeListener<Duration> currTimeListener, ChangeListener<Duration> durationListner, double volume, Runnable endOfMedia) {
        mMediaPlayer.setOnError(() -> {
            System.err.println("Media error occurred: " + mMediaPlayer.getError());
            mMediaPlayer.getError().printStackTrace();
        });

        mMediaPlayer.statusProperty().addListener(statusListener);
        mMediaPlayer.currentTimeProperty().addListener(currTimeListener);
        mMediaPlayer.totalDurationProperty().addListener(durationListner);
        mMediaPlayer.setVolume(volume);
    }
}
