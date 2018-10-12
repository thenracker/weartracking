package cz.weissar.weartracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableReceiver extends WearableListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        String channelId = "WearTracker_helper_channel";
        String channelName = "WearTracker Channel";

        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_SHORT).show();

        sendMessage("Ahoj", "Dotazníky čekají");
        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        /*Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
        resultIntent.setAction(DashboardActivity.TERMS_ACTION); //FIXME - po kliknutí na notifikaci se neotevřely termíny
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        */
        /*
        builder.setBadgeIconType(R.drawable.ic_home_black_24dp)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Něco se děje!")
                //.setContentIntent(resultPendingIntent) //TODO přidat přechod rovnou na termíny
                .setContentText("Zkontrolujte si a vyplňte dotazníky!")
                //.setLights(PrefManager.getPropFaculty().getLightColor(), AlarmUtils.onMs ,AlarmUtils.offMs)
                .setAutoCancel(true);

        /* if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setBadgeIconType(R.drawable.notification_alarm_white_24px);
            builder.setSmallIcon(R.drawable.notification_alarm_white_24px);
        } */

        /*
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            //mChannel.setLightColor(PrefManager.getPropFaculty().getLightColor());
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotifyMgr.createNotificationChannel(mChannel);
        }

        //ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, PrefManager.getStyleResouce());

        /*
        int[] attribute = new int[]{R.attr.colorPrimary};
        TypedArray array = contextThemeWrapper.getTheme().obtainStyledAttributes(attribute);
        int color = array.getColor(0, Color.TRANSPARENT);
        array.recycle();
        builder.setColor(color);
        */

        /*
        builder.setOnlyAlertOnce(true);

        Notification note = builder.build();

        mNotifyMgr.notify(1, note); */
    }

    private void sendMessage(String title, String message) {

        Context mContext = getApplicationContext();
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder;
        final String NOTIFICATION_CHANNEL_ID = "10001";

        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            //notificationChannel.setVibrationPattern(new long[]{100, 200, 300});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
}
