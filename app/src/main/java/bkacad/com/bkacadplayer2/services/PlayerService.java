package bkacad.com.bkacadplayer2.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import bkacad.com.bkacadplayer2.domains.Song;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    MediaPlayer mediaPlayer;
    ArrayList<Song> playList;
    int activeSongIndex;
    boolean isShuffle = false, isRepeat = false;

    public static final String ACTION_PLAY = "bkacad.com.bkacadplayer2.ACTION_PLAY";
    public static final String ACTION_PAUSE = "bkacad.com.bkacadplayer2.ACTION_PAUSE";

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer != null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    public void setPlayList(ArrayList<Song> playList, int activeSongIndex) {
        this.playList = playList;
        this.activeSongIndex = activeSongIndex;
        play();
    }

    //choi nhac
    public void play() {
        if (mediaPlayer != null) {
            release();
        }
        if (playList.size() > 0) {
            Song playSong = playList.get(this.activeSongIndex);
            playSong.setActive(true);

            String path = playSong.getPath();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
            mediaPlayer.start();

            Intent intent = new Intent(ACTION_PLAY);

            intent.putExtra("SONG_NAME", playSong.getName());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Intent intent = new Intent(ACTION_PLAY);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Intent intent = new Intent(ACTION_PAUSE);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public void next() {
        if (activeSongIndex < playList.size() - 1) {
            activeSongIndex++;
        }
        play();
    }

    public void prev() {
        if (activeSongIndex >= 1) {
            activeSongIndex--;
        }
        play();
    }

    public void setShuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public void setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public Service getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isShuffle) {
            //tron bai
            this.activeSongIndex = (int) Math.random() * playList.size() + 1;//(1-3)
            play();
        }

        if (isRepeat) {
            //play again
            play();
        }
    }
}
