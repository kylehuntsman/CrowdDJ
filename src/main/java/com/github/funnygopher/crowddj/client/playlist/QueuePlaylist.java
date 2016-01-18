package com.github.funnygopher.crowddj.client.playlist;

import com.github.funnygopher.crowddj.client.song.Song;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueuePlaylist implements Playlist {

    private Queue<Song> mPlaylist;

    public QueuePlaylist(List<Song> songList) {
        mPlaylist = new LinkedList<>(songList);
    }

    @Override
    public boolean add(Song song) {
        return mPlaylist.add(song);
    }

    @Override
    public Song getNextSong() {
        return mPlaylist.remove();
    }

    @Override
    public boolean isEmpty() {
        return mPlaylist.isEmpty();
    }

    @Override
    public String toJson() {
        return "{}";
    }
}
