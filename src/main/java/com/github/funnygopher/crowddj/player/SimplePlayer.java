package com.github.funnygopher.crowddj.player;

import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.voting.VotingBooth;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Random;
import java.util.Stack;

public class SimplePlayer implements Player {

    private final Playlist playlist;
    private double volume = .5;
    private VotingBooth<Song> votingBooth;

    private BooleanProperty playing, paused, stopped;
    private BooleanProperty shuffle, loop;
    private DoubleProperty currentTime, duration;

    private ObjectProperty<Song> currentSong;
    private ObjectProperty<MediaPlayer> currentPlayer;

    public SimplePlayer(Playlist playlist, VotingBooth votingBooth) {
        this.playlist = playlist;
        this.votingBooth = votingBooth;

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
        Song song;

        if(votingBooth.isActive() && votingBooth.getNumberOfVotes() > 0) {
            song = votingBooth.tallyVotesNoTies();
        } else {
            song = playlist.getNextItem(currentSong.get());

            if (song == null) {
                if (loop.getValue()) {
                    song = playlist.getItem(0);
                }
            }

            if (shuffle.getValue() && playlist.size() > 1) {
                Stack<Integer> indexes = new Stack<>();
                for(int i = 0; i < playlist.size(); i++)
                    indexes.push(i);
                shuffleBag(indexes);

                int newIndex = indexes.pop();
                if(currentSongProperty().get() != null) {
                    int currIndex = playlist.indexOf(currentSong.get());
                    if(currIndex == newIndex)
                        newIndex = indexes.pop();
                }

                song = playlist.getItem(newIndex);
            }
        }

        currentSong.set(song);
        return createPlayer(currentSong.get());
    }

    private MediaPlayer createPlayer(Song song) {
        final Media media = new Media(song.getFileURI());
        final MediaPlayer player = new MediaPlayer(media);

        player.setOnEndOfMedia(() -> next());

        return player;
    }

    private void shuffleBag(Stack<Integer> ar) {
        Random rnd = new Random(System.currentTimeMillis());
        for (int i = ar.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = ar.get(index);
            ar.set(index, ar.get(i));
            ar.set(i, a);
        }
    }
}