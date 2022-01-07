package bkacad.com.bkacadplayer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import bkacad.com.bkacadplayer2.adapters.SongAdapter;
import bkacad.com.bkacadplayer2.domains.Song;
import bkacad.com.bkacadplayer2.helpers.FileManager;
import bkacad.com.bkacadplayer2.services.PlayerService;

public class ActivityPlayer extends AppCompatActivity {
    ImageButton btnPlayOrPause, btnNext, btnPrev;
    RecyclerView rcListMusic;
    SongAdapter songAdapter;
    ArrayList<Song> songs = new ArrayList<>();
    TextView tvPlayingSong;

    PlayerService playerService;

    void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //chưa đc cấp quyền
                //yêu cầu người dùng cáp quyền ghi
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 2);
            } else {
                loadMusic();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            //hiểu là phản hồi của người dùng từ yêu cầu cấp quyền có mã request code là 2
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMusic();
                }
            }
        }
    }

    public void loadMusic() {
        songs = FileManager.loadSongs(new File(Environment.getExternalStorageDirectory().toString()));
        songAdapter = new SongAdapter(ActivityPlayer.this, songs);
        rcListMusic.setAdapter(songAdapter);
        LinearLayoutManager lln = new LinearLayoutManager(ActivityPlayer.this, RecyclerView.VERTICAL, false);
        rcListMusic.setLayoutManager(lln);
        songAdapter.setOnItemClickListener(new SongAdapter.MyItemClickListener() {
            @Override
            public void setOnItemClick(Song song, int activeSongIndex) {
                //goi play nhac
                if (playerService != null) {
                    ArrayList<Song> tmpSongs = new ArrayList<>(songs);//copy ra mot bien tam
                    songs.clear();
                    for (int i = 0; i < tmpSongs.size(); i++) {
                        if (i == activeSongIndex) {
                            tmpSongs.get(i).setActive(true);
                        }else{
                            tmpSongs.get(i).setActive(false);
                        }
                        songs.add(tmpSongs.get(i));
                    }
                    songAdapter.notifyDataSetChanged();
                    playerService.setPlayList(songs, activeSongIndex);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        rcListMusic = findViewById(R.id.rcListMusic);
        btnPlayOrPause = findViewById(R.id.btnPlayOrPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        tvPlayingSong = findViewById(R.id.tvPlayingSong);

        checkPermission();

        Intent intent = new Intent(ActivityPlayer.this, PlayerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_PLAY);
        intentFilter.addAction(PlayerService.ACTION_PAUSE);

        LocalBroadcastManager.getInstance(ActivityPlayer.this).registerReceiver(broadcastReceiver, intentFilter);

        btnPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerService != null) {
                    if (playerService.isPlaying()) {
                        playerService.pause();
                    } else {
                        playerService.resume();
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerService != null) {
                    playerService.next();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerService != null) {
                    playerService.prev();
                }
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PlayerService.ACTION_PLAY:
                    if (intent.hasExtra("SONG_NAME")) {
                        //get song name
                        String songName = intent.getStringExtra("SONG_NAME");
                        tvPlayingSong.setText(songName);
                    }
                    btnPlayOrPause.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_circle_outline_24));
                    break;

                case PlayerService.ACTION_PAUSE:
                    btnPlayOrPause.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_circle_outline_24));
                    break;
            }
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MyBinder binder = (PlayerService.MyBinder) service;
            playerService = (PlayerService) binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}