 package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

 public class MusicPlayer extends AppCompatActivity {
    TextView titleview,currenttime,totaltime;
    SeekBar seekBar;
    ImageView pause,next,previous,iconbig;
    ArrayList<AudioModel> songsList;
    AudioModel currentsong;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleview=findViewById(R.id.song_title);
        currenttime=findViewById(R.id.current_time);
        totaltime=findViewById(R.id.total_time);
        seekBar=findViewById(R.id.seekbar);
        pause=findViewById(R.id.pause);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        iconbig=findViewById(R.id.music_icon_big);

        songsList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesMusic();

        MusicPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currenttime.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pause.setImageResource(R.drawable.baseline_pause_24);
                        iconbig.setRotation(x++);
                    }
                    else{
                        pause.setImageResource(R.drawable.baseline_play_arrow_24);
                        iconbig.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if(mediaPlayer!=null && b){
                     mediaPlayer.seekTo(i);
                 }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setResourcesMusic(){
        currentsong=songsList.get(MyMediaPlayer.currentIndex);
        titleview.setText(currentsong.getTitle());
        totaltime.setText(convertToMMSS(currentsong.getDuration()));

        pause.setOnClickListener(view -> pauseMusic());
        next.setOnClickListener(view -> next());
        previous.setOnClickListener(view -> previous());

        titleview.setSelected(true);

        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentsong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration() );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
     private void pauseMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else {
            mediaPlayer.start();
        }
     }
     private void next(){

        if(MyMediaPlayer.currentIndex==songsList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourcesMusic();
     }
     private void previous(){
         if(MyMediaPlayer.currentIndex==0){
             return;
         }
         MyMediaPlayer.currentIndex-=1;
         mediaPlayer.reset();
         setResourcesMusic();
     }

    public static String convertToMMSS(String duration){
        long millis=Long.parseLong(duration);
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

    }
}