package com.bytedance.videoplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoIjk extends FrameLayout {
    private static final String TAG = "VideoIjk";

    private IMediaPlayer iMediaPlayer;
    private boolean hascreatedSurfaceView=false;
    private Context context;
    private SurfaceView surfaceView;
    private VideoPlayerListener listener;

    public VideoIjk(@NonNull Context context){
        super(context);
        initVideoView(context);
    }
    public VideoIjk(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public VideoIjk(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(Context context){
        this.context=context;
        Log.d(TAG, "initVideoView() duration called with: context = [" + context + "]");
        setFocusable(true);
    }

    public void setVideo(int id){
        createSurfaceView();
        Log.d(TAG, "setVideo() duration called with: id = [" + id + "]");
        load(id);
    }

    private void createSurfaceView(){
        if (hascreatedSurfaceView){
//            return;
        }
        surfaceView=new SurfaceView(context);
        surfaceView.getHolder().addCallback(new PlayerSurfaceCallBack());
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(layoutParams);
        this.addView(surfaceView);
        hascreatedSurfaceView=true;
        Log.d(TAG, "createSurfaceView() duration called");
    }

    private class PlayerSurfaceCallBack implements SurfaceHolder.Callback{
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder){
            iMediaPlayer.setDisplay(surfaceHolder);
            iMediaPlayer.prepareAsync();
            Log.d(TAG, "surfaceCreated() duration called with: surfaceHolder = [" + surfaceHolder + "]"+iMediaPlayer.getDuration());
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder,int formant,int width,int height){

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder){

        }
    }

    private void load(int id){
        createPlayer();
        AssetFileDescriptor fileDescriptor=context.getResources().openRawResourceFd(id);
        RawDataSourceProvider provider=new RawDataSourceProvider(fileDescriptor);
        iMediaPlayer.setDataSource(provider);
        Log.d(TAG, "load() duration called with: id = [" + id + "]");
    }

    private void createPlayer() {
        if (iMediaPlayer != null) {
            iMediaPlayer.stop();
            iMediaPlayer.setDisplay(null);
            iMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
//        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

        iMediaPlayer = ijkMediaPlayer;
        ((IjkMediaPlayer) iMediaPlayer).setSpeed(1f);

        if (listener != null) {
            ijkMediaPlayer.setOnPreparedListener(listener);
            ijkMediaPlayer.setOnInfoListener(listener);
            ijkMediaPlayer.setOnSeekCompleteListener(listener);
            ijkMediaPlayer.setOnBufferingUpdateListener(listener);
            ijkMediaPlayer.setOnErrorListener(listener);
            Log.d(TAG, "createPlayer() duration called"+iMediaPlayer.getDuration());
        }
    }

    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
        if (iMediaPlayer != null) {
            iMediaPlayer.setOnPreparedListener(listener);
        }
    }

    public void start() {
        if (iMediaPlayer != null) {
            iMediaPlayer.start();
        }
    }

    public void release() {
        if (iMediaPlayer != null) {
            iMediaPlayer.reset();
            iMediaPlayer.release();
            iMediaPlayer = null;
        }
    }

    public void pause() {
        if (iMediaPlayer != null) {
            iMediaPlayer.pause();
        }
    }

    public void stop() {
        if (iMediaPlayer != null) {
            iMediaPlayer.stop();
        }
    }


    public void reset() {
        if (iMediaPlayer != null) {
            iMediaPlayer.reset();
        }
    }


    public long getDuration() {
        if (iMediaPlayer != null) {

            return iMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public long getCurrentPosition(){
        if (iMediaPlayer!=null){
            return iMediaPlayer.getCurrentPosition();
        }else{
            return 0;
        }
    }

    public boolean isPlaying() {
        if (iMediaPlayer != null) {
            return iMediaPlayer.isPlaying();
        }
        return false;
    }

    public void seekTo(long l) {
        if (iMediaPlayer != null) {
            iMediaPlayer.seekTo(l);
        }
    }

}
