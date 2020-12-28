package com.example.myapplication;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlineMediaActivity extends WearableActivity {

    @BindView(R.id.btn_prev)
    ImageButton prevButton;
    @BindView(R.id.btn_play)
    ImageButton playButton;
    @BindView(R.id.btn_next)
    ImageButton nextButton;
    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    MediaPlayer mMediaPlayer;
    List<String> musicList;
    int musicNowTag;
    boolean isBegin;

    private int currentPosition;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_media);
        ButterKnife.bind(this);
        mMediaPlayer = new MediaPlayer();
        isBegin = false;
        musicList = initMusicList();
        playButton.setOnClickListener(new PlayButtonListener());
        prevButton.setOnClickListener(new PrevButtonListener());
        nextButton.setOnClickListener(new NextButtonListener());
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
    }

    public void playMusic() {
        switchMusic();
        playButton.setImageResource(R.drawable.pause);
        playMusicWithTimeBarUpdate();
    }

    public void playMusicWithTimeBarUpdate() {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.seekTo(currentPosition);
                seekBar.setMax(mMediaPlayer.getDuration());
            }
        });
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                    Looper.prepare();
                    LoadingHandler handler = new LoadingHandler();
                    Message msg = Message.obtain();
                    handler.sendMessage(msg);
                    Looper.loop();
                }
            }
        }, 0, 50);
    }

    class LoadingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                musicTitle.setText(currentPosition + "/" + mMediaPlayer.getDuration());
            });
        }
    }

    public void switchMusic() {
        stopMediaPlayer();
        int nowTag = Math.abs(musicNowTag % musicList.size());
        String url = musicList.get(nowTag);
        try {
            mMediaPlayer.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
            System.out.println(url);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = new MediaPlayer();
        }
    }

    public List<String> initMusicList() {
        String[] songs = getResources().getStringArray(R.array.music_list);
        List<String> musicList = new ArrayList<>();
        for (String song : songs) {
            musicList.add(song);
        }
        return musicList;
    }

    class PlayButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mMediaPlayer.isPlaying()) {
                playButton.setImageResource(R.drawable.play);
                //
                currentPosition = mMediaPlayer.getCurrentPosition();
                timer.purge();
                //
                mMediaPlayer.pause();
            } else if (!isBegin) {
                playMusic();
                isBegin = true;
            } else {
                playButton.setImageResource(R.drawable.pause);
                mMediaPlayer.start();
            }
        }
    }

    class PrevButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            musicNowTag--;
            if (mMediaPlayer.isPlaying()) {
                playMusic();
            } else {
                switchMusic();
            }
        }
    }

    class NextButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            musicNowTag++;
            if (mMediaPlayer.isPlaying()) {
                playMusic();
            } else {
                switchMusic();
            }
        }
    }

    class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.seekTo(seekBar.getProgress());
        }
    }
}