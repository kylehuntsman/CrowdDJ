package com.github.funnygopher.crowddj.playlist;

import com.github.funnygopher.crowddj.song.Song;

public interface Playlist {
    boolean add(Song song);
    Song getNextSong();
    boolean isEmpty();
    String toJson();
}
