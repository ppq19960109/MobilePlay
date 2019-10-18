package com.mobileplay.pager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import androidx.fragment.app.Fragment;

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
    public void mHandleMessage(Message msg) {

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


}
