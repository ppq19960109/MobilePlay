package com.mobileplay.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mobileplay.R;
import com.mobileplay.base.Basepager;
import com.mobileplay.pager.AudioPager;
import com.mobileplay.pager.NetAudioPager;
import com.mobileplay.pager.NetVideoPager;
import com.mobileplay.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main;
    private RadioGroup rg_tag;
    private ArrayList<Basepager> basepagers;
    private int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        basepagers.add(new VideoPager(this));
        basepagers.add(new AudioPager(this));
        basepagers.add(new NetVideoPager(this));
        basepagers.add(new NetAudioPager(this));

        rg_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_audio:
                        pos = 1;
                        break;
                    case R.id.rb_net_video:
                        pos = 2;
                        break;
                    case R.id.rb_net_audio:
                        pos = 3;
                        break;
                    default:
                        pos = 0;
                        break;
                }
                setfragment();
            }
        });
    }

    private void setfragment() {

    }

    private void initView() {
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        rg_tag = (RadioGroup) findViewById(R.id.rg_tag);
    }
}
