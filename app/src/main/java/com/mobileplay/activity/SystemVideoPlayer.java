package com.mobileplay.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.mobileplay.R;
import com.mobileplay.Receiver.BatteryChangedReceiver;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SystemVideoPlayer extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private VideoView video_view;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tv_battery;
    private TextView tvSystemTime;
    private SeekBar sbVoice;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar sbVideo;
    private TextView tvDuration;
    private BatteryChangedReceiver batteryChangedReceiver;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int currentPosition = video_view.getCurrentPosition();
                    sbVideo.setProgress(currentPosition);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                    tvCurrentTime.setText(formatter.format(currentPosition));
                    tvSystemTime.setText(getSystemtime());

                    removeMessages(1);
                    sendEmptyMessageDelayed(1,1000);
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
        batteryChangedReceiver=new BatteryChangedReceiver(this);
        registerReceiver(batteryChangedReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(1);
        if(batteryChangedReceiver!=null) {
            unregisterReceiver(batteryChangedReceiver);
            batteryChangedReceiver=null;
        }
        super.onDestroy();
    }

    public void setBattery(int level) {
        tv_battery.setText(Integer.toString(level)+"%");
        if(level<=10){
           ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level<=20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level<=40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level<=60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level<=80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level>=100){
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
        findViewById(R.id.btn_pre).setOnClickListener(this);
        findViewById(R.id.btn_play_start_pause).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_full_screen).setOnClickListener(this);
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

            }
        });
        video_view.setVideoURI(getIntent().getData());
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
                break;
            case R.id.btn_pre:
                //TODO implement
                break;
            case R.id.btn_play_start_pause:
                //TODO implement
                if(video_view.isPlaying()){
                    video_view.pause();
                    findViewById(R.id.btn_play_start_pause).setBackgroundResource(R.drawable.btn_play_start_selector);
                }else {
                    video_view.start();
                    findViewById(R.id.btn_play_start_pause).setBackgroundResource(R.drawable.btn_play_pause_selector);
                }
                break;
            case R.id.btn_next:
                //TODO implement
                break;
            case R.id.btn_full_screen:
                //TODO implement
                break;
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

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
