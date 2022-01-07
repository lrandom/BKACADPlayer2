package bkacad.com.bkacadplayer2.helpers;

import java.io.File;
import java.util.ArrayList;

import bkacad.com.bkacadplayer2.domains.Song;

public class FileManager {
    static ArrayList<Song> songs = new ArrayList<>();

    public static final ArrayList<Song> loadSongs(File rootDir) {
        File[] files = rootDir.listFiles();//danh sach file
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    loadSongs(file);
                } else if (file.getName().endsWith(".mp3")
                        || file.getName().endsWith(".wav") || file.getName().endsWith(".ogg")) {
                    Song song = new Song();
                    song.setName(file.getName());
                    song.setPath(file.getAbsolutePath());
                    songs.add(song);
                }
            }
        }
        return songs;
    }
}
