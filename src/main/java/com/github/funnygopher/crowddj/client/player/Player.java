package com.github.funnygopher.crowddj.client.player;

import com.github.funnygopher.crowddj.client.song.Song;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Player {

    private BooleanProperty mPlayingProperty, mPausedProperty, mStoppedProperty;
    private DoubleProperty mTimeProperty, mDurationProperty;

    private ObjectProperty<Song> mSongProperty;
    private ChangeListener<MediaPlayer.Status> mStatusChangeListener;
    private ChangeListener<Duration> mCurrentTimeChangeListener;
    private ChangeListener<Duration> mTotalDurationChangeListener;

    public Player() {
        initProperties();

        // Sets up the listeners for preparing a song
        mStatusChangeListener = (observable, oldStatus, newStatus) -> {
            mPlayingProperty.set(newStatus == MediaPlayer.Status.PLAYING);
            mPausedProperty.set(newStatus == MediaPlayer.Status.PAUSED);
            mStoppedProperty.set(newStatus == MediaPlayer.Status.STOPPED);
        };
        mCurrentTimeChangeListener = (observable, oldTime, newTime) -> {
            mTimeProperty.set(newTime.toSeconds());
        };
        mTotalDurationChangeListener = (observable, oldTime, newTime) -> {
            mDurationProperty.set(newTime.toSeconds());
        };

        // Prepares a song for playback in the player
        mSongProperty.addListener(((change, oldSong, newSong) -> {
            if (newSong == null) {
                return;
            }
            newSong.prepare(mStatusChangeListener, mCurrentTimeChangeListener, mTotalDurationChangeListener);
            mDurationProperty.set(newSong.getDuration());
        }));
    }

    private void initProperties() {
        mPlayingProperty = new SimpleBooleanProperty(this, "playing", false);
        mPausedProperty = new SimpleBooleanProperty(this, "paused", false);
        mStoppedProperty = new SimpleBooleanProperty(this, "stopped", true);
        mTimeProperty = new SimpleDoubleProperty(this, "currentTime", 0);
        mDurationProperty = new SimpleDoubleProperty(this, "duration", 0);
        mSongProperty = new SimpleObjectProperty<>(this, "song", null);
    }

    /*******************************
     * Playback Functionality
     *******************************/

    public void play() {
        if(isEmpty())
            return;

        mSongProperty.get().play();
    }

    public void pause() {
        if(isEmpty())
            return;

        mSongProperty.get().pause();
    }

    public void stop() {
        if(isEmpty())
            return;

        mSongProperty.get().stop();
    }

    public void eject() {
        if (isEmpty())
            return;

        stop();
        mSongProperty.set(null);
    }

    /*******************************
     * Getters and Setters
     *******************************/

    public boolean isPlaying() {
        return mPlayingProperty.get();
    }

    public boolean isPaused() {
        return mPausedProperty.get();
    }

    public boolean isStopped() {
        return mStoppedProperty.get();
    }

    public double getTime() {
        return mTimeProperty.get();
    }

    public double getDuration() {
        return mDurationProperty.get();
    }

    public Song getSong() {
        return mSongProperty.get();
    }

    public void setSong(Song song) {
        mSongProperty.set(song);
    }

    /*******************************
     * Properties
     *******************************/

    public ReadOnlyDoubleProperty getTimeProperty() {
        return mTimeProperty;
    }

    public ReadOnlyDoubleProperty getDurationProperty() {
        return mDurationProperty;
    }

    public ObjectProperty<Song> getSongProperty() {
        return mSongProperty;
    }

    public ReadOnlyBooleanProperty getPlayingProperty() {
        return mPlayingProperty;
    }

    public ReadOnlyBooleanProperty getPausedProperty() {
        return mPausedProperty;
    }

    public ReadOnlyBooleanProperty getStoppedProperty() {
        return mStoppedProperty;
    }

    /*******************************
     * Utility
     *******************************/

    public boolean isEmpty() {
        return mSongProperty.get() == null;
    }

    public interface Preparable {
        void prepare(ChangeListener<MediaPlayer.Status> statusListener, ChangeListener<Duration> currTimeListener,
                     ChangeListener<Duration> durationListener);
    }
}