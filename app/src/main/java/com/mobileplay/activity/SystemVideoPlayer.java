package com.mobileplay.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class SystemVideoPlayer extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
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
    private BatteryChangedReceiver batteryChangedReceiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private Uri uri;
    private GestureDetector gestureDetector;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int currentPosition = video_view.getCurrentPosition();
                    sbVideo.setProgress(currentPosition);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                    tvCurrentTime.setText(formatter.format(currentPosition));
                    tvSystemTime.setText(getSystemtime());

                    removeMessages(1);
                    sendEmptyMessageDelayed(1, 1000);
                    break;
                case 2:
                    media_controller.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private String getSystemtime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_videoplayer);
        initView();
        initData();
        initLocalVideo();
    }

    private void initData() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryChangedReceiver = new BatteryChangedReceiver(this);
        registerReceiver(batteryChangedReceiver, intentFilter);
        gestureDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                Log.i("TAG","onDown");
//                return true;
                return super.onDown(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i("TAG","onDoubleTap");
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i("TAG","onLongPress");
                startAndPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i("TAG","onSingleTapUp");
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i("TAG","onSingleTapConfirmed");
                if(media_controller.isShown()) {
                    media_controller.setVisibility(View.GONE);
                    handler.removeMessages(2);
                }else {
                    media_controller.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(2,3000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

//        video_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("TAG","video_view");
//                return false;
//            }
//        });
//        media_controller.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("TAG","media_controller");
//                return false;
//            }
//        });
//        system_videoplay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("TAG","system_videoplay");
//                return false;
//            }
//        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(1);
        if (batteryChangedReceiver != null) {
            unregisterReceiver(batteryChangedReceiver);
            batteryChangedReceiver = null;
        }
        super.onDestroy();
    }

    public void setBattery(int level) {
        tv_battery.setText(Integer.toString(level) + "%");
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

    private void initView() {
        video_view = (VideoView) findViewById(R.id.video_view);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        sbVoice = (SeekBar) findViewById(R.id.sb_voice);
        findViewById(R.id.btn_switch).setOnClickListener(this);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        sbVideo.setOnSeekBarChangeListener(this);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        btn_pre = findViewById(R.id.btn_pre);
        btn_pre.setOnClickListener(this);
        findViewById(R.id.btn_play_start_pause).setOnClickListener(this);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        findViewById(R.id.btn_full_screen).setOnClickListener(this);
        media_controller=findViewById(R.id.media_controller);
        system_videoplay=findViewById(R.id.system_videoplay);
    }


    //播放本地视频
    private void initLocalVideo() {
        //设置有进度条可以拖动快进
//        MediaController localMediaController = new MediaController(this);
//        video_view.setMediaController(localMediaController);


        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video_view.start();
                media_controller.setVisibility(View.GONE);
                int duration = mp.getDuration();
                sbVideo.setMax(duration);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                tvDuration.setText(formatter.format(duration));
                handler.sendEmptyMessage(1);
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
                playNextVideo();
            }
        });
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoadd");
        if (mediaItems == null) {
            uri = getIntent().getData();
            tvName.setText(uri.toString());
            video_view.setVideoURI(uri);
        } else {

            position = getIntent().getIntExtra("position", 0);
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            video_view.setVideoPath(mediaItem.getData());
        }
        setButtonState();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                //TODO implement
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
                break;
        }
        handler.removeMessages(2);
        handler.sendEmptyMessageDelayed(2,3000);
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
        if(position+1<mediaItems.size()) {
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
                btn_next.setBackgroundResource(R.drawable.btn_next_pressed);
                btn_next.setEnabled(false);
                btn_pre.setBackgroundResource(R.drawable.btn_pre_pressed);
                btn_pre.setEnabled(false);
            } else if (2 == mediaItems.size()) {
                if (position == 0) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_selector);
                    btn_next.setEnabled(true);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_pressed);
                    btn_pre.setEnabled(false);
                } else {
                    btn_next.setBackgroundResource(R.drawable.btn_next_pressed);
                    btn_next.setEnabled(false);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btn_pre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_selector);
                    btn_next.setEnabled(true);
                    btn_pre.setBackgroundResource(R.drawable.btn_pre_pressed);
                    btn_pre.setEnabled(false);
                } else if (position + 1 == mediaItems.size()) {
                    btn_next.setBackgroundResource(R.drawable.btn_next_pressed);
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
            btn_next.setBackgroundResource(R.drawable.btn_next_pressed);
            btn_next.setEnabled(false);
            btn_pre.setBackgroundResource(R.drawable.btn_pre_pressed);
            btn_pre.setEnabled(false);
        } else {

        }
    }


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
        handler.removeMessages(2);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.sendEmptyMessageDelayed(2,3000);
    }
}
