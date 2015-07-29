package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.javafx.buttons.Button;
import com.github.funnygopher.crowddj.javafx.buttons.ToggleButton;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

public class PlaybackManager {

    CrowdDJ crowdDJ;
    CrowdDJController controller;
    AudioPlayer player;

    final EventHandler PLAY, PAUSE, STOP, NEXT, SHUFFLE;

    private Button play, pause, stop;
    private Button next;
    private ToggleButton shuffle;

    private Text title, artist;
    private Label currTime, totalTime;

    private ProgressBar songProgress;

    public PlaybackManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;
        this.player = controller.getPlayer();

        PLAY = (event -> play());
        PAUSE = (event -> pause());
        STOP = (event -> stop());
        NEXT = (event -> next());
        SHUFFLE = (event -> shuffle());

        play = new Button(controller.bPlay,"/images/light/play-32.png", PLAY, "/css/button_playback.css");
        pause = new Button(controller.bPause,"/images/light/pause-32.png", PAUSE, "/css/button_playback.css");
        stop = new Button(controller.bStop,"/images/light/stop-32.png", STOP, "/css/button_playback.css");
        next = new Button(controller.bNext, "/images/light/fast-forward-16.png", NEXT, "/css/button_passive.css");
        shuffle = new ToggleButton(controller.bShuffle, "/images/light/shuffle-16.png", SHUFFLE, "/css/button_toggled_on.css", "/css/button_passive.css");

        this.title = controller.lbTitle;
        this.artist = controller.lbArtist;

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
    }

    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public void next() {
        player.next();
    }

    public void shuffle() {
        player.shuffle();
    }

    public void update(MediaPlayer.Status status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //int time = status.getTime();
                //int totTime = status.getLength();
                //currTime.setText(String.format("%02d:%02d", time / 60, time % 60));
                //totalTime.setText(String.format("%02d:%02d", totTime / 60, totTime % 60));

                //double position = status.getPosition();
                //songProgress.setProgress(position);
            }
        });
    }

    public void updatePlaybackButtons(MediaPlayer.Status status) {
        System.out.println("Updating playback buttons\n" +
                "\tStatus: " + status.toString());

        Platform.runLater(() -> {
            play.setVisible(false);
            pause.setVisible(false);
            stop.setVisible(false);

            switch (status) {
                case PLAYING:
                    pause.setVisible(true);
                    break;
                case PAUSED:
                    play.setVisible(true);
                    break;
                case STOPPED:
                    play.setVisible(true);
                    break;
            }
        });
    }
}
