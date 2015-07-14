package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.StatusObserver;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class MenuManager implements StatusObserver {

    CrowdDJ crowdDJ;
    CrowdDJController controller;
    PlaybackManager playbackManager;

    private MenuItem playpause, stop, next;
    private CheckMenuItem shuffle, showPlaylist;

    public MenuManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;
        this.playbackManager = controller.getPlaybackManager();

        this.playpause = controller.miPlayPause;
        this.stop = controller.miStop;
        this.next = controller.miNext;
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

        playpause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                VLCStatus status = crowdDJ.getStatusManager().getStatus();
                if(status.isPlaying()) {
                    playpause.setText("Pause");
                    playbackManager.pause();
                }

                if(status.isPaused() || status.isStopped()) {
                    playpause.setText("Play");
                    playbackManager.play();
                }
            }
        });

        showPlaylist.selectedProperty().set(true);
        showPlaylist.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                controller.pMusicList.setVisible(newValue);
            }
        });
    }

    private void updateMenuLabels(VLCStatus status) {
        if(status.isPlaying()) {
            playpause.setText("Pause");
        }
        if(status.isPaused() || status.isStopped()) {
            playpause.setText("Play");
        }
    }

    @Override
    public void update(VLCStatus status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(status.isConnected()) {
                    updateMenuLabels(status);
                }
            }
        });
    }
}
