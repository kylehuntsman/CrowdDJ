package com.github.funnygopher.crowddj.playlist;

import javafx.beans.property.SimpleStringProperty;

public class SongModel {

    private SimpleStringProperty title, artist;

    public SongModel(String title, String artist) {
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }
}
