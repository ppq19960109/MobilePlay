package com.mobileplay.mediaPlay.VideoPlay.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mobileplay.Interface.IBatteryChanged;
import com.mobileplay.R;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.mediaPlay.MediaPlayerUtils;
import com.mobileplay.mediaPlay.Receiver.BatteryChangedReceiver;
import com.mobileplay.mediaPlay.VideoPlay.vitamio.VitamioVideoPlayer;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private final String MEDIA_LIST = "VideoList";
    private final String MEDIA_POSITION = "position";
    /**
     * VideoPlayer
     */
    private VideoView video_view;
    private RelativeLayout media_controller;
    private View ll_buffer;
    private View ll_loading;
    /**
     * media_controller
     */
    /**
     * MediaController Visibility
     */
    private boolean isMediaControllerVisibility;
    private final int SHOW_DELAY_HIDE = 1;
    private final int HIDE = 2;
    private final int SHOW = 3;
    //上一半
    private TextView tv_name;
    //电池
    private ImageView iv_battery;
    private TextView tv_battery;
    private TextView tv_real_time_net_speed;
    private TextView tv_system_time;
    private Button btn_voice;
    private SeekBar sb_voice;
    //下一半
    private TextView tvCurrentTime;
    private SeekBar sbVideo;
    private TextView tvDuration;
    private Button btn_next;
    private Button btn_pre;
    private Button btn_play_start_pause;
    private Button btn_full_screen;

    /**
     * ll_buffer
     */
    private TextView tv_netspeed;
    /**
     * ll_loading
     */
    private TextView tv_loading_netspeed;
    /**
     * 电池
     */
    private BatteryChangedReceiver batteryChangedReceiver;
    /**
     * 视频信息
     */
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private Uri uri;
    private boolean isNet;
    private long videoDuration;
    private long currentVideoPosition;
    /**
     * 手势
     */
    private GestureDetector gestureDetector;
    //滑动调音
    private boolean firstScroll;
    private int scrollFlag;
    private int screenWidth;
    private int screenHeight;
    /**
     * 屏幕、全屏
     */
    private boolean isFullScreen;
    private int mVideoWidth;
    private int mVideoHeight;
    /**
     * 声音控制
     */
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private int scrollCurrentVolume;
    private boolean isMute;

    /**
     * 缓冲监听
     */
    private boolean isSystemBufferListener = true;
    private long preCurrentPosition;
    private boolean bufferListenerStart;

    private Handler handler = new mHandler(this);
    private final int MEDIA_PREPARED_TIMER = 1;
    private final int MediaController_HIDE = 2;
    private final int NET_SPEED = 3;

    public static class mHandler extends Handler {
        private WeakReference<Object> mWeakReference;

        public mHandler(Object object) {
            mWeakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SystemVideoPlayer mReference = (SystemVideoPlayer) mWeakReference.get();
            mReference.mHandleMessage(msg);
        }
    }

    public void mHandleMessage(Message msg) {
        switch (msg.what) {
            case MEDIA_PREPARED_TIMER:
                setVideoProgress();

                setSystemtime();

                bufferListener();

                handler.removeMessages(MEDIA_PREPARED_TIMER);
                handler.sendEmptyMessageDelayed(MEDIA_PREPARED_TIMER, 1000);
                break;
            case MediaController_HIDE:
                setMediaControllerVisibility(false);
                break;
            case NET_SPEED:
                setNetSpeed();

                handler.removeMessages(NET_SPEED);
                handler.sendEmptyMessageDelayed(NET_SPEED, 2000);
                break;
        }
    }

    /**
     * VideoPlayer
     */
    private void setVideoProgress() {
        currentVideoPosition = video_view.getCurrentPosition();
        sbVideo.setProgress((int) (currentVideoPosition));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        tvCurrentTime.setText(simpleDateFormat.format(currentVideoPosition));
    }

    /**
     * media_controller
     */

    private void setMediaControllerVisibility(boolean isShow) {
        if (isShow) {
            media_controller.setVisibility(View.VISIBLE);
        } else {
            media_controller.setVisibility(View.GONE);
        }
    }

    private void setMediaControllerVisibilityHandler(int index) {
        switch (index) {
            case SHOW_DELAY_HIDE:
                handler.removeMessages(MediaController_HIDE);
                handler.sendEmptyMessageDelayed(MediaController_HIDE, 4000);
                isMediaControllerVisibility = true;
                break;
            case HIDE:
                handler.removeMessages(MediaController_HIDE);
                isMediaControllerVisibility = false;
                break;
            case SHOW:
                handler.removeMessages(MediaController_HIDE);
                isMediaControllerVisibility = true;
                break;
        }
        setMediaControllerVisibility(isMediaControllerVisibility);
    }

    //上一半
    private void setSystemtime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String systemtime = simpleDateFormat.format(new Date());
        tv_system_time.setText(systemtime);
    }

    //电池
    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryChangedReceiver = new BatteryChangedReceiver(new BatteryChanged());
        registerReceiver(batteryChangedReceiver, intentFilter);
    }

    private class BatteryChanged implements IBatteryChanged {
        @Override
        public void setBattery(int batteryLevel) {
            if (tv_battery != null) {
                tv_battery.setText(batteryLevel + "%");
            }
            if (iv_battery != null) {
                ImageView view = iv_battery;
                if (batteryLevel <= 10) {
                    view.setImageResource(R.drawable.ic_battery_10);
                } else if (batteryLevel <= 20) {
                    view.setImageResource(R.drawable.ic_battery_20);
                } else if (batteryLevel <= 40) {
                    view.setImageResource(R.drawable.ic_battery_40);
                } else if (batteryLevel <= 60) {
                    view.setImageResource(R.drawable.ic_battery_60);
                } else if (batteryLevel <= 80) {
                    view.setImageResource(R.drawable.ic_battery_80);
                } else if (batteryLevel >= 100) {
                    view.setImageResource(R.drawable.ic_battery_100);
                }
            }
        }
    }

    //调音
    private void setAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setCurrentVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        setVoiceBackground();

        sb_voice.setMax(maxVolume);
        sb_voice.setProgress(getCurrentVolume());

        sb_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updataVoice(progress, false);
                } else {

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setMediaControllerVisibilityHandler(SHOW);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
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
            progress = 0;
        } else {
            setCurrentVolume(progress);
        }
        sb_voice.setProgress(progress);
        setVoiceBackground();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    private void setVoiceBackground() {
        if (getCurrentVolume() == 0 || isMute == true) {
            isMute = true;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_mute_selector);
        } else {
            isMute = false;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_selector);
        }
    }
    //下一半
    //网速设置

    private void setNetSpeed() {
        String netSpeed = MediaPlayerUtils.getNetSpeed(getApplicationInfo().uid);
        tv_real_time_net_speed.setText(netSpeed);
        if (isNet) {
            tv_netspeed.setText("缓冲中..." + netSpeed);
            tv_loading_netspeed.setText("玩命加载中..." + netSpeed);
        }
    }

    //缓冲监听卡
    private void systemBufferListener() {
        if (isSystemBufferListener) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                video_view.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                ll_buffer.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                ll_buffer.setVisibility(View.GONE);
                                break;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void bufferListener() {
        if (isSystemBufferListener) {
            return;
        }
        if (!bufferListenerStart) {
            bufferListenerStart = true;
            return;
        }
        if (video_view.isPlaying()) {
            long buffer = currentVideoPosition - preCurrentPosition;
            preCurrentPosition = currentVideoPosition;
            if (buffer < 500) {
                ll_buffer.setVisibility(View.VISIBLE);
            } else {
                ll_buffer.setVisibility(View.GONE);
            }
        } else {
            ll_buffer.setVisibility(View.GONE);
        }
    }

    //缓冲进度条
    private void systemBufferingUpdateProgress(MediaPlayer mp) {
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                long secondaryProgress = videoDuration * percent / 100;
                sbVideo.setSecondaryProgress((int) (secondaryProgress));
            }
        });
    }

    private void bufferingUpdateProgress() {
        int bufferPercent = video_view.getBufferPercentage();
        int percent = sbVideo.getMax() * bufferPercent / 100;
        sbVideo.setSecondaryProgress(percent);
    }

    public void close() {
        handler.removeCallbacksAndMessages(null);
        if (batteryChangedReceiver != null) {
            unregisterReceiver(batteryChangedReceiver);
            batteryChangedReceiver = null;
        }
        if (video_view != null) {
            video_view.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                break;
        }
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
                Log.i("TAG", "onScroll");
                float mOldX = e1.getX(), mOldY = e1.getY();
                float x = e2.getRawX(), y = e2.getRawY();

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
                setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setScreenFullAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                startAndPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isMediaControllerVisibility) {
                    setMediaControllerVisibilityHandler(HIDE);
                } else {
                    setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

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
            keyDownVolume(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            keyDownVolume(true);
            return true;
        } else {

        }

        return super.onKeyDown(keyCode, event);
    }

    private void keyDownVolume(boolean isAdd) {
        int volume = getCurrentVolume();
        if (isAdd) {
            volume = Math.min(++volume, maxVolume);
        } else {
            volume = Math.max(--volume, 0);
        }
        updataVoice(volume, false);
        setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
    }

    private void initView() {
        /**
         * VideoPlayer
         */
        video_view = (VideoView) findViewById(R.id.video_view);
        /**
         * media_controller
         */
        media_controller = findViewById(R.id.media_controller);
        //上一半
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        tv_system_time = (TextView) findViewById(R.id.tv_system_time);
        sb_voice = (SeekBar) findViewById(R.id.sb_voice);
        tv_real_time_net_speed = findViewById(R.id.tv_real_time_net_speed);
        //下一半
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);
        btn_play_start_pause = findViewById(R.id.btn_play_start_pause);
        btn_full_screen = findViewById(R.id.btn_full_screen);
        btn_voice = findViewById(R.id.btn_voice);
        /**
         * ll_buffer
         */
        ll_buffer = findViewById(R.id.ll_buffer);
        tv_netspeed = findViewById(R.id.tv_netspeed);
        /**
         * ll_loading
         */
        ll_loading = findViewById(R.id.ll_loading);
        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);
    }

    private void initListener() {

        findViewById(R.id.btn_switch).setOnClickListener(this);
        btn_voice.setOnClickListener(this);

        findViewById(R.id.btn_exit).setOnClickListener(this);
        btn_play_start_pause.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_full_screen.setOnClickListener(this);

        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //fromUser是否为手动调节，是则true，否则false
                if (fromUser) {
                    //若为手动调节则执行下面方法
                    if (!isSystemBufferListener) {
                        bufferListenerStart = false;
                        preCurrentPosition = progress;
                    }
                    video_view.seekTo(progress);
                }
                //若不加判定则是否手动调节都会执行下面的方法

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setMediaControllerVisibilityHandler(SHOW);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
            }
        });

    }

    private void initData() {
        getScreenSize();
        registerBatteryReceiver();
        setGestureDetector();
        setAudio();
    }

    private void initMediaPlay() {
        Serializable videoList = getIntent().getSerializableExtra(MEDIA_LIST);
        if (videoList != null) {
            mediaItems = (ArrayList) videoList;
        }
        if (mediaItems != null && mediaItems.size() > 0) {
            position = getIntent().getIntExtra(MEDIA_POSITION, 0);
            startMediaPlay();
        } else {
            uri = getIntent().getData();
            if (uri != null) {
                isNet = CommonUtils.isNetUri(uri.toString());
                tv_name.setText(uri.toString());
                video_view.setVideoURI(uri);
                setButtonState();
            }
        }

        systemBufferListener();
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                systemBufferingUpdateProgress(mp);
                ll_loading.setVisibility(View.GONE);

                mVideoHeight = mp.getVideoHeight();
                mVideoWidth = mp.getVideoWidth();
                setFullScreen(false);

                videoDuration = mp.getDuration();
                sbVideo.setMax((int) (videoDuration));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                tvDuration.setText(simpleDateFormat.format(videoDuration));

                video_view.start();

                setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
                handler.sendEmptyMessage(MEDIA_PREPARED_TIMER);
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                startSwitchVideoPlay();
                return true;
            }
        });

        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                isMute = !isMute;
                int mVolume = 0;
                if (isMute == false) {
                    mVolume = getCurrentVolume();
                    if (mVolume == 0) {
                        mVolume += 1;
                    }
                }
                updataVoice(mVolume, isMute);
                break;
            case R.id.btn_switch:
                showSwitchDialog();
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_pre:
                playPreVideo();
                break;
            case R.id.btn_play_start_pause:
                startAndPause();
                break;
            case R.id.btn_next:
                playNextVideo();
                break;
            case R.id.btn_full_screen:
                setScreenFullAndDefault();
                break;
        }
        setMediaControllerVisibilityHandler(SHOW_DELAY_HIDE);
    }


    //屏幕全屏
    private void getScreenSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
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

            if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth;
            }
            video_view.setVideoSize(width, height);
            btn_full_screen.setBackgroundResource(R.drawable.btn_full_screen_selector);
        }
    }

    private void startAndPause() {
        if (video_view.isPlaying()) {
            video_view.pause();
            btn_play_start_pause.setBackgroundResource(R.drawable.btn_play_start_selector);
        } else {
            video_view.start();
            btn_play_start_pause.setBackgroundResource(R.drawable.btn_play_pause_selector);
        }
    }

    private void playPreVideo() {
        --position;
        ll_loading.setVisibility(View.VISIBLE);
        startMediaPlay();
    }

    private void playNextVideo() {
        ++position;
        ll_loading.setVisibility(View.VISIBLE);
        startMediaPlay();
    }

    private void startMediaPlay() {
        MediaItem mediaItem = mediaItems.get(position);
        String uriData = mediaItem.getData();
        tv_name.setText(mediaItem.getName());
        video_view.setVideoPath(uriData);
        isNet = CommonUtils.isNetUri(uriData);
        setButtonState();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_videoplayer);
        initView();
        initListener();
        initData();
        initMediaPlay();
        handler.sendEmptyMessage(NET_SPEED);
    }

    private void showSwitchDialog() {
        new AlertDialog.Builder(this).setMessage("切换万能播放器？").setTitle("提示").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startSwitchVideoPlay();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void startSwitchVideoPlay() {
        Intent intent = new Intent(this, VitamioVideoPlayer.class);
        if (mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MEDIA_LIST, mediaItems);
            intent.putExtras(bundle);
            intent.putExtra(MEDIA_POSITION, position);
        } else {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }


}
