package com.mobileplay.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class BasePager extends Fragment {

    public static class MyHandler extends Handler {
        private WeakReference<Object> mWeakReference;

        public MyHandler(Object object) {
            mWeakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BasePager mReference = (BasePager) mWeakReference.get();
            mReference.mHandleMessage(msg);
        }
    }

    public Context context;

    public BasePager() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    public BasePager(Context context) {
        this.context = context;
    }

    public abstract void close();

    public void initData() {
    }

    public void mHandleMessage(Message msg) {

    }
}
