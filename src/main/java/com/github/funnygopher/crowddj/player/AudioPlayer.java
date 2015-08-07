package com.github.funnygopher.crowddj.player;

import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer implements Player {

    private final Playlist playlist;
    private double volume = .5;

    private BooleanProperty shuffle, loop;
    private DoubleProperty currentTime, duration;

    private ObjectProperty<Song> currentSong;
    private ObjectProperty<MediaPlayer> currentPlayer;

    private Runnable onPlay, onPause, onStop, onNext;

    public AudioPlayer(Playlist playlist) {
        this.playlist = playlist;

        loop = new SimpleBooleanProperty(this, "loop", true);
        shuffle = new SimpleBooleanProperty(this, "shuffle", false);
        currentTime = new SimpleDoubleProperty(this, "currentTime", 0);
        duration = new SimpleDoubleProperty(this, "duration", 0);

        currentSong = new SimpleObjectProperty<Song>(this, "currentSong");

        // Sets up the current media player, and what to do when the media player changes
        currentPlayer = new SimpleObjectProperty<>(this, "currentPlayer");
        currentPlayer.addListener(((change, oldPlayer, newPlayer) -> {
            if(newPlayer == null)
                return;

            newPlayer.setVolume(volume);
            newPlayer.setOnError(() -> {
                System.err.println("Media error occurred: " + newPlayer.getError());
                newPlayer.getError().printStackTrace();
            });
            newPlayer.setOnEndOfMedia(() -> {
                next();
            });
            newPlayer.setOnPlaying(onPlay);
            newPlayer.setOnPaused(onPause);
            newPlayer.setOnStopped(onStop);

            // Synchronizes the media player's current time with this player's current time
            newPlayer.currentTimeProperty().addListener((duration, oldValue, newValue) -> {
                currentTime.set(newValue.toSeconds());
            });
            newPlayer.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
                duration.set(newValue.toSeconds());
            });
        }));
    }

    public void play() {
        if(currentPlayer.get() == null) {
            currentPlayer.set(getNextMediaPlayer());
        }
        currentPlayer.get().play();
    }

    public void play(Song song) {
        final MediaPlayer nextPlayer = createPlayer(song);
        currentSong.set(song);

        if(currentPlayer.get() != null) {
            currentPlayer.get().stop();
        }
        currentPlayer.set(nextPlayer);
        play();
    }

    public void pause() {
        if(currentPlayer.get() == null)
            return;

        currentPlayer.get().pause();
    }

    public void stop() {
        if(currentPlayer.get() == null)
            return;

        currentPlayer.get().stop();
    }

    public void next() {
        final MediaPlayer nextPlayer = getNextMediaPlayer();

        if(currentPlayer.get() != null) {
            currentPlayer.get().stop();
        }
        currentPlayer.set(nextPlayer);

        if(onNext != null) {
            onNext.run();
        }

        play();
    }

    public void loop() {
        loop.set(!loop.get());
    }

    public void shuffle() {
        shuffle.set(!shuffle.get());
    }

    private MediaPlayer.Status getStatus() {
        return currentPlayer == null ? MediaPlayer.Status.STOPPED : currentPlayer.get().getStatus();
    }

    public void reset() {
        if(currentPlayer.get() != null) {
            currentPlayer.get().stop();
            currentPlayer.get().dispose();
        }
        currentPlayer.set(null);
        currentSong.set(null);
    }

    public void setOnPlay(Runnable runnable) {
        onPlay = runnable;
    }
    public void setOnPause(Runnable runnable) {
        onPause = runnable;
    }
    public void setOnStop(Runnable runnable) {
        onStop = runnable;
    }

    public void setOnNext(Runnable runnable) {
        onNext = runnable;
    }

    public ReadOnlyBooleanProperty loopProperty() {
        return loop;
    }

    public ReadOnlyBooleanProperty shuffleProperty() {
        return shuffle;
    }

    public ReadOnlyDoubleProperty currentTimeProperty() {
        return currentTime;
    }

    public ReadOnlyDoubleProperty durationProperty() {
        return duration;
    }

    public ObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }

    public boolean isPlaying() {
        return currentPlayer.get() != null && currentPlayer.get().getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean isPaused() {
        return currentPlayer.get() != null && currentPlayer.get().getStatus() == MediaPlayer.Status.PAUSED;
    }

    public boolean isStopped() {
        return currentPlayer.get() != null && currentPlayer.get().getStatus() == MediaPlayer.Status.STOPPED;
    }

    // Returns the next song in the playlist, accounting for looping and shuffling
    private MediaPlayer getNextMediaPlayer() {
        Song newSong = playlist.getNextItem(currentSong.get());

        if(newSong == null) {
            if (loop.getValue()) {
                newSong = playlist.getItem(0);
            }
        }

        if(shuffle.getValue()) {
            do {
                newSong = playlist.getRandomItem();
            } while(currentSong.get() == newSong);
        }

        currentSong.set(newSong);
        return createPlayer(currentSong.get());
    }

    private MediaPlayer createPlayer(Song song) {
        final Media media = new Media(song.getFileURI());
        final MediaPlayer player = new MediaPlayer(media);
        return player;
    }
}