package com.github.funnygopher.crowddj.playlist;

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

public class SongInformation {

    private StringProperty mTitle;
    private StringProperty mArtist;
    private StringProperty mAlbum;
    private IntegerProperty mDuration;
    private ObjectProperty<Image> mCoverArt;

    private String mFilePath;
    private Mp3File mMp3File;

    public SongInformation(String filePath) {
        mFilePath = filePath;
        File file = new File(mFilePath);

        mTitle = new SimpleStringProperty(this, "mTitle", file.getName());
        mArtist = new SimpleStringProperty(this, "mArtist", "");
        mAlbum = new SimpleStringProperty(this, "mAlbum", "");
        mDuration = new SimpleIntegerProperty(this, "mDuration", 0);

        Image defaultCoverArt = new Image(getClass().getResourceAsStream("/images/default_cover_art.png"));
        mCoverArt = new SimpleObjectProperty<>(this, "mCoverArt", defaultCoverArt);

        try {
            mMp3File = new Mp3File(filePath);
            initMp3Info();
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
    }

    private void initMp3Info() {
        if (mMp3File.hasId3v2Tag()) {
            ID3v2 tag = mMp3File.getId3v2Tag();

            String title = tag.getTitle();
            if(title != null && !title.isEmpty()) mTitle.set(title);

            String artist = tag.getArtist();
            if(artist != null && !artist.isEmpty()) mArtist.set(tag.getArtist());

            String album = tag.getAlbum();
            if(album != null && !album.isEmpty()) mAlbum.set(album);

            mDuration.set(tag.getLength());

            byte[] imageData = tag.getAlbumImage();
            if(imageData != null && imageData.length > 0) {
                mCoverArt.set(new Image(new ByteArrayInputStream(imageData)));
            }
        }
    }

    public void changeCoverArt(File file) {
        String retagExtension = ".retag";
        String backupExtension = ".bak";

        if(mMp3File == null){
            return;
        }

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
                mMp3File.save(mFilePath + retagExtension);

                XFile originalFile = new XFile(mFilePath);
                XFile backupFile = new XFile(mFilePath + backupExtension);
                XFile retaggedFile = new XFile(mFilePath + retagExtension);
                if (backupFile.exists())
                    backupFile.delete();

                originalFile.renameTo(backupFile);
                retaggedFile.renameTo(originalFile);

                if (backupFile.exists())
                    backupFile.delete();

                initMp3Info();
            }
        } catch (NotSupportedException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return mTitle.get();
    }

    public StringProperty titleProperty() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist.get();
    }

    public StringProperty artistProperty() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum.get();
    }

    public StringProperty albumProperty() {
        return mAlbum;
    }

    public int getDuration() {
        return mDuration.get();
    }

    public IntegerProperty durationProperty() {
        return mDuration;
    }

    public Image getCoverArt() {
        return mCoverArt.get();
    }

    public ObjectProperty<Image> coverArtProperty() {
        return mCoverArt;
    }

    public String getFilePath() {
        return mFilePath;
    }
}
