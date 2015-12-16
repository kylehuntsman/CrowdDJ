package com.github.funnygopher.crowddj.player;

import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.voting.VotingBooth;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Random;
import java.util.Stack;

public class Player {

    private final Playlist mPlaylist;
    private double mVolume = .5;
    private VotingBooth<Song> mVotingBooth;

    private BooleanProperty mPlaying, mPaused, mStopped;
    private BooleanProperty mShuffle, mLoop;
    private DoubleProperty mCurrentTime, mDuration;

    private ObjectProperty<Song> mCurrentSong;
    private ChangeListener<MediaPlayer.Status> mStatusChangeListener;
    private ChangeListener<Duration> mCurrentTimeChangeListener;
    private ChangeListener<Duration> mTotalDurationChangeListener;
    private Runnable mOnEndOfMedia;

    public Player(Playlist playlist, VotingBooth votingBooth) {
        this.mPlaylist = playlist;
        this.mVotingBooth = votingBooth;

        mPlaying = new SimpleBooleanProperty(this, "playing", false);
        mPaused = new SimpleBooleanProperty(this, "paused", false);
        mStopped = new SimpleBooleanProperty(this, "stopped", true);

        mShuffle = new SimpleBooleanProperty(this, "shuffle", false);
        mLoop = new SimpleBooleanProperty(this, "loop", true);
        mCurrentTime = new SimpleDoubleProperty(this, "currentTime", 0);
        mDuration = new SimpleDoubleProperty(this, "duration", 0);

        mCurrentSong = new SimpleObjectProperty<>(this, "currentSong", null);

        // Sets up the listeners for preparing a song
        mStatusChangeListener = (observable, oldStatus, newStatus) -> {
            mPlaying.set(newStatus == MediaPlayer.Status.PLAYING);
            mPaused.set(newStatus == MediaPlayer.Status.PAUSED);
            mStopped.set(newStatus == MediaPlayer.Status.STOPPED);
        };
        mCurrentTimeChangeListener = (observable, oldTime, newTime) -> {
            mCurrentTime.set(newTime.toSeconds());
        };
        mTotalDurationChangeListener = (observable, oldTime, newTime) -> {
            mDuration.set(newTime.toSeconds());
        };
        mOnEndOfMedia = () -> {
            next();
        };

        // Prepares a song for playback in the player
        mCurrentSong.addListener(((change, oldSong, newSong) -> {
            if (newSong == null) {
                return;
            }
            newSong.prepare(mStatusChangeListener, mCurrentTimeChangeListener, mTotalDurationChangeListener, mVolume,
                    mOnEndOfMedia);
            mDuration.set(newSong.getDuration());
        }));
    }

    // Playback Functionality

    public void play() {
        if(mPlaylist.size() == 0)
            return;

        // If there is no current song, play the next one
        if(mCurrentSong.get() == null) {
            next();
            return;
        }

        mCurrentSong.get().play();
    }

    public void pause() {
        if(mCurrentSong.get() == null)
            return;

        mCurrentSong.get().pause();
    }

    public void stop() {
        if(mCurrentSong.get() == null)
            return;

        mCurrentSong.get().stop();
    }

    public void next() {
        if(mPlaylist.size() == 0)
            return;

        // Stops the song, which will reset that songs time to 0 in the case it's played again
        if(mCurrentSong.get() != null) {
            mCurrentSong.get().stop();
        }

        mCurrentSong.set(getNextSong());
        play();
    }

    public void previous() {
        // TODO: Add previous functionality
    }

    public void loop() {
        mLoop.set(!mLoop.get());
    }

    public void shuffle() {
        mShuffle.set(!mShuffle.get());
    }

    public void setSong(Song song) {
        mCurrentSong.set(song);
    }

    // Returns the next song in the playlist, accounting for looping and shuffling
    private Song getNextSong() {
        Song song;

        if(mVotingBooth.isActive() && mVotingBooth.getNumberOfVotes() > 0) {
            song = mVotingBooth.tallyVotesNoTies();
        } else {
            song = mPlaylist.getNextItem(mCurrentSong.get());

            if (song == null) {
                if (mLoop.getValue()) {
                    song = mPlaylist.getItem(0);
                }
            }

            if (mShuffle.getValue() && mPlaylist.size() > 1) {
                Stack<Integer> indexes = new Stack<>();
                for(int i = 0; i < mPlaylist.size(); i++)
                    indexes.push(i);
                shuffleBag(indexes);

                int newIndex = indexes.pop();
                if(currentSongProperty().get() != null) {
                    int currIndex = mPlaylist.indexOf(mCurrentSong.get());
                    if(currIndex == newIndex)
                        newIndex = indexes.pop();
                }

                song = mPlaylist.getItem(newIndex);
            }
        }

        return song;
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


    // Properties for the GUI
    public ReadOnlyBooleanProperty shuffleProperty() {
        return mShuffle;
    }

    public ReadOnlyBooleanProperty loopProperty() {
        return mLoop;
    }

    public ReadOnlyDoubleProperty currentTimeProperty() {
        return mCurrentTime;
    }

    public ReadOnlyDoubleProperty durationProperty() {
        return mDuration;
    }

    public ObjectProperty<Song> currentSongProperty() {
        return mCurrentSong;
    }

    public ReadOnlyBooleanProperty playingProperty() {
        return mPlaying;
    }

    public ReadOnlyBooleanProperty pausedProperty() {
        return mPaused;

    }

    public ReadOnlyBooleanProperty stoppedProperty() {
        return mStopped;
    }


    public interface Preparable {
        void prepare(ChangeListener<MediaPlayer.Status> statusListener, ChangeListener<Duration> currTimeListener,
                     ChangeListener<Duration> durationListener, double volume, Runnable endOfMedia);
    }
}