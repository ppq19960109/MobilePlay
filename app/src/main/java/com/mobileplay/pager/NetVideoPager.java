package com.mobileplay.pager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobileplay.base.BasePager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NetVideoPager extends BasePager {
    public static  final  String NET_VIDEO_URL = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
    public NetVideoPager() {

    }
    public NetVideoPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
