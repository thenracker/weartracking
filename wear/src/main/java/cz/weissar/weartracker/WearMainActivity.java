package cz.weissar.weartracker;

import android.Manifest;
import android.app.ActivityManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cz.weissar.weartracker.service.Measurement;
import cz.weissar.weartracker.service.Measurement_Table;
import cz.weissar.weartracker.service.SensorHandler;
import cz.weissar.weartracker.service.TrackingService;

public class WearMainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener {

    private TextView mTextView;

    ImageView cancelButton;
    ImageView okButton;
    ImageView saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);

        mTextView = findViewById(R.id.text);
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText(String.valueOf(SQLite.selectCountOf().from(Measurement.class).count()));
            }
        });

        /*saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(WearMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);
                } else {
                    for (SensorHandler.Type sensor : TrackingService.sensors) {
                        saveVals(sensor);
                    }
                    Toast.makeText(WearMainActivity.this, "Probíhá ukládání", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        // Enables Always-on
        //setAmbientEnabled();


        Wearable.getMessageClient(this).addListener(this);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(WearMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(WearMainActivity.this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
                    mTextView.setText("Potvrďte oprávnění aplikace");
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BODY_SENSORS}, 666);
                } else {
                    handleSensorService();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(WearMainActivity.this, TrackingService.class));
                mTextView.setText("Měření ukončeno");
            }
        });

        if (isMyServiceRunning(TrackingService.class)){
            mTextView.setText("Měření již probíhá");
        } else {
            mTextView.setText("Spusťte měření");
        }
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
        //SQLite.delete().from(Measurement.class).execute(); //delete starých hodnot fixme smazat
        if (!isMyServiceRunning(TrackingService.class)) {
            Intent serviceIntent = new Intent(this, TrackingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            mTextView.setText("Bylo spuštěno měření");
        } else {
            mTextView.setText("Měření stále probíhá :)");
        }

    }

    private void checkLastUpdated() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
                String fileName = Environment.getExternalStorageDirectory().toString() + "/" + sdf.format(Calendar.getInstance().getTime()) + "_" + "values.txt";
                File file = new File(fileName);

                if (file.exists()) {
                    String format = new SimpleDateFormat("d.M. HH:mm:ss").format(file.lastModified());
                    mTextView.setText("Poslední update " + format);
                } else {
                    mTextView.setText("Soubor zatim neexistuje");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveVals(final SensorHandler.Type type) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            SQLite.select().from(Measurement.class).where(Measurement_Table.sensorType.eq(type)).async().queryListResultCallback(new QueryTransaction.QueryResultListCallback<Measurement>() {
                @Override
                public void onListQueryResult(QueryTransaction transaction, @NonNull List<Measurement> tResult) {
                    try {
                        if (!tResult.isEmpty()) {

                            SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
                            String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/" + sdf.format(Calendar.getInstance().getTime()) + "/" + type.name() + ".txt";
                            File file = new File(fileName);

                            file.getParentFile().mkdirs(); //složky nad

                            boolean append = false;
                            final StringBuilder builder;
                            if (!file.exists()) {
                                builder = new StringBuilder("timestamp,x" + (type.getColumnCount() == 1 ? "\n" : ",y,z\n"));
                            } else {
                                builder = new StringBuilder();
                                append = true;
                            }

                            for (Measurement measurement : tResult) {
                                if (type.getColumnCount() == 1) {
                                    builder.append(String.format("%s,%s\n", measurement.getTime(), measurement.getVal1()));
                                } else {
                                    builder.append(String.format("%s,%s,%s,%s\n", measurement.getTime(), measurement.getVal1(), measurement.getVal2(), measurement.getVal3()));
                                }
                            }
                            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, append));
                            out.write(builder.toString());
                            out.flush();
                            out.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SQLite.delete().from(Measurement.class).where(Measurement_Table.sensorType.eq(type)).execute();
                    }
                }
            }).execute();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())/* && service.*/) {
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
