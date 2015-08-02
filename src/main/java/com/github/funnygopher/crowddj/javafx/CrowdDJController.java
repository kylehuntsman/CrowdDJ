package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.playlist.SongModel;
import com.github.funnygopher.crowddj.database.Database;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CrowdDJController implements Initializable {

    @FXML
    Button bPlay, bPause, bStop, bShuffle, bNext;

    @FXML
    AnchorPane pMusicList;

    @FXML
    MenuBar menuBar;

    @FXML
    MenuItem miPlayPause, miStop, miNext;

    @FXML
    MenuItem miAddFiles, miClearPlaylist;

    @FXML
    CheckMenuItem cmiShuffle, cmiShowPlaylist;

    @FXML
    TableView tblPlaylist;

    @FXML
    ImageView ivAlbumArt;

    @FXML
    Label lblDragAndDrop, lbSongTime, lbSongTotalTime;

    @FXML
    Text lbTitle, lbArtist;

    @FXML
    AnchorPane apRoot;

    @FXML
    Rectangle rectTopFade, rectBottomFade;

    @FXML
    ProgressBar pbSongProgress;

    CrowdDJ crowdDJ;
    PlaybackManager playbackManager;
    MenuManager menuManager;

    public CrowdDJController(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Playback button stuff
        playbackManager = new PlaybackManager(this);

        // Menu stuff
        menuManager = new MenuManager(this);
        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());
        miClearPlaylist.setOnAction(event -> {
            crowdDJ.getPlaylist().clear();
            updateDatabase();
        });

        // Setting up the playlist table
        TableColumn playlistTitle = new TableColumn("Title");
        TableColumn playlistArtist = new TableColumn("Artist");

        playlistTitle.setMinWidth(100);
        playlistArtist.setMinWidth(100);

        playlistTitle.setCellValueFactory(
                new PropertyValueFactory<SongModel, String>("title")
        );
        playlistArtist.setCellValueFactory(
                new PropertyValueFactory<SongModel, String>("artist")
        );
        tblPlaylist.setItems(crowdDJ.getPlaylist().getObservableList());
        tblPlaylist.getColumns().addAll(playlistTitle, playlistArtist);

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

                if (db.hasFiles()) {
                    success = true;
                    event.setDropCompleted(success);
                    db.getFiles().forEach(file -> {
                        addFile(file);
                    });
                    lblDragAndDrop.setDisable(true);
                    lblDragAndDrop.setVisible(false);
                } else {
                    event.setDropCompleted(success);
                }

                event.consume();
                updateDatabase();
            }
        });

        /*
        // When the playlist is double clicked, play the selected song
        tblPlaylist.setRowFactory(tableView -> {
            TableRow<Song> row = new TableRow<Song>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && (!row.isEmpty())) {
                    crowdDJ.getPlayer().play(row.getItem());
                }
            });
            return null;
        });
        */

        // The listener for resizing the window, changes the size of the album art
        InvalidationListener resizeListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                bPlay.setLayoutX((apRoot.getWidth() / 2) - (bPlay.getPrefWidth() / 2));
                bPause.setLayoutX(bPlay.getLayoutX());
                bStop.setLayoutX(bPlay.getLayoutX());

                bShuffle.setLayoutX(bPlay.getLayoutX() - 40);
                bNext.setLayoutX(bPlay.getLayoutX() + 70);

                pbSongProgress.setLayoutX((apRoot.getWidth() / 2) - (pbSongProgress.getPrefWidth() / 2));
                lbSongTime.setLayoutX(pbSongProgress.getLayoutX() - 43);
                lbSongTotalTime.setLayoutX(pbSongProgress.getLayoutX() + 310);

                ivAlbumArt.setFitWidth(apRoot.getWidth());
                ivAlbumArt.setFitHeight(ivAlbumArt.getFitWidth());
                ivAlbumArt.setLayoutX((apRoot.getWidth() / 2) - (ivAlbumArt.getBoundsInParent().getWidth() / 2));
            }
        };
        apRoot.widthProperty().addListener(resizeListener);
        apRoot.heightProperty().addListener(resizeListener);

        lbTitle.wrappingWidthProperty().bind(apRoot.widthProperty());
        lbArtist.wrappingWidthProperty().bind(apRoot.widthProperty());

        rectTopFade.widthProperty().bind(apRoot.widthProperty());
        rectBottomFade.widthProperty().bind(apRoot.widthProperty());
    }

    public void addFile(File file) {
        crowdDJ.getPlaylist().add(file);
    }

    public void updateDatabase() {
		Database database = crowdDJ.getDatabase();
        crowdDJ.getPlaylist().updateDbTable(database);
    }

    public void setSongInformation(Song song) {
        System.out.println("Updating song information\n" +
                "\tTitle:  " + song.getTitle() + "\n" +
                "\tArtist: " + song.getArtist() + "\n" +
                "\tVotes:  " + song.getVotes() + "\n" +
                "\tURI:    " + song.getURI());

        Platform.runLater(() -> {
            if (song != null) {
                lbTitle.setText(song.getTitle());
                lbArtist.setText(song.getArtist());

                if (song.getAlbumArt() == null) {
                    apRoot.setStyle("-fx-background-color: inherit");
                } else {
                    apRoot.setStyle("-fx-background-color: black");
                }

                ivAlbumArt.setImage(song.getAlbumArt());
            } else {
                lbTitle.setText("CrowdDJ");
                lbArtist.setText("Let The Crowd Choose");
                apRoot.setStyle("-fx-background-color: inherit");
            }
        });
    }

    public PlaybackManager getPlaybackManager() {
        return playbackManager;
    }
}