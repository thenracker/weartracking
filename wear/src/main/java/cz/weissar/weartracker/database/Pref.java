package cz.weissar.weartracker.database;

import android.content.Context;
import android.preference.PreferenceManager;

public class Pref {

    public static String getFolderName(Context context) {
        long lastUpdate = PreferenceManager.getDefaultSharedPreferences(context).getLong("LAST_UPDATE", 0);

        int folderPointer = PreferenceManager.getDefaultSharedPreferences(context).getInt("FOLDER", 1);

        if ((System.currentTimeMillis() - lastUpdate) > (60 * 60 * 1000)) {
            folderPointer++;
        }

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong("LAST_UPDATE", System.currentTimeMillis())
                .putInt("FOLDER", folderPointer).commit();

        return "MEASURE_" + folderPointer;
    }
}
