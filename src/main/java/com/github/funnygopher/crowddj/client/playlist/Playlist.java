package com.github.funnygopher.crowddj.client.playlist;

import com.github.funnygopher.crowddj.client.song.Song;

public interface Playlist {
    boolean add(Song song);
    Song getNextSong();
    boolean isEmpty();
    String toJson();
}
