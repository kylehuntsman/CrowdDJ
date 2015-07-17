package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.Property;
import com.github.funnygopher.crowddj.StatusObserver;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.application.Platform;
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
    private MenuItem addFiles, clearPlaylist;
    private CheckMenuItem shuffle, showPlaylist;

    public MenuManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;
        this.playbackManager = controller.getPlaybackManager();

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

        playpause.setOnAction(event -> {
            VLCStatus status = crowdDJ.getStatusManager().getStatus();
            if (status.isPlaying()) {
                playpause.setText("Pause");
                playbackManager.pause();
            }

            if (status.isPaused() || status.isStopped()) {
                playpause.setText("Play");
                playbackManager.play();
            }
        });

        showPlaylist.selectedProperty().set(true);
        showPlaylist.selectedProperty().addListener((observable, oldValue, newValue) -> {
            controller.pMusicList.setVisible(newValue);
        });

        String vlcPath = crowdDJ.getProperties().getStringProperty(Property.VLC_PATH);
        controller.txtVLCPath.setText(vlcPath);

        controller.txtVLCPath.textProperty().addListener((observable, oldValue, newValue) -> {
            String alteredValue = newValue.replace("\\", "/");

            boolean validPath = crowdDJ.isValidVLCPath(alteredValue);
            if (validPath)
                crowdDJ.setVLCPath(alteredValue);
            controller.miStartVLC.setDisable(!validPath);
        });
    }

    @Override
    public void update(VLCStatus status) {
        Platform.runLater(() -> {
            if(status.isConnected()) {
                updateMenuLabels(status);
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
}
