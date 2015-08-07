package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class MenuManager {

    private MenuBar menuBar;
    private MenuItem playpause, stop, next;
    private MenuItem addFiles, clearPlaylist;
    private CheckMenuItem shuffle, showPlaylist;

    public MenuManager(CrowdDJController controller, Player player, Playlist playlist) {
        this.menuBar = controller.menuBar;

        this.playpause = controller.miPlayPause;
        this.stop = controller.miStop;
        this.next = controller.miNext;

        this.addFiles = controller.miAddFiles;
        this.clearPlaylist = controller.miClearPlaylist;

        this.shuffle = controller.cmiShuffle;
        this.showPlaylist = controller.cmiShowPlaylist;


        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());

        playpause.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN));
        stop.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        next.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT));
        shuffle.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHIFT_DOWN));
        showPlaylist.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

        playpause.setOnAction(event -> player.play());
        stop.setOnAction(event -> player.stop());
        next.setOnAction(event -> player.next());
        shuffle.setOnAction(event -> player.shuffle());

        addFiles.setOnAction(controller.addFilesEvent);
        clearPlaylist.setOnAction(controller.clearPlaylistEvent);


        player.shuffleProperty().addListener((observable, oldValue, newValue) -> {
            shuffle.setSelected(newValue);
        });

        showPlaylist.selectedProperty().set(true);
        showPlaylist.selectedProperty().addListener((observable, oldValue, newValue) -> {
            controller.pMusicList.setVisible(newValue);
        });
    }

    public void updatePlaybackButtons(Player player) {
        if (player.isPlaying()) {
            playpause.setOnAction(event -> player.pause());
            playpause.setText("Pause");
        } else if (player.isPaused() || player.isStopped()) {
            playpause.setOnAction(event -> player.play());
            playpause.setText("Play");
        }
    }
}
