package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.vlc.VLCPlaylist;
import com.github.funnygopher.crowddj.vlc.VLCPlaylistItem;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

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
    Menu mTools, mVLCSettings, mSetup;

    @FXML
    MenuItem miStartVLC, miPlay, miPause, miStop;

    @FXML
    TextField txtVLCPath;

    @FXML
    ListView lvPlaylist;

    @FXML
    ImageView ivAlbumArt;

    @FXML
    Label lblDragAndDrop;

    @FXML
    AnchorPane apRoot;

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
        updatePlaylist();
        updateAlbumArt();
        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());

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

        Image playIcon = new Image(getClass().getResourceAsStream("/images/play-32.png"));
        bPlay.setGraphic(new ImageView(playIcon));
        bPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                play();
            }
        });

        Image pauseIcon = new Image(getClass().getResourceAsStream("/images/pause-32.png"));
        bPause.setGraphic(new ImageView(pauseIcon));
        bPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pause();
            }
        });

        Image stopIcon = new Image(getClass().getResourceAsStream("/images/stop-32.png"));
        bStop.setGraphic(new ImageView(stopIcon));
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

        // Initial setup for drag and drop
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

        // When the music is dropped onto the list, add all of the music to VLC
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
                        lblDragAndDrop.setDisable(true);
                        lblDragAndDrop.setVisible(false);
                    }
                } else {
                    event.setDropCompleted(success);
                }

                event.consume();
                updatePlaybackButtons();
                updatePlaylist();
            }
        });

        // Sets the list items to show the song names
        lvPlaylist.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                ListCell<VLCPlaylistItem> cell = new ListCell<VLCPlaylistItem>() {
                    @Override
                    protected void updateItem(VLCPlaylistItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                };
                return cell;
            }
        });

        // When the playlist is double clicked, play the selected song
        lvPlaylist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                VLCPlaylistItem currentItem = (VLCPlaylistItem) lvPlaylist.getSelectionModel().getSelectedItem();

                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    play(currentItem.getId());
                }
            }
        });

        apRoot.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                // Moves buttons to center of window
                bPlay.setLayoutX((newValue.doubleValue() / 2) - (bPlay.getPrefWidth() / 2));
                bPause.setLayoutX(bPlay.getLayoutX() - 70);
                bStop.setLayoutX(bPlay.getLayoutX() + 70);
            }
        });

        // Sets the background color to black
        apRoot.setStyle("-fx-background-color: black");

        // Adds menu labels to the menus in the menu bar
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
        updateAlbumArt();
    }

    private void play(int id) {
        crowdDJ.getVLC().getController().play(id);
        updatePlaybackButtons();
        updateAlbumArt();
    }

    private void startVLC() {
        crowdDJ.startVLC();
        updatePlaybackButtons();
    }

    private void stop() {
        crowdDJ.getVLC().getController().stop();
        updatePlaybackButtons();
    }

    private void updateAlbumArt() {
        Image albumArt = crowdDJ.getVLC().getController().getAlbumArt();
        if (albumArt == null)
            return;

        ivAlbumArt.setImage(new Image(crowdDJ.getVLC().ALBUM_ART));

        /*
        ivAlbumArt.setFitWidth(apRoot.getWidth());
        ivAlbumArt.setLayoutX((apRoot.getWidth() / 2) - (ivAlbumArt.getBoundsInParent().getWidth() / 2));
        */
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

    private void updatePlaylist() {
        VLCPlaylist playlist = crowdDJ.getPlaylist();
        ObservableList<VLCPlaylistItem> songNames = FXCollections.observableArrayList();
        playlist.getItems().forEach(item -> songNames.add(item));
        if(playlist.size() > 0) {
            lblDragAndDrop.setDisable(true);
            lblDragAndDrop.setVisible(false);
        }
        lvPlaylist.setItems(songNames);
    }
}
