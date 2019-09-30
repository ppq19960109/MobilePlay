package com.mobileplay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobileplay.R;
import com.mobileplay.common.CommonUtils;

import androidx.annotation.Nullable;

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tv_search;
    private RelativeLayout rl_game;
    private View iv_record;
    private View iv_game;
    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search=getChildAt(1);
        rl_game=(RelativeLayout)getChildAt(2);
        iv_game= rl_game.getChildAt(0);

        iv_record=getChildAt(3);

        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
//        iv_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                CommonUtils.showToastMsg(null,"搜索");
                break;
            case R.id.rl_game:
                CommonUtils.showToastMsg(null,"游戏");
                break;
            case R.id.iv_game:
                CommonUtils.showToastMsg(null,"游戏图片");
                break;
            case R.id.iv_record:
                CommonUtils.showToastMsg(null,"历史");
                break;
        }
    }
}
