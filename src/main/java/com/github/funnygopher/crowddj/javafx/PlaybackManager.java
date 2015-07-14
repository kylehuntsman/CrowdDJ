package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.StatusObserver;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class PlaybackManager implements StatusObserver {

    CrowdDJ crowdDJ;
    CrowdDJController controller;
    final EventHandler PLAY, PAUSE, STOP, SHUFFLE, NEXT;

    private Button play, pause, stop, shuffle, next;
    private Button[] playbackButtons, extraButtons;

    private Text title, artist;
    private Label currTime, totalTime;

    private ProgressBar songProgress;

    private final String playbackButtonStyle = "-fx-background-color: rgba(0,0,0,0);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;";
    private final String extraButtonStyle = "-fx-background-color: rgba(0,0,0,0)";

    public PlaybackManager(CrowdDJController controller) {
        this.crowdDJ = controller.crowdDJ;
        this.controller = controller;

        PLAY = (event -> play());
        PAUSE = (event -> pause());
        STOP = (event -> stop());
        SHUFFLE = (event -> shuffle());
        NEXT = (event -> next());

        this.play = controller.bPlay;
        this.pause = controller.bPause;
        this.stop = controller.bStop;
        playbackButtons = new Button[]{play, pause, stop};

        this.shuffle = controller.bShuffle;
        this.next = controller.bNext;
        extraButtons = new Button[]{shuffle, next};

        this.title = controller.lbTitle;
        this.artist = controller.lbArtist;

        this.songProgress = controller.pbSongProgress;
        songProgress.getStylesheets().add(this.getClass().getResource("/css/song_progressbar.css").toExternalForm());

        this.currTime = controller.lbSongTime;
        this.totalTime = controller.lbSongTotalTime;

        Image playIcon = new Image(getClass().getResourceAsStream("/images/light/play-32.png"));
        Image pauseIcon = new Image(getClass().getResourceAsStream("/images/light/pause-32.png"));
        Image stopIcon = new Image(getClass().getResourceAsStream("/images/light/stop-32.png"));
        Image shuffleIcon = new Image(getClass().getResourceAsStream("/images/light/shuffle-16.png"));
        Image nextIcon = new Image(getClass().getResourceAsStream("/images/light/fast-forward-16.png"));

        play.setGraphic(new ImageView(playIcon));
        pause.setGraphic(new ImageView(pauseIcon));
        stop.setGraphic(new ImageView(stopIcon));
        shuffle.setGraphic(new ImageView(shuffleIcon));
        next.setGraphic(new ImageView(nextIcon));

        play.setOnMouseClicked(PLAY);
        pause.setOnMouseClicked(PAUSE);
        stop.setOnMouseClicked(STOP);
        shuffle.setOnMouseClicked(SHUFFLE);
        next.setOnMouseClicked(NEXT);

        // Setting initial style
        for (Button button : playbackButtons)
            button.setStyle(playbackButtonStyle + "-fx-border-color: gray");

        for (Button extraButton : extraButtons) {
            extraButton.setStyle(extraButtonStyle);
            extraButton.getGraphic().setOpacity(.5);
        }

        // Set on mouse enter event
        for (Button button : playbackButtons) {
            button.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    button.setStyle(playbackButtonStyle + "-fx-border-color: white");
                }
            });
        }

        for (Button extraButton : extraButtons) {
            extraButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    extraButton.getGraphic().setOpacity(1);
                }
            });
        }

        // Set on mouse exit event
        for (Button button : playbackButtons) {
            button.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    button.setStyle(playbackButtonStyle + "-fx-border-color: gray");
                }
            });
        }

        for (Button extraButton : extraButtons) {
            extraButton.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    extraButton.getGraphic().setOpacity(.5);
                }
            });
        }

        shuffle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!crowdDJ.getStatusManager().getStatus().isRandom())
                    shuffle.getGraphic().setOpacity(.5);
            }
        });
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

    public void shuffle() {
        crowdDJ.getVLC().getController().toggleRandom();
    }

    public void next() {
        crowdDJ.getVLC().getController().next();
    }

    @Override
    public void update(VLCStatus status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updatePlaybackButtons(status);

                if(status.isConnected()) {
                    if (status.getTitle() != null && !status.getTitle().equals(title.getText())) {
                        updateAlbumArt();
                    }

                    if(status.isPlaying())
                        controller.apRoot.setStyle("-fx-background-color: black");

                    // Update the labels
                    if(status.getTitle() != null) {
                        title.setText(status.getTitle());
                        artist.setText(status.getArtist());
                    }

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
        if (albumArt == null)
            return;

        controller.ivAlbumArt.setImage(albumArt);
    }

    private void updatePlaybackButtons(VLCStatus status) {
        play.setVisible(false);
        pause.setVisible(false);
        stop.setVisible(false);

        if(!status.isConnected()) {
            stop.setVisible(true);
            stop.setDisable(false);
        }
        if(status.isPlaying()) {
            play.setVisible(false);
            pause.setVisible(true);

            pause.setDisable(false);
        }
        if(status.isPaused()) {
            play.setVisible(true);
            pause.setVisible(false);

            play.setDisable(false);
            pause.setDisable(true);
            stop.setDisable(false);
        }
        if(status.isStopped()) {
            play.setVisible(true);
            pause.setVisible(false);

            play.setDisable(false);
            pause.setDisable(true);
            stop.setDisable(true);
        }
        if(status.isRandom()) {
            shuffle.getGraphic().setOpacity(1);
        }
    }
}
