package com.mobileplay.mediaPlay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.doamain.IMusicService;
import com.mobileplay.doamain.MediaItem;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

public class AudioPlayer extends Activity implements View.OnClickListener {
    private final String MEDIA_LIST = "AudioList";
    private final String MEDIA_POSITION = "position";

    private ImageView iv_icon;
    private TextView tv_artist;
    private TextView tv_audio_name;
    private TextView tv_time;
    private SeekBar sb_audio;
    private Button btn_audio_playmode;
    private Button btn_pre;
    private Button btn_play_start_pause;
    private Button btn_next;
    private Button btn_lyrics;

    private IMusicService musicService;
    private List<MediaItem> mediaItems;
    private int position;
    private Uri uri;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMusicService.Stub.asInterface(service);
            Log.e("TAG", "IMusicService");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);
        initView();
        initListener();
        initData();

    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_audio_name = (TextView) findViewById(R.id.tv_audio_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        sb_audio = (SeekBar) findViewById(R.id.sb_audio);
        sb_audio.setOnClickListener(this);
        btn_audio_playmode = (Button) findViewById(R.id.btn_audio_playmode);
        btn_audio_playmode.setOnClickListener(this);
        btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_pre.setOnClickListener(this);
        btn_play_start_pause = (Button) findViewById(R.id.btn_play_start_pause);
        btn_play_start_pause.setOnClickListener(this);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_lyrics = (Button) findViewById(R.id.btn_lyrics);
        btn_lyrics.setOnClickListener(this);
    }

    private void initListener() {
        btn_audio_playmode.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_play_start_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_lyrics.setOnClickListener(this);
    }

    private void initData() {
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getDrawable();
        animationDrawable.start();

        bindService();
        initMediaPlay();
    }
    private void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.mobileplay");
        intent.setAction("com.mobileplay.service.action");
//        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void initMediaPlay() {
        Serializable list = getIntent().getSerializableExtra(MEDIA_LIST);
        if (list != null) {
            mediaItems = (List) list;
        }
        if (mediaItems != null && mediaItems.size() > 0) {
            position = getIntent().getIntExtra(MEDIA_POSITION, 0);

        } else {
            uri = getIntent().getData();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_audio_playmode:

                break;
            case R.id.btn_pre:

                break;
            case R.id.btn_play_start_pause:
                initServiceData();
                break;
            case R.id.btn_next:

                break;
            case R.id.btn_lyrics:

                break;
        }
    }

    private void initServiceData() {
        try {
            if(musicService!=null) {
                musicService.setMediaList(mediaItems);
                musicService.setMediaPosition(position);

                mediaItems = musicService.getMediaList();
                Log.e("TAG", mediaItems.get(0).toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
