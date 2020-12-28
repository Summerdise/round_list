package com.example.myapplication;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class MediaPlayerActivity extends WearableActivity {

    @BindView(R.id.btn_prev)
    ImageButton prevButton;
    @BindView(R.id.btn_play)
    ImageButton playButton;
    @BindView(R.id.btn_next)
    ImageButton nextButton;
    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.artist_title)
    TextView artistTitle;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.now_position)
    TextView nowPosition;
    @BindView(R.id.max_position)
    TextView maxPosition;

    MediaPlayer mMediaPlayer;
    MediaMetadataRetriever mMediaMetadataRetriever;
    List<Integer> musicList;
    int musicNowTag;
    boolean isBegin;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        musicList = initMusicList();
        mMediaPlayer = new MediaPlayer();
        mMediaMetadataRetriever = new MediaMetadataRetriever();
        isBegin = false;

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
                mp.seekTo(mMediaPlayer.getCurrentPosition());
                seekBar.setMax(mMediaPlayer.getDuration());
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);
    }

    public void switchMusic() {
        stopMediaPlayer();
        timer = new Timer();
        int nowTag = Math.abs(musicNowTag % musicList.size());
        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/" + musicList.get(nowTag));
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mUri);
            mMediaPlayer.prepare();
            getTitleShowing(mUri);
            getTimeShowing();
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

    public List<Integer> initMusicList() {
        List<Integer> musicList = new ArrayList<>();
        musicList.add(R.raw.room);
        musicList.add(R.raw.fast);
        musicList.add(R.raw.littleforever);
        musicList.add(R.raw.loveyou);
        return musicList;
    }

    class PlayButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mMediaPlayer.isPlaying()) {
                playButton.setImageResource(R.drawable.play);
                timer.purge();
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
            timer.purge();
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
            timer.purge();
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
            getTimeShowing();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.seekTo(seekBar.getProgress());
            nowPosition.setText(calculateTime(mMediaPlayer.getCurrentPosition() / 1000));
        }
    }

    public void getTitleShowing(Uri uri){
        mMediaMetadataRetriever.setDataSource(getApplicationContext(),uri);
        String songTitle = mMediaMetadataRetriever.extractMetadata(METADATA_KEY_TITLE);
        String artist = mMediaMetadataRetriever.extractMetadata(METADATA_KEY_ARTIST);
        musicTitle.setText(songTitle);
        artistTitle.setText(artist);
    }
    public void getTimeShowing(){
        int maxTime = mMediaPlayer.getDuration() / 1000;//获取音乐总时长
        int nowTime = mMediaPlayer.getCurrentPosition();//获取当前播放的位置
        nowPosition.setText(calculateTime(nowTime / 1000));//开始时间
        maxPosition.setText(calculateTime(maxTime));
    }

    public String calculateTime(int time){
        int minute;
        int second;
        if(time > 60){
            minute = time / 60;
            second = time % 60;
            if(minute >= 0 && minute < 10){
                if(second >= 0 && second < 10){
                    return "0"+minute+":"+"0"+second;
                }else {
                    return "0"+minute+":"+second;
                }
            }else {
                if(second >= 0 && second < 10){
                    return minute+":"+"0"+second;
                }else {
                    return minute+":"+second;
                }
            }
        }else if(time < 60){
            second = time;
            if(second >= 0 && second < 10){
                return "00:"+"0"+second;
            }else {
                return "00:"+ second;
            }
        }
        return null;
    }
}