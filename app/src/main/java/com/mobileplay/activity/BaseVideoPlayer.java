package com.mobileplay.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
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

import com.mobileplay.R;
import com.mobileplay.Receiver.BatteryChangedReceiver;
import com.mobileplay.Receiver.IBatteryChanged;
import com.mobileplay.base.BasePager;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.view.VideoView;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class BaseVideoPlayer extends Activity implements View.OnClickListener {

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
    private int videoDuration;
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
    private boolean isUseSystem = true;
    private int preCurrentPosition;
    private boolean bufferListenerStart;
    /**
     * 网速
     */
    private long lastTotalRxBytes;
    private long lastTimeStamp;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private Handler handler = new mHandler(this);
    public static class mHandler extends Handler {
        private WeakReference<Object> mWeakReference;

        public mHandler(Object object) {
            mWeakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseVideoPlayer mReference = (BaseVideoPlayer) mWeakReference.get();
            mReference.mHandleMessage(msg);
        }
    }

    public void mHandleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                int currentPosition = video_view.getCurrentPosition();
                sbVideo.setProgress(currentPosition);

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                tvCurrentTime.setText(simpleDateFormat.format(currentPosition));

                setSystemtime();

                bufferListener(currentPosition);

                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, 1000);
                break;
            case 2:
                setMediaControllerVisibility(false);
                break;
            case 3:
                setNetSpeed();

                handler.removeMessages(3);
                handler.sendEmptyMessageDelayed(3, 2000);
                break;
        }
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
    /**
     * VideoPlayer
     */

    /**
     * media_controller
     */
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

    //下一半
    //网速设置
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    public String getNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes(getApplicationInfo().uid);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return String.valueOf(speed) + " kb/s";
    }

    private void setNetSpeed() {
        String netSpeed = getNetSpeed();
        tv_real_time_net_speed.setText(netSpeed);
        if (isNet) {
            tv_netspeed.setText("缓冲中..." + netSpeed);
            tv_loading_netspeed.setText("玩命加载中..." + netSpeed);
        }
    }

    //缓冲监听卡
    private void systemBufferListener() {
        if (isUseSystem) {
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

    private void bufferListener(int currentPosition) {
        if (isUseSystem) {
            return;
        }
        if (!bufferListenerStart) {
            bufferListenerStart = true;
            return;
        }
        if (video_view.isPlaying()) {
            int buffer = currentPosition - preCurrentPosition;
            preCurrentPosition = currentPosition;
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
                int secondaryProgress = videoDuration * percent / 100;
                sbVideo.setSecondaryProgress(secondaryProgress);
            }
        });
    }
    private void bufferingUpdateProgress() {
        int bufferPercentage = video_view.getBufferPercentage();
        int percent = sbVideo.getMax() * bufferPercentage / 100;
        sbVideo.setSecondaryProgress(percent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initLocalVideo();
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

        } else {

        }

        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        video_view = (VideoView) findViewById(R.id.video_view);

        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        tv_system_time = (TextView) findViewById(R.id.tv_system_time);
        sb_voice = (SeekBar) findViewById(R.id.sb_voice);

        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);
        btn_full_screen = findViewById(R.id.btn_full_screen);
        btn_voice = findViewById(R.id.btn_voice);
        tv_real_time_net_speed = findViewById(R.id.tv_real_time_net_speed);

        media_controller = findViewById(R.id.media_controller);

        ll_buffer = findViewById(R.id.ll_buffer);
        tv_netspeed = findViewById(R.id.tv_netspeed);
        ll_loading = findViewById(R.id.ll_loading);
        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);
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
                    if (!isUseSystem) {
                        bufferListenerStart = false;
                        preCurrentPosition = progress;
                    }
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

        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video_view.start();
                mVideoHeight = mp.getVideoHeight();
                mVideoWidth = mp.getVideoWidth();

                setFullScreen(false);

                HandlerMediaControllerShowAndHide(1);

                videoDuration = mp.getDuration();
                sbVideo.setMax(videoDuration);

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                tvDuration.setText(simpleDateFormat.format(videoDuration));

                ll_loading.setVisibility(View.GONE);
                handler.sendEmptyMessage(1);
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                startVitamioVideoPlay();
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
                isNet = isNetUri(uri.toString());
                tv_name.setText(uri.toString());
                video_view.setVideoURI(uri);
            }
        } else {
            position = getIntent().getIntExtra("position", 0);
            MediaItem mediaItem = mediaItems.get(position);
            isNet = isNetUri(mediaItem.getData());
            tv_name.setText(mediaItem.getName());
            video_view.setVideoPath(mediaItem.getData());
        }

        setButtonState();
        handler.sendEmptyMessage(3);
//        startVitamioVideoPlay();
    }

    private void startVitamioVideoPlay() {
//        if(video_view!=null){
//            video_view.stopPlayback();
//        }
        Intent intent = new Intent(this, VitamioVideoPlayer.class);
        if (mediaItems == null) {
            intent.setData(uri);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("VideoList", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        }
        startActivity(intent);
        finish();
    }

    private boolean isNetUri(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("https")
                || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
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
                showSwitchDialog();
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

    private void showSwitchDialog() {
        new AlertDialog.Builder(this).setMessage("切换万能播放器？").setTitle("提示").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startVitamioVideoPlay();
                    }
                }).setNegativeButton("取消", null).show();
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


    private void setAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setCurrentVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        setVoice();

        sb_voice.setMax(maxVolume);
        sb_voice.setProgress(getCurrentVolume());

        sb_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    //若为手动调节则执行下面方法
                    updataVoice(progress, false);
                } else {

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
            sb_voice.setProgress(0);
            btn_voice.setBackgroundResource(R.drawable.btn_voice_mute_selector);
        } else {
            setCurrentVolume(progress);
            setVoice();
            sb_voice.setProgress(progress);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    private void setVoice() {
        if (getCurrentVolume() == 0) {
            isMute = true;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_mute_pressed);
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
        ll_loading.setVisibility(View.VISIBLE);
        MediaItem mediaItem = mediaItems.get(position);
        isNet = isNetUri(mediaItem.getData());
        tv_name.setText(mediaItem.getName());
        video_view.setVideoPath(mediaItem.getData());

        setButtonState();
    }

    private void playNextVideo() {
        if (position + 1 < mediaItems.size()) {
            ++position;
            ll_loading.setVisibility(View.VISIBLE);
            MediaItem mediaItem = mediaItems.get(position);
            isNet = isNetUri(mediaItem.getData());
            tv_name.setText(mediaItem.getName());
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
