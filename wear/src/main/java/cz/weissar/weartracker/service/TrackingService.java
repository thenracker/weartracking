package cz.weissar.weartracker.service;

import android.Manifest;
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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

    public TrackingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        register();
    }

    @Override
    public void onDestroy() {
        unregister();

        super.onDestroy();

        Intent broadcastIntent = new Intent(RestartSensor.class.getName());
        sendBroadcast(broadcastIntent);
    }

    public void register() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        Toast.makeText(this, "Měření spuštěno", Toast.LENGTH_SHORT).show();
    }

    private void unregister() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        saveAccelerometer();

        Toast.makeText(this, "Měření zastaveno", Toast.LENGTH_SHORT).show();
        showEndNotification();
    }

    private void showEndNotification() {

        int notificationId = 001;
        // The channel ID of the notification.
        String id = "my_channel_01";
        // Build intent for notification content
        Intent viewIntent = new Intent(this, WearMainActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_cc_clear)
                .setContentTitle("Měření zastaveno")
                .setContentText(String.format("V čase %s", System.currentTimeMillis()))
                .setContentIntent(viewPendingIntent)
                .setAutoCancel(true);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Issue the notification with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //DONE
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
}
