package com.mobileplay.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobileplay.base.BasePager;

public class AudioPager extends BasePager {

    public AudioPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        TextView textView = new TextView(context);
        textView.setText("AudioPager");
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i("initData","AudioPager");
    }
}
