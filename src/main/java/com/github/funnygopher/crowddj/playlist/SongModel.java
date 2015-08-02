package com.github.funnygopher.crowddj.playlist;

import javafx.beans.property.SimpleStringProperty;

public class SongModel {

    private final SimpleStringProperty title;
    private final SimpleStringProperty artist;

    private SongModel(Song song) {
        this.title = new SimpleStringProperty(song.getTitle());
        this.artist = new SimpleStringProperty(song.getArtist());
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }
}
