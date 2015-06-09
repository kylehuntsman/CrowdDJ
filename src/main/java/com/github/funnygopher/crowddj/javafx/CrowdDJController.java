package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    Button bPlay, bPause, bStop;

    @FXML
    AnchorPane pMusicList;

    @FXML
    MenuBar menuBar;

    @FXML
    Menu mVLCSettings, mSetup;

    @FXML
    MenuItem miStartVLC, miPlay, miPause, miStop;

    @FXML
    TextField txtVLCPath;

    private CrowdDJ crowdDJ;

    public CrowdDJController(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initial values for fields and stuff
        txtVLCPath.setText("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
        crowdDJ.setVLCPath(txtVLCPath.getText());

        miStartVLC.setDisable(true);
        miStartVLC.setDisable(!crowdDJ.hasValidVLCPath());

        updatePlaybackButtons();
        menuBar.getStylesheets().add(this.getClass().getResource("/label_separator.css").toExternalForm());

        // We can't use SceneBuilder to set all of the actions, because we need to be able to pass our CrowdDJ
        // instance to the constructor of this class. This means the CrowdDJController is not tied to the form,
        // like other controller classes. Without a CrowdDJController instance being tied to the form,
        // SceneBuilder will not recognize annotated functions, hence all the manual setting of actions.

        txtVLCPath.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String alteredValue = newValue.replace("\\", "/");
                crowdDJ.setVLCPath(alteredValue);

                miStartVLC.setDisable(!crowdDJ.hasValidVLCPath());
            }
        });

        miStartVLC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startVLC();
            }
        });

        bPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                play();
            }
        });

        bPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pause();
            }
        });

        bStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stop();
            }
        });

        miPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                play();
            }
        });

        miPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pause();
            }
        });

        miStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stop();
            }
        });

        pMusicList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
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

                if (crowdDJ.isVLCConnected()) {
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

        addMenuLabel(mVLCSettings, "VLC Executable Path", 0);

        addMenuLabel(mSetup, "Port", 0);
        addMenuLabel(mSetup, "Password", 2);
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

    private void addMenuLabel(Menu menu, String text, int index) {
        menu.getItems().add(index, new LabelSeparatorMenuItem(text));
    }

    private void pause() {
        crowdDJ.getVLC().getController().pause();
        updatePlaybackButtons();
    }

    private void play() {
        crowdDJ.getVLC().getController().play();
        updatePlaybackButtons();
    }

    private void startVLC() {
        crowdDJ.startVLC();
        updatePlaybackButtons();
    }

    private void stop() {
        crowdDJ.getVLC().getController().stop();
        updatePlaybackButtons();
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
