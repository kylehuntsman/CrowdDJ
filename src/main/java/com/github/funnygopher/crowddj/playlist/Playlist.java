package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.util.SearchParty;
import javafx.collections.ObservableList;

import java.io.File;

public interface Playlist {

    void add(File file);
    void remove(Song song);
    void clear();
    int size();
    String toXML();

    int indexOf(Song song);
    Song getItem(int index);
    Song getNextItem(Song song);
    Song getRandomItem();
    ObservableList<Song> getItems();

    SearchParty<Song> search(File file);
    void updateDatabaseTable();
}
