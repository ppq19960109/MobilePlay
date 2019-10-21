package com.mobileplay.mediaPlay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.aidl.AudioMediaController;
import com.mobileplay.doamain.IMusicService;
import com.mobileplay.doamain.MediaItem;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    private AudioMediaController audioMediaController;
    //seekbar
    private int mediaDuration;
    private int currentMediaPosition;

    private Handler handler = new mHandler(this);
    private final int MEDIA_PREPARED_TIMER = 1;


    public static class mHandler extends Handler {
        private WeakReference<Object> mWeakReference;

        public mHandler(Object object) {
            mWeakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AudioPlayer mReference = (AudioPlayer) mWeakReference.get();
            mReference.mHandleMessage(msg);
        }
    }

    public void mHandleMessage(Message msg) {
        switch (msg.what) {
            case MEDIA_PREPARED_TIMER:
                setMediaProgress();

                handler.removeMessages(MEDIA_PREPARED_TIMER);
                handler.sendEmptyMessageDelayed(MEDIA_PREPARED_TIMER, 1000);
                break;

        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMusicService.Stub.asInterface(service);
            initMusicService();
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

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
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
//        intent.setClass(this, MusicService.class);
        startService(intent);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public static void stopMusicService(Context context) {
        Intent intent = new Intent();
        intent.setPackage("com.mobileplay");
        intent.setAction("com.mobileplay.service.action");

        context.stopService(intent);
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

    private void initMusicService() {
        try {
            if (musicService != null) {
                audioMediaController = musicService.getAudioMediaController();
                if (audioMediaController.getMediaItems() == null) {
                    audioMediaController.setMediaItems(mediaItems);
                }
                audioMediaController.setPosition(position);
                audioMediaController.setOnPreparedListener(new AudioMediaController.OnPreparedListener() {
                    @Override
                 public void OnPrepared() {
                        initSeekBar();
                        handler.sendEmptyMessage(MEDIA_PREPARED_TIMER);
                    }
                });
                audioMediaController.openAudio();

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * SeekBar
     */
    private void initSeekBar() {
        mediaDuration = audioMediaController.getDuration();
        sb_audio.setMax(mediaDuration);

    }

    private void setMediaProgress() {
        currentMediaPosition = audioMediaController.getCurrentPosition();
        sb_audio.setProgress(currentMediaPosition);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        tv_time.setText(simpleDateFormat.format(currentMediaPosition) + "/" + simpleDateFormat.format(mediaDuration));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_audio_playmode:

                break;
            case R.id.btn_pre:

                break;
            case R.id.btn_play_start_pause:
                if (audioMediaController.startAndPause()) {
                    btn_play_start_pause.setBackgroundResource(R.drawable.btn_play_pause_selector);
                } else {
                    btn_play_start_pause.setBackgroundResource(R.drawable.btn_play_start_selector);
                }
                break;
            case R.id.btn_next:

                break;
            case R.id.btn_lyrics:

                break;
        }
    }


}
