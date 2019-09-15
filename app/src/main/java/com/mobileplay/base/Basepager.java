package com.mobileplay.base;

import android.content.Context;
import android.view.View;

public abstract class Basepager {

    public View rootView;
    public Context context;

    public Basepager(Context context) {
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    public void initData() {

    }
}
