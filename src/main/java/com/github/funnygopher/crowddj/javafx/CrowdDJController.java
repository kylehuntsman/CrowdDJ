package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CrowdDJController implements Initializable {

    @FXML
    TextField txtVLCPath;

    @FXML
    Button bStartVLC;

    @FXML
    Button bPlay, bPause, bStop;

    @FXML
    AnchorPane pMusicList;

    private CrowdDJ crowdDJ;

    public CrowdDJController(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtVLCPath.setText("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
        crowdDJ.setVLCPath(txtVLCPath.getText());

        bStartVLC.setDisable(true);
        bStartVLC.setDisable(!crowdDJ.hasValidVLCPath());

        updatePlaybackButtons();

        txtVLCPath.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String alteredValue = newValue.replace("\\", "/");
                crowdDJ.setVLCPath(alteredValue);

                bStartVLC.setDisable(!crowdDJ.hasValidVLCPath());
            }
        });

        bStartVLC.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onStartVLCClick();
            }
        });

        bPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                crowdDJ.getVLC().getController().play();
                updatePlaybackButtons();
            }
        });

        bPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                crowdDJ.getVLC().getController().pause();
                updatePlaybackButtons();
            }
        });

        bStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                crowdDJ.getVLC().getController().stop();
				updatePlaybackButtons();
				updatePlaybackButtons();
            }
        });

        pMusicList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if(db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                } else {
                    event.consume();
                }
            }
        });

        pMusicList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if(crowdDJ.isVLCConnected()) {
                    if (db.hasFiles()) {
                        success = true;
                        event.setDropCompleted(success);
                        db.getFiles().forEach(file -> addFile(file));
                    }
                } else {
                    event.setDropCompleted(success);
                }
                event.consume();
				updatePlaybackButtons();
            }
        });
    }

    private void onStartVLCClick() {
        crowdDJ.startVLC();
        updatePlaybackButtons();
    }

    private void addFile(File file) {
        if(file.isDirectory()) {
            for (File fileInDir : file.listFiles()) {
                addFile(fileInDir);
            }
        } else {
            crowdDJ.getVLC().getController().add(file);
        }
    }

    private void updatePlaybackButtons() {
        VLCStatus status = crowdDJ.getVLC().getStatus();
		System.out.println(status);

        if(!status.isConnected()) {
            bPlay.setDisable(true);
            bPause.setDisable(true);
            bStop.setDisable(true);
        }
		if(status.isPlaying()) {
            bPlay.setDisable(true);
            bPause.setDisable(false);
            bStop.setDisable(false);
        }
		if(status.isPaused()) {
            bPlay.setDisable(false);
            bPause.setDisable(true);
            bStop.setDisable(false);
        }
		if(status.isStopped()) {
            bPlay.setDisable(false);
            bPause.setDisable(true);
            bStop.setDisable(true);
        }
    }
}
