package com.github.funnygopher.crowddj.player;

import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer implements Player {

    private final Playlist playlist;
    private double volume = .5;

    private BooleanProperty playing, paused, stopped;
    private BooleanProperty shuffle, loop;
    private DoubleProperty currentTime, duration;

    private ObjectProperty<Song> currentSong;
    private ObjectProperty<MediaPlayer> currentPlayer;

    public AudioPlayer(Playlist playlist) {
        this.playlist = playlist;

        playing = new SimpleBooleanProperty(this, "playing", false);
        paused = new SimpleBooleanProperty(this, "paused", false);
        stopped = new SimpleBooleanProperty(this, "stopped", true);

        shuffle = new SimpleBooleanProperty(this, "shuffle", false);
        loop = new SimpleBooleanProperty(this, "loop", true);
        currentTime = new SimpleDoubleProperty(this, "currentTime", 0);
        duration = new SimpleDoubleProperty(this, "duration", 0);

        currentSong = new SimpleObjectProperty<Song>(this, "currentSong");

        // Sets up the current media player, and what to do when the media player changes
        currentPlayer = new SimpleObjectProperty<>(this, "currentPlayer");
        currentPlayer.addListener(((change, oldPlayer, newPlayer) -> {
            if(newPlayer == null)
                return;

            newPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                playing.set(newStatus == MediaPlayer.Status.PLAYING);
                paused.set(newStatus == MediaPlayer.Status.PAUSED);
                stopped.set(newStatus == MediaPlayer.Status.STOPPED);
            });

            newPlayer.setVolume(volume);
            newPlayer.setOnError(() -> {
                System.err.println("Media error occurred: " + newPlayer.getError());
                newPlayer.getError().printStackTrace();
            });
            newPlayer.setOnEndOfMedia(() -> {
                next();
            });

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

        play();
    }

    public void loop() {
        loop.set(!loop.get());
    }

    public void shuffle() {
        shuffle.set(!shuffle.get());
    }

    public void reset() {
        if(currentPlayer.get() != null) {
            currentPlayer.get().stop();
            currentPlayer.get().dispose();
        }
        currentPlayer.set(null);
        currentSong.set(null);
    }

    public ReadOnlyBooleanProperty shuffleProperty() {
        return shuffle;
    }

    public ReadOnlyBooleanProperty loopProperty() {
        return loop;
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


    public ReadOnlyBooleanProperty playingProperty() {
        return playing;
    }

    public ReadOnlyBooleanProperty pausedProperty() {
        return paused;

    }

    public ReadOnlyBooleanProperty stoppedProperty() {
        return stopped;
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