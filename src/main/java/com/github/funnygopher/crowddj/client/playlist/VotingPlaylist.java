package com.github.funnygopher.crowddj.client.playlist;

import com.github.funnygopher.crowddj.client.song.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingPlaylist implements Playlist {

    private Map<Song,Integer> mPlaylist;

    public VotingPlaylist(List<Song> songList) {
        mPlaylist = new HashMap<Song, Integer>();

        for (Song song : songList) {
            mPlaylist.put(song, 0);
        }
    }

    @Override
    public boolean add(Song song) {
        mPlaylist.put(song, 0);
        return true;
    }

    @Override
    public Song getNextSong() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toJson() {
        return null;
    }
}
