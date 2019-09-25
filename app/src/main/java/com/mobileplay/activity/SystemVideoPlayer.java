package com.mobileplay.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.mobileplay.R;

import androidx.annotation.Nullable;

public class SystemVideoPlayer extends Activity {
    private VideoView video_view;
    private Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_videoplayer);
        initView();
        initLocalVideo();
    }

    private void initView() {
        video_view = (VideoView) findViewById(R.id.video_view);

    }
    //播放本地视频
    private void initLocalVideo() {
        //设置有进度条可以拖动快进
        MediaController localMediaController = new MediaController(this);
        video_view.setMediaController(localMediaController);


        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video_view.start();
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        video_view.setVideoURI(getIntent().getData());
    }

}
