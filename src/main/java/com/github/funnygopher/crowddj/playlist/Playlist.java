package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.database.Database;
import com.github.funnygopher.crowddj.util.SearchParty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;

public interface Playlist {

    void add(Song song);
    void add(File file);
    void add(List<Song> songs);
    void remove(Song song);
    void clear();
    int size();

    int vote(File file);
    int vote(Song song);

    SearchParty<Song> search(File file);
    String toXML();
    void updateDbTable(Database database);
    ObservableList<Song> getObservableList();
}
