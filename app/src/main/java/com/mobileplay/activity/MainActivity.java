package com.mobileplay.activity;

import android.app.Activity;
import android.os.Bundle;

import com.mobileplay.R;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
