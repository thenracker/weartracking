package cz.weissar.weartracker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.weissar.weartracker.service.TrackingService;

public class WearMainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener {

    private TextView mTextView;

    TrackingService trackingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);

        mTextView = (TextView) findViewById(R.id.text);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(WearMainActivity.this, MainActivity.class);
                //Wearable.getMessageClient(WearMainActivity.this).sendMessage("TEST", "TEST", null);

                checkLastUpdated();
            }
        });

        // Enables Always-on
        //setAmbientEnabled();

        handleSensorService();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);
        }

        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void handleSensorService() {
        if (!isMyServiceRunning(TrackingService.class)) {
            Intent serviceIntent = new Intent(this, TrackingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            mTextView.setText("Bylo spuštěno měření ;)");
        } else {
            mTextView.setText("Měření stále probíhá :)");
        }

    }

    private void checkLastUpdated() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
                String fileName = Environment.getExternalStorageDirectory().toString() + "/" + sdf.format(Calendar.getInstance().getTime()) + "_" + "accelerometer.txt";
                File file = new File(fileName);

                if (file.exists()) {
                    String format = new SimpleDateFormat("d.M. HH:mm").format(file.lastModified());
                    mTextView.setText("Poslední update " + format);
                } else {
                    mTextView.setText("Soubor zatim neexistuje");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        mTextView.setText(messageEvent.getPath());
    }
}
