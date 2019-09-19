package com.mobileplay.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobileplay.base.BasePager;

public class NetVideoPager extends BasePager {

    public NetVideoPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        TextView textView = new TextView(context);
        textView.setText("NetVideoPager");
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i("initData","NetVideoPager");
    }

}