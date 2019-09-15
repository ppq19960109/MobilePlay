package com.mobileplay.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.mobileplay.R;

public class MainActivity extends Activity {
    private FrameLayout fl_main;
    private RadioGroup rg_tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        rg_tag = (RadioGroup) findViewById(R.id.rg_tag);
    }
}
