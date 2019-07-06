package com.bytedance.videoplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static VideoIjk videoIjk;
    private static SeekBar seekBar;
    private Thread thread;

    MHandler mHandler=new MHandler(this);


    private static final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setMax((int)(videoIjk.getDuration()));
            seekBar.setProgress((int)(videoIjk.getCurrentPosition()));
            Log.d(TAG, "run()123 called"+videoIjk.getDuration()+"  "+videoIjk.getCurrentPosition());
        }
    };

    private static class MHandler extends Handler{
        WeakReference<MainActivity> mainActivityWeakReference;

        public MHandler(MainActivity activity){
            this.mainActivityWeakReference= new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            final MainActivity mainActivity=mainActivityWeakReference.get();
            if (mainActivity!=null){
                Log.d(TAG, "handleMessage() called with: msg = [" + msg + "]");
            }
        }
    }

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){

            videoIjk=findViewById(R.id.ijkPlayer);
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoIjk.start();
                }
            });

            findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoIjk.pause();
                }
            });

            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoIjk.seekTo(20 * 1000);
                }
            });

            seekBar=findViewById(R.id.seekBar);
        } else if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
            videoIjk=findViewById(R.id.ijkPlayer);
            seekBar=findViewById(R.id.seekBar);
        }

        /***
         * 上面的findViewByID
         */
        try{
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        videoIjk.setListener(new VideoPlayerListener());
        videoIjk.setVideo(R.raw.yuminhong);
//        ijkPlayer.setVideoPath(getVideoPath());
        System.out.println("durationMain"+videoIjk.getDuration());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true){
                        mHandler.postDelayed(runnable,50);
                        sleep(50);
                    }
                }catch (InterruptedException ie){

                }
            }
        });
        thread.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() called with: seekBar = [" + seekBar + "], progress = [" + progress + "], "+"["+(long)(progress)+"fromUser = [" + fromUser + "]"+videoIjk.getDuration());
                if (fromUser){
                    videoIjk.pause();
                    videoIjk.seekTo((long)(progress));
                    mHandler.removeMessages(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch() called with: seekBar = [" + seekBar + "]");
                mHandler.removeMessages(0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
                mHandler.removeMessages(0);
                videoIjk.start();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() duration called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() duration called");
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (videoIjk.isPlaying()) {
            videoIjk.stop();
        }

        IjkMediaPlayer.native_profileEnd();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        thread.interrupt();
    }

}
