package com.mobileplay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.mobileplay.activity.SystemVideoPlayer;

public class BatteryChangedReceiver extends BroadcastReceiver {
    private SystemVideoPlayer videoPlayer;

    public BatteryChangedReceiver(SystemVideoPlayer systemVideoPlayer) {
        videoPlayer=systemVideoPlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                videoPlayer.setBattery(level);
                break;
        }
    }
}
