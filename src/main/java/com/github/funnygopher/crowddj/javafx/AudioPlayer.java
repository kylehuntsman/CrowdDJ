package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.playlist.Playlist;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.List;
import java.util.Random;

public class AudioPlayer {

    CrowdDJ crowdDJ;

    private Playlist playlist;
    private Song currSong;
    private MediaPlayer currPlayer;
    private double volume = .5;

    private BooleanProperty shuffle, loop;

    private Runnable onButtonPress;

    public AudioPlayer(CrowdDJ crowdDJ, Playlist playlist) {
        this.crowdDJ = crowdDJ;
        this.playlist = playlist;
        currSong = null;
        currPlayer = null;

        loop = new SimpleBooleanProperty(this, "loop", true);
        shuffle = new SimpleBooleanProperty(this, "shuffle", false);

        onButtonPress = () -> {
            crowdDJ.getController().playbackManager.updatePlaybackButtons(currPlayer.getStatus());
            crowdDJ.getController().menuManager.updatePlaybackButtons(currPlayer.getStatus());
        };
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

    public void pause() {
        if(currPlayer == null)
            return;

        currPlayer.pause();
    }

    public void stop() {
        if(currPlayer == null)
            return;

        currPlayer.stop();
    }

    public void next() {
        final MediaPlayer nextPlayer = getNextMediaPlayer();

        if(currPlayer != null) {
            currPlayer.stop();
        }
        playMediaPlayer(nextPlayer);
    }

    public void shuffle() {
        shuffle.set(!shuffle.get());
    }

    public void loop() {
        loop.set(!loop.get());
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

    public void dispose() {
        if(currPlayer != null) {
            currPlayer.stop();
            currPlayer.dispose();
        }
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

    // Returns the next song in the playlist, accounting for looping and shuffling
    private MediaPlayer getNextMediaPlayer() {
        Song newSong;
        List<Song> playlistItems = playlist.getObservableList();
        int nextIndex = playlistItems.indexOf(currSong) + 1;
        if(nextIndex >= playlistItems.size()) {
            if(loop.getValue()) {
                newSong = playlistItems.get(0);
            } else {
                newSong = null;
            }
        } else {
            newSong = playlistItems.get(nextIndex);
        }

        if(shuffle.getValue()) {
            Random rand = new Random(System.currentTimeMillis());
            do {
                newSong = playlistItems.get(rand.nextInt(playlistItems.size()));
            } while(currSong == newSong);
        }

        currSong = newSong;
        return createPlayer(currSong);
    }

    private void playMediaPlayer(MediaPlayer player) {
        player.play();
        currPlayer = player;
        crowdDJ.getController().setSongInformation(currSong);
    }

    private MediaPlayer createPlayer(Song song) {
        final Media media = new Media(song.getFileURI());
        final MediaPlayer player = new MediaPlayer(media);

        preparePlayer(player);
        return player;
    }
}