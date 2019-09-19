package com.mobileplay.pager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileplay.base.BasePager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoPager extends BasePager {

    public VideoPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        TextView textView = new TextView(context);
        textView.setText("VideoPager");
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i("initData","VideoPager");
    }
}
