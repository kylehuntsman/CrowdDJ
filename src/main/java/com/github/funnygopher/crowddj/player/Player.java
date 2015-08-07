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

    void setOnPlay(Runnable runnable);
    void setOnPause(Runnable runnable);
    void setOnStop(Runnable runnable);
    void setOnNext(Runnable runnable);

    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    ReadOnlyBooleanProperty loopProperty();
    ReadOnlyBooleanProperty shuffleProperty();
    ReadOnlyDoubleProperty currentTimeProperty();
    ReadOnlyDoubleProperty durationProperty();
    ReadOnlyObjectProperty<Song> currentSongProperty();

    void turnOff();
}
