package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.Song;
import com.github.funnygopher.crowddj.managers.DatabaseManager;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.github.funnygopher.crowddj.jooq.Tables.PLAYLIST;

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
    ListView lvPlaylist;

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
    AudioPlayer player;
    PlaybackManager playbackManager;
    MenuManager menuManager;

    public CrowdDJController(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
        player = new AudioPlayer(this, new ArrayList<Song>());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Playback button stuff
        playbackManager = new PlaybackManager(this);

        // Menu stuff
        menuManager = new MenuManager(this);
        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());
        miClearPlaylist.setOnAction(event -> {
            player.clearPlaylist();
            updateDatabase();
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
                updatePlaylist();
                updateDatabase();
            }
        });

        // Sets the list items to show the song names
        lvPlaylist.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                ListCell<Song> cell = new ListCell<Song>() {
                    @Override
                    protected void updateItem(Song item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getTitle());
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
                Song currentItem = (Song) lvPlaylist.getSelectionModel().getSelectedItem();
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    player.play(currentItem);
                }
            }
        });

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

        updatePlaylist();
    }

    public void addFile(File file) {
        if(file.isDirectory()) {
            for (File fileInDir : file.listFiles()) {
                addFile(fileInDir);
            }
        } else {
			player.add(file);
        }
    }

    private void updatePlaylist() {
        //Updates the playlist listview with the names of the songs
        ObservableList<Song> songTitles = FXCollections.observableArrayList();
        player.getPlaylist().forEach(song -> songTitles.add(song));
        if(player.getPlaylist().size() > 0) {
            lblDragAndDrop.setDisable(true);
            lblDragAndDrop.setVisible(false);
        }
        lvPlaylist.setItems(songTitles);
    }

    public void updateDatabase() {
		DatabaseManager database = crowdDJ.getDatabase();

        // Updates the PLAYLIST table in the database
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn);

            // Drop the table and recreate to clear the table. There's probably a better way to do this...
            db.execute("DROP TABLE PLAYLIST");
            db.execute(database.CREATE_PLAYLIST_TABLE);

            for (Song song : player.getPlaylist()) {
                db.insertInto(PLAYLIST, PLAYLIST.FILEPATH).values(song.getFile().getPath()).returning().fetch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public AudioPlayer getPlayer() {
        return player;
    }
}