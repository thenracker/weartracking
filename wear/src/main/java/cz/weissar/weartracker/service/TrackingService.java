package cz.weissar.weartracker.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.weissar.weartracker.R;
import cz.weissar.weartracker.WearMainActivity;

public class TrackingService extends Service implements SensorEventListener {

    private static final int SIZE = 1024;

    int accelerometerPointer = 0;
    long[] accelerometerTimestamp = new long[SIZE];
    float[] accelerometerValues = new float[SIZE * 3];
    private int NOTIFICATION = 066;

    private boolean isRegistered;


    PowerManager.WakeLock wl;

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

        /*Intent broadcastIntent = new Intent(RestartSensor.class.getName());
        sendBroadcast(broadcastIntent);*/
    }

    public void register() {
        if (!isRegistered) {
            SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
            Toast.makeText(this, "Měření spuštěno", Toast.LENGTH_SHORT).show();
        }
        isRegistered = true;
    }

    private void unregister() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        saveAccelerometer();

        isRegistered = false;

        Toast.makeText(this, "Měření zastaveno", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if (accelerometerPointer == accelerometerTimestamp.length) {
                    saveAccelerometer();
                    accelerometerPointer = 0;
                }
                accelerometerTimestamp[accelerometerPointer] = sensorEvent.timestamp;
                accelerometerValues[(3 * accelerometerPointer)] = sensorEvent.values[0];
                accelerometerValues[(3 * accelerometerPointer) + 1] = sensorEvent.values[1];
                accelerometerValues[(3 * accelerometerPointer) + 2] = sensorEvent.values[2];

                accelerometerPointer++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAccelerometer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
                String fileName = Environment.getExternalStorageDirectory().toString() + "/" + sdf.format(Calendar.getInstance().getTime()) + "_" + "accelerometer.txt";
                File file = new File(fileName);

                boolean append = false;
                StringBuilder builder;
                if (!file.exists()) {
                    builder = new StringBuilder("timestamp,accX,accY,accZ\n");
                } else {
                    builder = new StringBuilder();
                    append = true;
                }

                for (int i = 0; i < accelerometerPointer; i++) {
                    builder.append(String.format("%s,%s,%s,%s\n",
                            (accelerometerTimestamp[i]),
                            //(String.valueOf(accelerometerValues[(i * 3)])).replace(".", ","),
                            ((accelerometerValues[(i * 3)])),
                            //(String.valueOf(accelerometerValues[(i * 3) + 1])).replace(".", ","),
                            ((accelerometerValues[(i * 3) + 1])),
                            //(String.valueOf(accelerometerValues[(i * 3) + 2])).replace(".", ",")
                            ((accelerometerValues[(i * 3) + 2]))
                    ));
                }

                BufferedWriter out = new BufferedWriter(new FileWriter(fileName, append));
                out.write(builder.toString());
                out.flush();
                out.close();

                String format = new SimpleDateFormat("d.M. HH:mm:ss").format(file.lastModified());

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION, showNotification(format));

            } catch (IOException e) {
                e.printStackTrace();
            }

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
        CharSequence text = "SERVICE STARTED";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WearMainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_cc_clear)  // the status icon
                .setTicker(s == null ? text : s)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("WEAR TRACKING")  // the label of the entry
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
