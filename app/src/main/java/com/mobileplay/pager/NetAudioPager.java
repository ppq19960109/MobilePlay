package com.mobileplay.pager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetAudioPager extends BasePager {
    private final int GET_MEDIA = 1;

    private Handler handler = new MyHandler(this);

    @Override
    public void mHandleMessage(Message msg) {
        switch (msg.what) {
            case GET_MEDIA:

                break;
        }
    }

    public NetAudioPager() {

    }
    public NetAudioPager(Context context) {
        super(context);
        initData();
    }


    @Override
    public void initData() {
        super.initData();
        Log.i("initData","NetAudioPager");
    }
    @Override
    public void close() {
        handler.removeCallbacksAndMessages(null);
    }

}
