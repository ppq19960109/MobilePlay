package com.mobileplay.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BasePager extends Fragment {

    public View rootView;
    public Context context;

    public BasePager() {

    }
    public BasePager(Context context) {
        this.context = context;
        rootView = initRootView();
    }

    public abstract View initRootView();

    public void initData() {
    }

}
