package com.mobileplay.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mobileplay.R;
import com.mobileplay.base.BasePager;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.pager.AudioPager;
import com.mobileplay.pager.NetAudioPager;
import com.mobileplay.pager.NetVideoPager;
import com.mobileplay.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_tag;
    public ArrayList<BasePager> basePagers = new ArrayList<>();
    public int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUtils.isGrantExternalRW(this,0);
        initView();
        initListener();

        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));

        rg_tag.check(R.id.rb_video);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("TAG","Activity onConfigurationChanged");
    }

    private void setfragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, basePagers.get(pos));
        fragmentTransaction.commit();
    }

    private void initView() {
        CommonUtils.debugContext = this;
        rg_tag = (RadioGroup) findViewById(R.id.rg_tag);
    }
    private void initListener() {
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

}
