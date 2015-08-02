package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.media.MediaPlayer;

public class MenuManager {

    CrowdDJ crowdDJ;
    CrowdDJController controller;
    PlaybackManager playbackManager;
    AudioPlayer player;

    private MenuItem playpause, stop, next;
    private MenuItem addFiles, clearPlaylist;
    private CheckMenuItem shuffle, showPlaylist;

    public MenuManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;
        this.playbackManager = controller.getPlaybackManager();
        this.player = crowdDJ.getPlayer();

        this.playpause = controller.miPlayPause;
        this.stop = controller.miStop;
        this.next = controller.miNext;

        this.addFiles = controller.miAddFiles;

        this.shuffle = controller.cmiShuffle;
        this.showPlaylist = controller.cmiShowPlaylist;

        playpause.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN));
        stop.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        next.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT));
        shuffle.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHIFT_DOWN));
        showPlaylist.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

        playpause.setOnAction(playbackManager.PLAY);
        stop.setOnAction(playbackManager.STOP);
        next.setOnAction(playbackManager.NEXT);
        shuffle.setOnAction(playbackManager.SHUFFLE);

        player.shuffleProperty().addListener((observable, oldValue, newValue) -> {
            shuffle.setSelected(newValue);
        });

        playpause.setOnAction(event -> {
            if(player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                playbackManager.pause();
            } else if (player.getStatus().equals(MediaPlayer.Status.PAUSED) || player.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                playbackManager.play();
            }
        });

        showPlaylist.selectedProperty().set(true);
        showPlaylist.selectedProperty().addListener((observable, oldValue, newValue) -> {
            controller.pMusicList.setVisible(newValue);
        });
    }

    public void updatePlaybackButtons(MediaPlayer.Status status) {
        if(status == MediaPlayer.Status.PLAYING) {
            playpause.setText("Pause");
        }
        if(status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED) {
            playpause.setText("Play");
        }
    }
}
