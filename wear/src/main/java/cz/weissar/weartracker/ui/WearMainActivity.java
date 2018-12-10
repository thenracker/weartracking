package cz.weissar.weartracker.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.weissar.weartracker.R;
import cz.weissar.weartracker.database.Rule;
import cz.weissar.weartracker.dto.ContextualUserQuestionnaire;
import cz.weissar.weartracker.rest.RestClient;
import cz.weissar.weartracker.service.DeviceBroadcaster;
import cz.weissar.weartracker.service.SendFilesService;
import cz.weissar.weartracker.service.TrackingService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.weissar.weartracker.rest.RestClient.TEST_TOKEN;

public class WearMainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener {

    TextView mTextView;
    ImageView cancelButton;
    ImageView okButton;
    FrameLayout progressFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);

        mTextView = findViewById(R.id.text);
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressFrameLayout = findViewById(R.id.progressFrameLayout);

        //Wearable.getMessageClient(this).addListener(this);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceBroadcaster.notifyDevices(WearMainActivity.this);
            }
        });

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
                Intent intent = new Intent(WearMainActivity.this, SendFilesService.class); //odesílání
                startService(intent);
            }
        });

        if (isMyServiceRunning(TrackingService.class)) {
            mTextView.setText("Měření již probíhá");
        } else {
            mTextView.setText("Spusťte měření");
        }

        /*
        RestClient.get().getRules(TEST_TOKEN).enqueue(new Callback<List<ContextualUserQuestionnaire>>() {
            @Override
            public void onResponse(Call<List<ContextualUserQuestionnaire>> call, Response<List<ContextualUserQuestionnaire>> response) {
                SQLite.delete().from(Rule.class).execute();
                List<ContextualUserQuestionnaire> body = response.body();
                for (ContextualUserQuestionnaire contextualUserQuestionnaire : body) {
                    for (Rule rule : contextualUserQuestionnaire.getStartRules()) {
                        rule.setQuestionnaireId(contextualUserQuestionnaire.getId());
                        rule.async().save();
                    }
                    // prozatím end rules ignorujeme - TODO !
                    /*for (Rule rule : contextualUserQuestionnaire.getEndRules()) {
                        rule.async().save();
                    }*//*
                }
                progressFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<ContextualUserQuestionnaire>> call, Throwable t) {
                Toast.makeText(WearMainActivity.this, "Chyba načítání rulesů", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
        */

        progressFrameLayout.setVisibility(View.GONE); // fixme - potřebujeme rulese
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
