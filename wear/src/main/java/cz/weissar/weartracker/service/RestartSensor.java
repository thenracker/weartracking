package cz.weissar.weartracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartSensor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, TrackingService.class));
    }
}
