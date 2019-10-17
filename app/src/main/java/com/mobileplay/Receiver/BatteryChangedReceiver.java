package com.mobileplay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryChangedReceiver extends BroadcastReceiver {
    private IBatteryChanged batteryChanged;

    public BatteryChangedReceiver(IBatteryChanged batteryChanged) {
        this.batteryChanged=batteryChanged;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                batteryChanged.setBattery(batteryLevel);
                break;
        }
    }
}
