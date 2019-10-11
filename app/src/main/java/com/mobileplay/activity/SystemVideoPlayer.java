package com.mobileplay.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.mobileplay.R;
import com.mobileplay.Receiver.BatteryChangedReceiver;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.view.VideoView;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.Vitamio;


public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private VideoView video_view;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tv_battery;
    private TextView tvSystemTime;
    private SeekBar sbVoice;
    private LinearLayout llBottom;
    private RelativeLayout media_controller;
    private RelativeLayout system_videoplay;
    private TextView tvCurrentTime;
    private SeekBar sbVideo;
    private TextView tvDuration;
    private Button btn_next;
    private Button btn_pre;
    private Button btn_full_screen;
    private Button btn_voice;

    private BatteryChangedReceiver batteryChangedReceiver;

    private ArrayList<MediaItem> mediaItems;
    private int position;
    private Uri uri;

    private GestureDetector gestureDetector;

    private boolean isFullScreen;
    private int mVideoWidth;
    private int mVideoHeight;

    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private int scrollCurrentVolume;
    private boolean isMute;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private boolean firstScroll;
    private int scrollFlag;
    private int screenWidth;
    private int screenHeight;

    private int videoDuration;


    private static class MyHandler extends Handler {
        private WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SystemVideoPlayer mActivity = (SystemVideoPlayer) mWeakReference.get();
            switch (msg.what) {
                case 1:
                    int currentPosition = mActivity.video_view.getCurrentPosition();
                    mActivity.sbVideo.setProgress(currentPosition);

                    mActivity.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    mActivity.tvCurrentTime.setText(mActivity.simpleDateFormat.format(currentPosition));

                    mActivity.tvSystemTime.setText(mActivity.getSystemtime());

                    removeMessages(1);
                    sendEmptyMessageDelayed(1, 1000);
                    break;
                case 2:
                    mActivity.setMediaControllerVisibility(false);
                    break;
            }
        }


    }

    private Handler handler = new MyHandler(this);

//    private void updataSecondaryProgress() {
//        int bufferPercentage = video_view.getBufferPercentage();
//        int percent=sbVideo.getMax()*bufferPercentage/100;
//        sbVideo.setSecondaryProgress(percent);
//        Log.i("TAG", "setSecondaryProgress"+bufferPercentage+"="+percent);
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_videoplayer);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        Log.i("TAG", "Vitamio");
        if (Vitamio.isInitialized(this)) {
            Log.i("TAG", "Vitamio isInitialized");
        }
        initView();
        initData();
        initLocalVideo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.i("TAG", "ACTION_UP");
                break;
        }
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (batteryChangedReceiver != null) {
            unregisterReceiver(batteryChangedReceiver);
            batteryChangedReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenSize();
        setFullScreen(isFullScreen);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            int volume = getCurrentVolume();
            updataVoice(Math.max(--volume, 0), false);
            HandlerMediaControllerShowAndHide(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            int volume = getCurrentVolume();
            updataVoice(Math.min(++volume, maxVolume), false);
            HandlerMediaControllerShowAndHide(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            Log.i("TAG", "KEYCODE_VOLUME_MUTE");
        } else {

        }

        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        video_view = (VideoView) findViewById(R.id.video_view);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        sbVoice = (SeekBar) findViewById(R.id.sb_voice);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);
        btn_full_screen = findViewById(R.id.btn_full_screen);
        btn_voice = findViewById(R.id.btn_voice);

        media_controller = findViewById(R.id.media_controller);
        system_videoplay = findViewById(R.id.system_videoplay);
        initListener();
    }

    private void initListener() {
        btn_voice.setOnClickListener(this);
        findViewById(R.id.btn_switch).setOnClickListener(this);
        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //fromUser是否为手动调节，是则true，否则false
                if (fromUser) {
                    //若为手动调节则执行下面方法
                    video_view.seekTo(progress);
                }
                //若不加判定则是否手动调节都会执行下面的方法

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                HandlerMediaControllerShowAndHide(2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                HandlerMediaControllerShowAndHide(1);
            }
        });
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_play_start_pause).setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_full_screen.setOnClickListener(this);
    }

    private void initData() {
        getScreenSize();
        registerBatteryReceiver();
        setGestureDetector();
        setAudio();

    }

    //播放本地视频
    private void initLocalVideo() {
        //设置有进度条可以拖动快进
//        MediaController localMediaController = new MediaController(this);
//        video_view.setMediaController(localMediaController);

        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                            int secondaryProgress = videoDuration * percent / 100;
                            sbVideo.setSecondaryProgress(secondaryProgress);
                    }
                });
                video_view.start();

                mVideoHeight = mp.getVideoHeight();
                mVideoWidth = mp.getVideoWidth();
                setFullScreen(false);

                setMediaControllerVisibility(false);

                 videoDuration = mp.getDuration();
                sbVideo.setMax(videoDuration);

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                tvDuration.setText(simpleDateFormat.format(videoDuration));

                handler.sendEmptyMessage(1);
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("TAG", "onError: MediaPlayer");
                return true;
            }
        });

        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();
            }
        });

        Serializable videoList = getIntent().getSerializableExtra("VideoList");
        if (videoList instanceof ArrayList) {
            mediaItems = (ArrayList) videoList;
        }
//        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("VideoList");
        if (mediaItems == null) {
            uri = getIntent().getData();
            if (uri != null) {
                tvName.setText(uri.toString());
                video_view.setVideoURI(uri);
            }
        } else {
            position = getIntent().getIntExtra("position", 0);
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            video_view.setVideoPath(mediaItem.getData());
        }

        setButtonState();
    }

    private boolean isNetUri(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("https")
                || uri.toLowerCase().startsWith("rtsp")|| uri.toLowerCase().startsWith("mms")) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                //TODO implement
                isMute = !isMute;
                int mVolume = getCurrentVolume();
                if (mVolume == 0 && isMute == false) {
                    mVolume += 1;
                }
                updataVoice(mVolume, isMute);
                break;
            case R.id.btn_switch:
                //TODO implement
                break;
            case R.id.btn_exit:
                //TODO implement
                finish();
                break;
            case R.id.btn_pre:
                //TODO implement
                playPreVideo();
                break;
            case R.id.btn_play_start_pause:
                //TODO implement
                startAndPause();
                break;
            case R.id.btn_next:
                //TODO implement
                playNextVideo();
                break;
            case R.id.btn_full_screen:
                //TODO implement
                setScreenFullAndDefault();
                break;
        }
        HandlerMediaControllerShowAndHide(4);
    }

    private String getSystemtime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(new Date());
    }

    private void getScreenSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }

    private void setGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.i("TAG", "onDown");
                firstScroll = true;
                return super.onDown(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i("TAG", "onFling");

                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float x = e2.getRawX(), y = e2.getRawY();
                Log.i("TAG", "onScroll");
//                Log.i("TAG","OldX:"+mOldX);
//                Log.i("TAG","OldY:"+mOldY);
//                Log.i("TAG","X:"+distanceX);
//                Log.i("TAG","Y:"+distanceY);
                if (firstScroll) {
                    scrollCurrentVolume = getCurrentVolume();
                    setMediaControllerVisibility(true);

                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        scrollFlag = 1;
                    } else {
                        if (mOldX > screenWidth / 2) {
                            scrollFlag = 2;
                        } else {
                            scrollFlag = 3;
                        }
                    }
                }

                firstScroll = false;
                HandlerMediaControllerShowAndHide(4);

                switch (scrollFlag) {
                    case 1:

                        break;
                    case 2:
                        if (Math.abs(distanceY) > Math.abs(distanceX)) {
                            int volume = (int) (maxVolume * (mOldY - y) / screenHeight);
                            updataVoice(Math.min(Math.max(0, scrollCurrentVolume + volume), maxVolume), false);
                        }
                        break;
                    case 3:

                        break;
                    default:
                        break;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i("TAG", "onDoubleTap");
                setScreenFullAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i("TAG", "onLongPress");
                startAndPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i("TAG", "onSingleTapConfirmed");
                if (media_controller.isShown()) {
                    setMediaControllerVisibility(false);
                    HandlerMediaControllerShowAndHide(2);
                } else {
                    setMediaControllerVisibility(true);
                    HandlerMediaControllerShowAndHide(1);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
//        system_videoplay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("TAG","system_videoplay");
//                return false;
//            }
//        });
    }

    private void setMediaControllerVisibility(boolean isShow) {
        if (isShow) {
            media_controller.setVisibility(View.VISIBLE);
        } else {
            media_controller.setVisibility(View.GONE);
        }
    }

    private void HandlerMediaControllerShowAndHide(int index) {
        switch (index) {
            case 1:
                handler.sendEmptyMessageDelayed(2, 4000);
                break;
            case 2:
                handler.removeMessages(2);
                break;
            case 3:
                handler.removeMessages(2);
                handler.sendEmptyMessageDelayed(2, 4000);
                setMediaControllerVisibility(true);
                break;
            case 4:
                handler.removeMessages(2);
                handler.sendEmptyMessageDelayed(2, 4000);
                break;
        }
    }

    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryChangedReceiver = new BatteryChangedReceiver(this);
        registerReceiver(batteryChangedReceiver, intentFilter);
    }

    public void setBattery(int level) {
        tv_battery.setText(level + "%");
        if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level >= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setCurrentVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        setVoice();

        sbVoice.setMax(maxVolume);
        sbVoice.setProgress(getCurrentVolume());

        sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    //若为手动调节则执行下面方法
                    Log.i("TAG", "fromUser");

                    updataVoice(progress, false);
                } else {
                    Log.i("TAG", "not fromUser");

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                HandlerMediaControllerShowAndHide(2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                HandlerMediaControllerShowAndHide(1);
            }
        });
    }

    private int getCurrentVolume() {
        return currentVolume;
    }

    private void setCurrentVolume(int volume) {
        currentVolume = volume;
    }

    private void updataVoice(int progress, boolean mute) {
        if (mute) {
            sbVoice.setProgress(0);
            btn_voice.setBackgroundResource(R.drawable.btn_voice_mute);
        } else {
            setCurrentVolume(progress);
            setVoice();
            sbVoice.setProgress(progress);

        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    private void setVoice() {
        if (getCurrentVolume() == 0) {
            isMute = true;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_mute);
        } else {
            isMute = false;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_selector);
        }
    }


    private void setScreenFullAndDefault() {
        isFullScreen = !isFullScreen;
        setFullScreen(isFullScreen);
    }

    private void setFullScreen(boolean isFullScreen) {

        if (isFullScreen) {
            video_view.setVideoSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            btn_full_screen.setBackgroundResource(R.drawable.btn_default_screen_selector);
        } else {

            int width = screenWidth;
            int height = screenHeight;
            // for compatibility, we adjust size based on aspect ratio
            if (mVideoWidth * height < width * mVideoHeight) {
                //Log.i("@@@", "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            }
            video_view.setVideoSize(width, height);
            btn_full_screen.setBackgroundResource(R.drawable.btn_full_screen_selector);
        }
    }

    private void startAndPause() {
        if (video_view.isPlaying()) {
            video_view.pause();
            findViewById(R.id.btn_play_start_pause).setBackgroundResource(R.drawable.btn_play_start_selector);
        } else {
            video_view.start();
            findViewById(R.id.btn_play_start_pause).setBackgroundResource(R.drawable.btn_play_pause_selector);
        }
    }

    private void playPreVideo() {
        --position;

        MediaItem mediaItem = mediaItems.get(position);
        tvName.setText(mediaItem.getName());
        video_view.setVideoPath(mediaItem.getData());

        setButtonState();
    }

    private void playNextVideo() {
        if (position + 1 < mediaItems.size()) {
            ++position;

            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            video_view.setVideoPath(mediaItem.getData());

            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null) {
            if (1 == mediaItems.size()) {
                btn_next.setBackgroundResource(R.drawable.btn_next_gray);
                btn_next.setEnabled(false);
                btn_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                btn_pre.setEnabled(false);
            } else if (2 == mediaItems.size()) {
                if (position == 0) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_selector);
                    btn_next.setEnabled(true);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btn_pre.setEnabled(false);
                } else {
                    btn_next.setBackgroundResource(R.drawable.btn_next_gray);
                    btn_next.setEnabled(false);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btn_pre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_selector);
                    btn_next.setEnabled(true);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btn_pre.setEnabled(false);
                } else if (position + 1 == mediaItems.size()) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_gray);
                    btn_next.setEnabled(false);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btn_pre.setEnabled(true);
                } else {
                    btn_next.setBackgroundResource(R.drawable.btn_next_selector);
                    btn_next.setEnabled(true);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btn_pre.setEnabled(true);
                }
            }

        } else if (uri != null) {
            btn_next.setBackgroundResource(R.drawable.btn_next_gray);
            btn_next.setEnabled(false);
            btn_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            btn_pre.setEnabled(false);
        } else {

        }
    }


}
