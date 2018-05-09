package cz.weissar.weartracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.weissar.weartracker.R;
import cz.weissar.weartracker.WearMainActivity;

public class TrackingService extends Service implements SensorEventListener {

    private static final int NOTIFICATION = 61;

    public static final SensorHandler.Type[] sensors = new SensorHandler.Type[]{
            SensorHandler.Type.ACCELEROMETER, SensorHandler.Type.GYROSCOPE, SensorHandler.Type.PRESSURE, SensorHandler.Type.HEART_RATE};
    private List<SensorHandler> handlers;

    PowerManager.WakeLock wl;

    boolean isRegistered;

    public TrackingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WAKELOCK_TAG");
            wl.acquire();

            return START_REDELIVER_INTENT;
        } catch (Exception e) {
            return START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION, showNotification(null));
        register();
    }

    @Override
    public void onDestroy() {
        unregister();
        wl.release();

    }

    public void register() {
        if (!isRegistered) {
            handlers = new ArrayList<>();
            SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            for (SensorHandler.Type sensor : sensors) {
                handlers.add(SensorHandler.newInstance(sensor, getBaseContext()));
                manager.registerListener(this, manager.getDefaultSensor(sensor.getType()),
                        ((sensor.equals(SensorHandler.Type.HEART_RATE) || sensor.equals(SensorHandler.Type.PRESSURE))? SensorManager.SENSOR_DELAY_NORMAL : SensorManager.SENSOR_DELAY_FASTEST));
            }
            Toast.makeText(this, "Měření spuštěno", Toast.LENGTH_SHORT).show();
        }
        isRegistered = true;
    }

    private void unregister() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (SensorHandler.Type sensor : sensors) {
            manager.unregisterListener(this, manager.getDefaultSensor(sensor.getType()));
        }

        //douložení zbytku
        for (SensorHandler handler : handlers) {
            handler.saveValues();
        }

        isRegistered = false;

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.cancel(NOTIFICATION);
        Toast.makeText(this, "Měření zastaveno", Toast.LENGTH_SHORT).show();
    }

    //private int[] sensors = new int[]{Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_PRESSURE, Sensor.TYPE_HEART_BEAT};

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                handlers.get(0).handleNewValues(sensorEvent.values);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                handlers.get(1).handleNewValues(sensorEvent.values);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
                handlers.get(2).handleNewValues(sensorEvent.values);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                handlers.get(3).handleNewValues(sensorEvent.values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification showNotification(@Nullable String s) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Měření je aktivní";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WearMainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_cc_checkmark)  // the status icon
                .setTicker(s == null ? text : s)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("WearTracker")  // the label of the entry
                .setContentText(s == null ? text : s)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        return notification;
        // Send the notification.
        //mNM.notify(NOTIFICATION, notification);
    }
}
