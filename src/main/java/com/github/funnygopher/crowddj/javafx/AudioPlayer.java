package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.SearchParty;
import com.github.funnygopher.crowddj.Song;
import com.github.funnygopher.crowddj.exceptions.SongCreationException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.List;
import java.util.Random;

public class AudioPlayer {

    CrowdDJController controller;

    private List<Song> playlist;
    private Song currSong;
    private MediaPlayer currPlayer;
    private double volume = .5;

    private BooleanProperty shuffle, loop;

    private Runnable onButtonPress;

    public AudioPlayer(CrowdDJController controller, List<Song> playlist) {
        this.controller = controller;
        this.playlist = playlist;
        currSong = null;
        currPlayer = null;

        loop = new SimpleBooleanProperty(this, "loop", true);
        shuffle = new SimpleBooleanProperty(this, "shuffle", false);

        onButtonPress = () -> {
            controller.playbackManager.updatePlaybackButtons(currPlayer.getStatus());
            controller.menuManager.updatePlaybackButtons(currPlayer.getStatus());
        };
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    // Adds a song to the song list
    public void add(File file) {
        try {
            Song song = new Song(file);
            add(song);
        } catch(SongCreationException e) {
            e.printError(e.getMessage());
        }
    }

    public void add(Song song) {
        if(!playlist.contains(song)) {
            playlist.add(song);
        }
    }

    public void clearPlaylist() {
        playlist.clear();
    }

    public void play() {
        if(currPlayer == null) {
            final MediaPlayer newPlayer = getNextMediaPlayer();
            playMediaPlayer(newPlayer);
        } else {
            playMediaPlayer(currPlayer);
        }
    }

    public void play(Song song) {
        final MediaPlayer nextPlayer = createPlayer(song);
        currSong = song;

        if(currPlayer != null) {
            currPlayer.stop();
        }
        playMediaPlayer(nextPlayer);
    }

    private void preparePlayer(MediaPlayer player) {
        player.setVolume(volume);

        player.setOnError(() -> {
            System.err.println("Media error occurred: " + player.getError());
            player.getError().printStackTrace();
        });
        player.setOnPlaying(onButtonPress);
        player.setOnPaused(onButtonPress);
        player.setOnStopped(onButtonPress);
        player.setOnEndOfMedia(() -> {
            final MediaPlayer nextPlayer = getNextMediaPlayer();
            if (currPlayer != null) {
                currPlayer.stop();
            }
            playMediaPlayer(nextPlayer);
        });
    }

    public void pause() {
        if(currPlayer == null)
            return;

        currPlayer.pause();
    }

    // Returns the next song in the playlist, accounting for looping and shuffling
    public MediaPlayer getNextMediaPlayer() {
        Song newSong;
        int nextIndex = playlist.indexOf(currSong) + 1;
        if(nextIndex >= playlist.size()) {
            if(loop.getValue()) {
                newSong = playlist.get(0);
            } else {
                newSong = null;
            }
        } else {
            newSong = playlist.get(nextIndex);
        }

        if(shuffle.getValue()) {
            Random rand = new Random(System.currentTimeMillis());
            do {
                newSong = playlist.get(rand.nextInt(playlist.size()));
            } while(currSong == newSong);
        }

        currSong = newSong;
        return createPlayer(currSong);
    }

    public void next() {
        final MediaPlayer nextPlayer = getNextMediaPlayer();

        if(currPlayer != null) {
            currPlayer.stop();
        }
        playMediaPlayer(nextPlayer);
    }

    private void playMediaPlayer(MediaPlayer player) {
        player.play();
        currPlayer = player;
        controller.setSongInformation(currSong);
    }

    private MediaPlayer createPlayer(Song song) {
        final Media media = new Media(song.getFileURI());
        final MediaPlayer player = new MediaPlayer(media);

        preparePlayer(player);
        return player;
    }

    public void stop() {
        if(currPlayer == null)
            return;

        currPlayer.stop();
    }

    public SearchParty<Song> search(File file) {
        for(Song song : playlist) {
            if(song.getFile().equals(file)) {
                return new SearchParty<Song>(song);
            }
        }

        return new SearchParty<Song>();
    }

    public void shuffle() {
        shuffle.set(!shuffle.get());
    }

    public void loop() {
        loop.set(!loop.get());
    }

    public int vote(Song song) {
        return song.vote();
    }

    public Song getCurrentSong() {
        return currSong;
    }

    public MediaPlayer getCurrentMediaPlayer() {
        return currPlayer;
    }

    public MediaPlayer.Status getStatus() {
        return currPlayer == null ? MediaPlayer.Status.STOPPED : currPlayer.getStatus();
    }

    public BooleanProperty shuffleProperty() {
        return shuffle;
    }

    public BooleanProperty loopProperty() {
        return loop;
    }
}