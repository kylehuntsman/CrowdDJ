package com.github.funnygopher.crowddj.song;

import com.github.funnygopher.crowddj.util.XFile;
import com.mpatric.mp3agic.*;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SongInfo {

    private StringProperty mTitle;
    private StringProperty mArtist;
    private StringProperty mAlbum;
    private ObjectProperty<Image> mCoverArt;

    private String mFilePath;
    private Mp3File mMp3File;

    public SongInfo(String filePath) {
        mFilePath = filePath;
        File file = new File(mFilePath);

        mTitle = new SimpleStringProperty(this, "title", file.getName());
        mArtist = new SimpleStringProperty(this, "artist", "");
        mAlbum = new SimpleStringProperty(this, "album", "");

        Image defaultCoverArt = new Image(getClass().getResourceAsStream("/images/default_cover_art.png"));
        mCoverArt = new SimpleObjectProperty<>(this, "coverArt", defaultCoverArt);

        // Reads the Mp3 tags for song information
        try {
            mMp3File = new Mp3File(filePath);
            if (mMp3File.hasId3v2Tag()) {
                ID3v2 tag = mMp3File.getId3v2Tag();

                String title = tag.getTitle(); // The title
                if(title != null && !title.isEmpty()) mTitle.set(title);

                String artist = tag.getArtist(); // The artist
                if(artist != null && !artist.isEmpty()) mArtist.set(tag.getArtist());

                String album = tag.getAlbum(); // The album
                if(album != null && !album.isEmpty()) mAlbum.set(album);

                byte[] imageData = tag.getAlbumImage(); // The cover art
                if(imageData != null && imageData.length > 0) {
                    mCoverArt.set(new Image(new ByteArrayInputStream(imageData)));
                }
            }
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
    }

    public void changeCoverArt(File file) {
        final String RETAG = ".retag";
        final String BACKUP = ".bak";

        if(mMp3File == null)
            return;

        try {
            if (mMp3File.hasId3v2Tag()) {
                ID3v2 tag = mMp3File.getId3v2Tag();

                // Sets the new image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage img = ImageIO.read(file);
                ImageIO.write(img, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();
                tag.setAlbumImage(imageData, "image/jpeg");
                mMp3File.setId3v2Tag(tag);

                // Refreshes the song file by saving it again
                mMp3File.save(mFilePath + RETAG);
                XFile originalFile = new XFile(mFilePath);
                XFile backupFile = new XFile(mFilePath + BACKUP);
                XFile retaggedFile = new XFile(mFilePath + RETAG);
                if (backupFile.exists())
                    backupFile.delete();

                originalFile.renameTo(backupFile);
                retaggedFile.renameTo(originalFile);

                if (backupFile.exists())
                    backupFile.delete();

                // Gets the image data from the newly saved file
                byte[] newImageData = tag.getAlbumImage();
                if(newImageData != null && newImageData.length > 0) {
                    mCoverArt.set(new Image(new ByteArrayInputStream(newImageData)));
                }
            }
        } catch (NotSupportedException | IOException e) {
            e.printStackTrace();
        }
    }

    /*******************************
     * Getters
     *******************************/

    public String getTitle() {
        return mTitle.get();
    }

    public String getArtist() {
        return mArtist.get();
    }

    public String getAlbum() {
        return mAlbum.get();
    }

    public Image getCoverArt() {
        return mCoverArt.get();
    }

    public String getFilePath() {
        return mFilePath;
    }

    /*******************************
     * Properties
     *******************************/

    public StringProperty getTitleProperty() {
        return mTitle;
    }

    public StringProperty getArtistProperty() {
        return mArtist;
    }

    public StringProperty getAlbumProperty() {
        return mAlbum;
    }

    public ObjectProperty<Image> getCoverArtProperty() {
        return mCoverArt;
    }
}
