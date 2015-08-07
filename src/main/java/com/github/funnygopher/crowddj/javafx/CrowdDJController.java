package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.playlist.SongModel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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

    private Player player;
    private Playlist playlist;
    private PlaybackManager playbackManager;
    private MenuManager menuManager;

    public CrowdDJController(Player player, Playlist playlist) {
        this.player = player;
        this.playlist = playlist;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playbackManager = new PlaybackManager(this, player);
        menuManager = new MenuManager(this, player, playlist);

        Runnable updatePlaybackButtons = () -> {
            playbackManager.updatePlaybackButtons(player);
            menuManager.updatePlaybackButtons(player);
        };
        player.setOnPlay(updatePlaybackButtons);
        player.setOnPause(updatePlaybackButtons);
        player.setOnStop(updatePlaybackButtons);
        player.currentSongProperty().addListener(((observable, oldValue, newValue) -> {
            setSongInformation(newValue);
        }));

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
        tblPlaylist.setItems(playlist.getItems());
        tblPlaylist.getColumns().addAll(playlistTitle, playlistArtist);

        // Initial setup for drag and drop
        pMusicList.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.ANY);
            } else {
                dragEvent.consume();
            }
        });

        // When the music is dropped onto the list, add all of the music to VLC
        pMusicList.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                success = true;
                dragEvent.setDropCompleted(success);
                db.getFiles().forEach(file -> {
                    playlist.add(file);
                });
                lblDragAndDrop.setDisable(true);
                lblDragAndDrop.setVisible(false);
            } else {
                dragEvent.setDropCompleted(success);
            }

            dragEvent.consume();
        });

        // When the playlist is double clicked, play the selected song
        tblPlaylist.setRowFactory(tableView -> {
            TableRow<Song> row = new TableRow<Song>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && (!row.isEmpty())) {
                    player.play(row.getItem());
                }
            });
            return row;
        });

        // The listener for resizing the window, changes the size of the album art
        InvalidationListener resizeListener = observable -> {
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
        };

        apRoot.widthProperty().addListener(resizeListener);
        apRoot.heightProperty().addListener(resizeListener);

        lbTitle.wrappingWidthProperty().bind(apRoot.widthProperty());
        lbArtist.wrappingWidthProperty().bind(apRoot.widthProperty());

        rectTopFade.widthProperty().bind(apRoot.widthProperty());
        rectBottomFade.widthProperty().bind(apRoot.widthProperty());
    }

    private void setSongInformation(Song song) {
        System.out.println("Updating song information\n" +
                "\tTitle:  " + song.getTitle() + "\n" +
                "\tArtist: " + song.getArtist() + "\n" +
                "\tVotes:  " + song.getVotes() + "\n" +
                "\tURI:    " + song.getURI());

        Platform.runLater(() -> {
            lbTitle.setText(song.getTitle());
            lbArtist.setText(song.getArtist());

            if (song.getAlbumArt() == null) {
                apRoot.setStyle("-fx-background-color: inherit");
            } else {
                apRoot.setStyle("-fx-background-color: black");
            }

            ivAlbumArt.setImage(song.getAlbumArt());
        });
    }
}