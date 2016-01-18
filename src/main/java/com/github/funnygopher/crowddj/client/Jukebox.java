package com.github.funnygopher.crowddj.client;

import com.github.funnygopher.crowddj.client.playlist.QueuePlaylist;
import com.github.funnygopher.crowddj.client.song.Song;
import com.github.funnygopher.crowddj.client.player.Player;
import com.github.funnygopher.crowddj.client.playlist.Playlist;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Random;
import java.util.Stack;

public class Jukebox {

    private MusicLibrary mMusicLibrary;
    private Player mPlayer;
    private Playlist mPlaylist;

    private BooleanProperty mLoopProperty;
    private BooleanProperty mShuffleProperty;

    private DoubleProperty mVolumeProperty;

    private final Runnable ON_END_OF_MEDIA = () -> next();

    public Jukebox(MusicLibrary musicLibrary) {
        mMusicLibrary = musicLibrary;
        mPlayer = new Player();
        mPlaylist = new QueuePlaylist(musicLibrary.getSongs());

        initProperties();
    }

    private void initProperties() {
        mLoopProperty = new SimpleBooleanProperty(this, "loop", true);
        mShuffleProperty = new SimpleBooleanProperty(this, "shuffle", false);

        mVolumeProperty = new SimpleDoubleProperty(this, "volume", 0.5);
    }

    /*******************************
     * Playback Functionality
     *******************************/

    public void play() {
        if(mPlayer.isEmpty()) {
            next();
        } else {
            mPlayer.play();
        }
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.stop();
    }

    public void next() {
        Song prevSong = mPlayer.getSong();
        if(!mPlayer.isEmpty()) {
            mPlayer.eject();
        }

        Song song;
        if(!mPlaylist.isEmpty()) {
            song = mPlaylist.getNextSong();
        } else {
            song = mMusicLibrary.getNextSong(prevSong);

            // If looping...
            if(song == null) {
                if(isLooping()) {
                    song = mMusicLibrary.get(0);
                }
            }

            // If shuffling, choose a random song from the music library
            if(isShuffling() && mMusicLibrary.size() > 1) {
                Stack<Integer> indexes = new Stack<>();
                for(int i = 0; i < mMusicLibrary.size(); i++)
                    indexes.push(i);
                shuffleBag(indexes);

                int newIndex = indexes.pop();
                if(!mPlayer.isEmpty()) {
                    Song newSong = mMusicLibrary.get(newIndex);
                    if(newSong == mPlayer.getSong())
                        newIndex = indexes.pop();
                }

                song = mMusicLibrary.get(newIndex);
            }
        }
        prepareSong(song);

        mPlayer.setSong(song);
        mPlayer.play();
    }

    /*******************************
     * Getters and Setters
     *******************************/

    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    public boolean isLooping() {
        return mLoopProperty.get();
    }

    public void setLooping(boolean loop) {
        mLoopProperty.set(loop);
    }

    public boolean isShuffling() {
        return mShuffleProperty.get();
    }

    public void setShuffling(boolean shuffle) {
        mShuffleProperty.set(shuffle);
    }

    public double getVolume() {
        return mVolumeProperty.get();
    }

    public void setVolume(double value) {
        if(value < 0) value = 0;
        if(value > 1) value = 1;
        mVolumeProperty.set(value);
    }

    /*******************************
     * Utility
     *******************************/

    private void prepareSong(Song song) {
        song.getMediaPlayer().setOnEndOfMedia(ON_END_OF_MEDIA);
        song.getMediaPlayer().setVolume(getVolume());
    }

    private void shuffleBag(Stack<Integer> stack) {
        Random rand = new Random(System.currentTimeMillis());
        for (int i = stack.size() - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int a = stack.get(index);
            stack.set(index, stack.get(i));
            stack.set(i, a);
        }
    }

    /*******************************
     * Properties
     *******************************/

    public BooleanProperty getLoopingProperty() {
        return mLoopProperty;
    }

    public BooleanProperty getShufflingProperty() {
        return mShuffleProperty;
    }

    public DoubleProperty getVolumeProperty() {
        return mVolumeProperty;
    }
}
