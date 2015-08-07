package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.javafx.buttons.Button;
import com.github.funnygopher.crowddj.javafx.buttons.ToggleButton;
import com.github.funnygopher.crowddj.player.Player;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class PlaybackManager {

    private Button play, pause, stop;
    private Button next;
    private ToggleButton shuffle;

    private Label currTime, totalTime;
    private ProgressBar songProgress;

    private Player player;

    public PlaybackManager(CrowdDJController controller, Player player) {
        this.player = player;

        play = new Button(controller.bPlay,"/images/light/play-32.png", event -> player.play(), "/css/button_playback.css");
        pause = new Button(controller.bPause,"/images/light/pause-32.png", event -> player.pause(), "/css/button_playback.css");
        stop = new Button(controller.bStop,"/images/light/stop-32.png", event -> player.stop(), "/css/button_playback.css");
        next = new Button(controller.bNext, "/images/light/fast-forward-16.png", event -> player.next(), "/css/button_passive.css");
        shuffle = new ToggleButton(controller.bShuffle, "/images/light/shuffle-16.png", event -> player.shuffle(), "/css/button_toggled_on.css", "/css/button_passive.css");

        this.songProgress = controller.pbSongProgress;
        songProgress.getStylesheets().add(this.getClass().getResource("/css/song_progressbar.css").toExternalForm());

        this.currTime = controller.lbSongTime;
        this.totalTime = controller.lbSongTotalTime;

        play.setVisible(true);
        pause.setVisible(false);
        stop.setVisible(false);

        shuffle.set(player.shuffleProperty().get());
        player.shuffleProperty().addListener((observable, oldValue, newValue) -> {
            shuffle.set(newValue);
        });

        player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            currTime.setText(String.format("%02d:%02d", newValue.intValue() / 60, newValue.intValue() % 60));
            double duration = player.durationProperty().get();
            songProgress.setProgress(newValue.doubleValue() / duration);
        });
        player.durationProperty().addListener((observable, oldValue, newValue) -> {
            totalTime.setText(String.format("%02d:%02d", newValue.intValue() / 60, newValue.intValue() % 60));
        });
    }

    public void updatePlaybackButtons(Player player) {
        System.out.println("Updating playback buttons");

        Platform.runLater(() -> {
            play.setVisible(false);
            pause.setVisible(false);
            stop.setVisible(false);

            if(player.isPlaying()) {
                pause.setVisible(true);
            } else if(player.isPaused()) {
                play.setVisible(true);
            } else if(player.isStopped()) {
                play.setVisible(true);
            }
        });
    }
}
