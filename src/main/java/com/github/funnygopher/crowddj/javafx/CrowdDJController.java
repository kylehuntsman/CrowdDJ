package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.javafx.buttons.ButtonUtil;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;
import com.sun.javafx.iio.ImageStorage;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CrowdDJController implements Initializable {

    @FXML
    Button bPlay, bPause, bStop, bShuffle, bNext;

    @FXML
    AnchorPane pPlaylist;

    @FXML
    MenuBar menuBar;

    @FXML
    MenuItem miPlayPause, miStop, miNext;

    @FXML
    MenuItem miAddFiles, miClearPlaylist;

    @FXML
    ToggleButton tbShowQRCode;

    @FXML
    CheckMenuItem cmiShuffle, cmiShowPlaylist;

    @FXML
    TableView<Song> tblPlaylist;

    @FXML
    ImageView ivCoverArt;

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

    private final Player player;
    private final Playlist playlist;
    private final String serverCode;

    private final Image DEFAULT_COVER_ART;
    private final Image QR_CODE;
    private EventHandler<ActionEvent> addFilesEvent, clearPlaylistEvent;

    public CrowdDJController(Player player, Playlist playlist, String serverCode) {
        this.player = player;
        this.playlist = playlist;
        this.serverCode = serverCode;

        DEFAULT_COVER_ART = getImage("default_cover_art.png");
        QR_CODE = getQRCode();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initEvents();
        initRoot();
        initMenuView();
        initPlaylistView();
        initPlayerView();

        player.playingProperty().addListener((observable, wasPlaying, isPlaying) -> {
            if (isPlaying) {
                miPlayPause.setOnAction(event -> player.pause());
                miPlayPause.setText("Pause");
            } else {
                miPlayPause.setOnAction(event -> player.play());
                miPlayPause.setText("Play");
            }
        });

        bPlay.visibleProperty().bind(player.pausedProperty().or(player.stoppedProperty()));
        bPause.visibleProperty().bind(player.playingProperty());

        showDefaultPlayer();
    }

    private void initEvents() {
        addFilesEvent = event -> {
            FileChooser fileChooser = new FileChooser();
            List<File> list = fileChooser.showOpenMultipleDialog(apRoot.getScene().getWindow());
            if (list != null) {
                list.forEach(file -> playlist.add(file));
            }
        };

        clearPlaylistEvent = event -> {
            playlist.clear();
            playlist.updateDatabaseTable();
        };
    }

    private void initRoot() {
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

            ivCoverArt.setFitWidth(apRoot.getWidth());
            ivCoverArt.setFitHeight(ivCoverArt.getFitWidth());
            ivCoverArt.setLayoutX((apRoot.getWidth() / 2) - (ivCoverArt.getBoundsInParent().getWidth() / 2));
        };
        apRoot.widthProperty().addListener(resizeListener);
        apRoot.heightProperty().addListener(resizeListener);
        apRoot.setStyle("-fx-background-color: black");

        // Sets the window to allow drag and drop
        apRoot.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.ANY);
            } else {
                dragEvent.consume();
            }
        });

        apRoot.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                dragEvent.setDropCompleted(true);
                db.getFiles().forEach(file -> playlist.add(file));
            } else {
                dragEvent.setDropCompleted(false);
            }
            dragEvent.consume();
        });

        // Moves the text as well as some decoration boxes when the window resizes
        lbTitle.wrappingWidthProperty().bind(apRoot.widthProperty());
        lbArtist.wrappingWidthProperty().bind(apRoot.widthProperty());
        rectTopFade.widthProperty().bind(apRoot.widthProperty());
        rectBottomFade.widthProperty().bind(apRoot.widthProperty());
    }

    private void initMenuView() {
        menuBar.getStylesheets().add(this.getClass().getResource("/css/label_separator.css").toExternalForm());
        menuBar.getStylesheets().add(this.getClass().getResource("/css/dark_menubar.css").toExternalForm());

        miPlayPause.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN));
        miStop.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        miNext.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT));
        cmiShuffle.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHIFT_DOWN));
        cmiShowPlaylist.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

        miPlayPause.setOnAction(event -> player.play());
        miStop.setOnAction(event -> player.stop());
        miNext.setOnAction(event -> player.next());
        cmiShuffle.setOnAction(event -> player.shuffle());

        miAddFiles.setOnAction(addFilesEvent);
        miClearPlaylist.setOnAction(clearPlaylistEvent);


        player.shuffleProperty().addListener((observable, oldValue, newValue) -> {
            cmiShuffle.setSelected(newValue);
        });

        cmiShowPlaylist.selectedProperty().set(true);
        cmiShowPlaylist.selectedProperty().addListener((observable, oldValue, newValue) -> {
            pPlaylist.setVisible(newValue);
        });

        tbShowQRCode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                ivCoverArt.setImage(QR_CODE);
            } else {
                if(player.currentSongProperty().get() == null) {
                    ivCoverArt.setImage(DEFAULT_COVER_ART);
                } else {
                    ivCoverArt.setImage(player.currentSongProperty().get().coverArtProperty().get());
                }
            }
        });
    }

    private void initPlaylistView() {
        // Creates the table columns
        TableColumn<Song, String> playlistTitle = new TableColumn<>("Title");
        TableColumn<Song, String> playlistArtist = new TableColumn<>("Artist");
        TableColumn<Song, String> playlistVotes = new TableColumn<>("Votes");

        playlistTitle.setMinWidth(300);
        playlistArtist.setMinWidth(150);
        playlistVotes.setMinWidth(100);

        playlistTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        playlistArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        playlistVotes.setCellValueFactory(new PropertyValueFactory<>("votes"));

        tblPlaylist.setItems(playlist.getItems());
        tblPlaylist.getColumns().addAll(playlistTitle, playlistArtist, playlistVotes);
        tblPlaylist.setOpacity(.75);
        tblPlaylist.getStylesheets().add(getCss("playlist_table.css"));

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem addFiles = new MenuItem("Add files...");
        MenuItem clearPlaylist = new MenuItem("Clear playlist");
        addFiles.setOnAction(addFilesEvent);
        clearPlaylist.setOnAction(addFilesEvent);
        contextMenu.getItems().addAll(addFiles, clearPlaylist);
        tblPlaylist.setContextMenu(contextMenu);

        // Sets the actions when a row is selected
        tblPlaylist.setRowFactory(tableView -> {
            TableRow<Song> row = new TableRow<Song>();

            // Plays the song when the row is double clicked
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && (!row.isEmpty())) {
                    player.play(row.getItem());
                }
            });

            EventHandler<ActionEvent> changeCoverArt = event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Cover Art", "*.jpg", "*.jpeg"));
                File file = fileChooser.showOpenDialog(apRoot.getScene().getWindow());

                if (row.getItem() == player.currentSongProperty().get()) {
                    player.reset();
                }

                if (file != null) {
                    Song song = row.getItem();
                    song.changeAlbumArt(file);
                }
            };

            EventHandler<ActionEvent> removeSelectedItem = event -> {
                if (row.getItem() == player.currentSongProperty().get()) {
                    player.reset();
                }
                playlist.remove(row.getItem());
            };

            // Creates a context menu for the selected item
            final ContextMenu itemContextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Remove");
            MenuItem editAlbumArt = new MenuItem("Change cover art...");
            removeItem.setOnAction(removeSelectedItem);
            editAlbumArt.setOnAction(changeCoverArt);
            itemContextMenu.getItems().addAll(removeItem, editAlbumArt);

            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(itemContextMenu)
                            .otherwise((ContextMenu) null));

            return row;
        });
    }

    private void initPlayerView() {
        player.currentSongProperty().addListener((observable1, oldSong, newSong) -> {
            if (newSong == null) {
                showDefaultPlayer();
            } else {
                lbTitle.setText(newSong.titleProperty().get());
                lbArtist.setText(newSong.artistProperty().get());

                if (newSong.coverArtProperty().get() == null) {
                    ivCoverArt.setImage(DEFAULT_COVER_ART);
                } else {
                    ivCoverArt.setImage(newSong.coverArtProperty().get());
                }
            }
        });

        ButtonUtil.SetupButton(bPlay, event -> player.play(), getImage("light/play-32.png"), getCss("button_playback.css"));
        ButtonUtil.SetupButton(bPause, event -> player.pause(), getImage("light/pause-32.png"), getCss("button_playback.css"));
        ButtonUtil.SetupButton(bStop, event -> player.stop(), getImage("light/stop-32.png"), getCss("button_playback.css"));
        ButtonUtil.SetupButton(bNext, event -> player.next(), getImage("light/fast-forward-16.png"), getCss("button_passive.css"));
        ButtonUtil.SetupToggleButton(
                bShuffle, event -> player.shuffle(), player.shuffleProperty(), getImage("light/shuffle-16.png"),
                getCss("button_toggled_on.css"), getCss("button_passive.css")
        );

        pbSongProgress.getStylesheets().add(getCss("song_progressbar.css"));

        bPlay.setVisible(true);
        bPause.setVisible(false);
        bStop.setVisible(false);

        player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            lbSongTime.setText(String.format("%02d:%02d", newValue.intValue() / 60, newValue.intValue() % 60));
            double duration = player.durationProperty().get();
            pbSongProgress.setProgress(newValue.doubleValue() / duration);
        });
        player.durationProperty().addListener((observable, oldValue, newValue) -> {
            lbSongTotalTime.setText(String.format("%02d:%02d", newValue.intValue() / 60, newValue.intValue() % 60));
        });
    }

    private void showDefaultPlayer() {
        Platform.runLater(() -> {
            lbTitle.setText("CrowdDJ");
            lbArtist.setText("Let The Crowd Choose");
            ivCoverArt.setImage(DEFAULT_COVER_ART);
        });
    }

    private Image getImage(String imageName) {
        return new Image(getClass().getResourceAsStream("/images/" + imageName));
    }

    private String getCss(String cssName) {
        return getClass().getResource("/css/" + cssName).toExternalForm();
    }

    private Image getQRCode() {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(serverCode, BarcodeFormat.QR_CODE, 250, 250, hintMap);
            ByteOutputStream out = new ByteOutputStream();
            MatrixToImageWriter.writeToStream(byteMatrix, "PNG", out);
            return new Image(new ByteArrayInputStream(out.getBytes()));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}