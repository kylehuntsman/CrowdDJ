package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.Song;
import com.github.funnygopher.crowddj.exceptions.NoVLCConnectionException;
import com.github.funnygopher.crowddj.managers.DatabaseManager;
import com.github.funnygopher.crowddj.managers.PlaylistManager;
import com.github.funnygopher.crowddj.vlc.VLCPlaylist;
import com.github.funnygopher.crowddj.vlc.VLCPlaylistItem;
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
    Menu mVLCSettings, mSetup;

    @FXML
    MenuItem miPlayPause, miStop, miNext;
    @FXML
    MenuItem miStartVLC, miAddFiles, miClearPlaylist;

    @FXML
    CheckMenuItem cmiShuffle, cmiShowPlaylist;

    @FXML
    TextField txtPort, txtVLCPath;

    @FXML
    ListView lvPlaylist;
    ObservableList<VLCPlaylistItem> songNames = FXCollections.observableArrayList();

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
        playbackManager = new PlaybackManager(this);
        menuManager = new MenuManager(this);

        // Initial values for fields and stuff

        updatePlaylist();
        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());

        // We can't use SceneBuilder to set all of the actions, because we need to be able to pass our CrowdDJ
        // instance to the constructor of this class. This means the CrowdDJController is not tied to the form,
        // like other controller classes. Without a CrowdDJController instance being tied to the form,
        // SceneBuilder will not recognize @FXML annotated functions, hence all the manual setting
        // of actions.

        miClearPlaylist.setOnAction(event -> {
            crowdDJ.getVLC().getController().clearPlaylist();
            crowdDJ.getPlaylist().clear();
            updateDatabase();
        });

        miStartVLC.setOnAction(event -> startVLC());
        miStartVLC.setDisable(!crowdDJ.hasValidVLCPath());

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
                updatePlaylist();
                updateDatabase();
            }
        });

        lvPlaylist.setItems(songNames);
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

        // Adds menu labels to the menus in the menu bar
        addMenuLabel(mVLCSettings, "VLC Executable Path", 0);

        crowdDJ.getStatusManager().registerObserver(playbackManager);
        crowdDJ.getStatusManager().registerObserver(menuManager);
        crowdDJ.getStatusManager().start();
    }

    public void addFile(File file) {
        if(file.isDirectory()) {
            for (File fileInDir : file.listFiles()) {
                addFile(fileInDir);
            }
        } else {
            PlaylistManager playlist = crowdDJ.getPlaylist();
			if(!playlist.search(file).found()) {
				playlist.add(file);
                crowdDJ.getVLC().getController().add(file);
            }
        }
    }

    private void play(int id) {
        crowdDJ.getVLC().getController().play(id);
    }

    private void addMenuLabel(Menu menu, String text, int index) {
        menu.getItems().add(index, new LabelSeparatorMenuItem(text));
    }

    public void startVLC() {
        crowdDJ.startVLC();
    }

    private void updatePlaylist() {
        try {
            //Updates the playlist listview with the names of the songs
        /*
            PlaylistManager playlist = crowdDJ.getPlaylist();
            ObservableList<Song> songTitles = FXCollections.observableArrayList();
            playlist.getItems().forEach(song -> songTitles.add(song));
            if(playlist.getItems().size() > 0) {
                lblDragAndDrop.setDisable(true);
                lblDragAndDrop.setVisible(false);
            }
            lvPlaylist.setItems(songTitles);
        */

            VLCPlaylist vlcPlaylist = crowdDJ.getVLC().getPlaylist();

            if(vlcPlaylist.size() != songNames.size()) {
                songNames.clear();
                vlcPlaylist.getItems().forEach(item -> songNames.add(item));
                if(vlcPlaylist.size() > 0) {
                    lblDragAndDrop.setDisable(true);
                    lblDragAndDrop.setVisible(false);
                }
            }
        } catch (NoVLCConnectionException e) {
            e.printError("Could not fetch playlist. Not connected to VLC media player.");
        }
    }

    public void updateDatabase() {
		DatabaseManager database = crowdDJ.getDatabase();
        PlaylistManager playlist = crowdDJ.getPlaylist();

        // Updates the PLAYLIST table in the database
        try (Connection conn = database.getConnection()) {
            DSLContext db = DSL.using(conn);

            // Drop the table and recreate to clear the table. There's probably a better way to do this...
            db.execute("DROP TABLE PLAYLIST");
            db.execute(database.CREATE_PLAYLIST_TABLE);

            for (Song song : playlist.getItems()) {
                db.insertInto(PLAYLIST, PLAYLIST.FILEPATH).values(song.getFile().getPath()).returning().fetch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlaybackManager getPlaybackManager() {
        return playbackManager;
    }
}