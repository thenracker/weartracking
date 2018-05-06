package cz.weissar.weartracker;

import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class SaveFileReceiver extends WearableListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_SHORT).show();
    }
}
