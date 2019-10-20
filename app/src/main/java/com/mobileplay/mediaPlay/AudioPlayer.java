package com.mobileplay.mediaPlay;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mobileplay.R;

public class AudioPlayer extends Activity {

    private ImageView iv_icon;
    private TextView tv_artist;
    private TextView tv_audio_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);
        initView();
        initData();
    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_audio_name = (TextView) findViewById(R.id.tv_audio_name);
    }

    private void initData() {
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getDrawable();
        animationDrawable.start();
    }

}
