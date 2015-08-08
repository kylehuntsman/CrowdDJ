package com.github.funnygopher.crowddj.player;

import com.github.funnygopher.crowddj.playlist.Song;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface Player {

    void play();
    void play(Song song);
    void pause();
    void stop();
    void next();
    void loop();
    void shuffle();

    ReadOnlyBooleanProperty playingProperty();
    ReadOnlyBooleanProperty pausedProperty();
    ReadOnlyBooleanProperty stoppedProperty();

    ReadOnlyBooleanProperty loopProperty();
    ReadOnlyBooleanProperty shuffleProperty();
    ReadOnlyDoubleProperty currentTimeProperty();
    ReadOnlyDoubleProperty durationProperty();
    ReadOnlyObjectProperty<Song> currentSongProperty();

    void reset();
}
