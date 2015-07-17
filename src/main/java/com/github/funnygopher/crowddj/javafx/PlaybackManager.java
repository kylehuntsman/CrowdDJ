package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.StatusObserver;
import com.github.funnygopher.crowddj.javafx.buttons.Button;
import com.github.funnygopher.crowddj.javafx.buttons.ToggleButton;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class PlaybackManager implements StatusObserver {

    CrowdDJ crowdDJ;
    CrowdDJController controller;
    final EventHandler PLAY, PAUSE, STOP, NEXT, SHUFFLE;

    private Button play, pause, stop;
    private Button next;
    private Button shuffle;

    private Text title, artist;
    private Label currTime, totalTime;

    private ProgressBar songProgress;

    public PlaybackManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;

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
    }

    public void play() {
        crowdDJ.getVLC().getController().play();
    }

    public void pause() {
        crowdDJ.getVLC().getController().pause();
    }

    public void stop() {
        crowdDJ.getVLC().getController().stop();
    }

    public void next() {
        crowdDJ.getVLC().getController().next();
    }

    public void shuffle() {
        crowdDJ.getVLC().getController().toggleRandom();
    }

    @Override
    public void update(VLCStatus status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updatePlaybackButtons(status);

                if (status.isConnected()) {
                    if (status.getTitle() != null && status.getTitle().equals(title.getText())) {
                        updateAlbumArt();
                    }

                    // Update the labels
                    if (status.getTitle() != null) {
                        title.setText(status.getTitle());
                        artist.setText(status.getArtist());
                    } else {
                        title.setText("CrowdDJ");
                        artist.setText("Let The Crowd Choose");
                    }

                    shuffle.update(status);

                    int time = status.getTime();
                    int totTime = status.getLength();
                    currTime.setText(String.format("%02d:%02d", time / 60, time % 60));
                    totalTime.setText(String.format("%02d:%02d", totTime / 60, totTime % 60));

                    double position = status.getPosition();
                    songProgress.setProgress(position);
                }
            }
        });
    }

    private void updateAlbumArt() {
        Image albumArt = crowdDJ.getVLC().getController().getAlbumArt();
        if (albumArt == null) {
            controller.apRoot.setStyle("-fx-background-color: inherit");
        } else {
            controller.apRoot.setStyle("-fx-background-color: black");
        }

        controller.ivAlbumArt.setImage(albumArt);
    }

    private void updatePlaybackButtons(VLCStatus status) {
        play.setVisible(false);
        pause.setVisible(false);
        stop.setVisible(false);

        if(!status.isConnected()) {
            stop.setVisible(true);
            return;
        }

        if(status.isPlaying()) {
            pause.setVisible(true);
        } else if(status.isPaused()) {
            play.setVisible(true);
        } else if(status.isStopped()) {
            play.setVisible(true);
        }
    }
}
