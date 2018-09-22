package cz.weissar.weartracker.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import cz.weissar.weartracker.database.Pref;

import static android.content.Context.BATTERY_SERVICE;

public class SensorHandler {

    private Context context;
    private int batLevel;

    public enum Type {

        ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, 3, SensorManager.SENSOR_DELAY_FASTEST),
        GYROSCOPE(Sensor.TYPE_GYROSCOPE, 3, SensorManager.SENSOR_DELAY_FASTEST),
        PRESSURE(Sensor.TYPE_PRESSURE, 1, SensorManager.SENSOR_DELAY_NORMAL),
        HEART_RATE(Sensor.TYPE_HEART_RATE, 1, SensorManager.SENSOR_DELAY_FASTEST);

        private int sensorType;
        private int columnCount;
        private int delay;

        Type(int sensorType, int columnCount, int delay) {
            this.sensorType = sensorType;
            this.columnCount = columnCount;
            this.delay = delay;
        }

        public int getType() {
            return sensorType;
        }

        public int getColumnCount() {
            return columnCount;
        }

        public int getDelay() {
            return delay;
        }
    }

    private final static int SIZE = 1024;

    private Type sensorType;
    private int columnCount;

    //dva zásobníky na časová razítka
    private long[] timestamp1 = new long[SIZE];
    private long[] timestamp2 = new long[SIZE];

    //je dvojnásobná pro
    private float[][] values;

    private boolean firstFilled = false; //čas plnit druhý

    private int pointer = 0;

    public static SensorHandler newInstance(Type sensorType, Context context) {
        return new SensorHandler(sensorType, context);
    }

    private SensorHandler() { //nepoužívat
    }

    private SensorHandler(Type sensorType, Context context) {
        this.context = context;
        this.sensorType = sensorType;
        this.columnCount = sensorType.columnCount;

        values = new float[columnCount * 2][];
        for (int i = 0; i < (columnCount * 2); i++) {
            values[i] = new float[SIZE];
        }

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * Zápis do zásobníku
     *
     * @param values hodnoty ze sensorEvent
     */
    public void handleNewValues(float[] values) {
        for (int i = 0; i < columnCount; i++) {
            this.values[firstFilled ? (columnCount + i) : (i)][pointer] = values[i];
        }

        if (!firstFilled) {
            timestamp1[pointer] = currentMillis();
        } else {
            timestamp2[pointer] = currentMillis();
        }
        pointer++;

        if (pointer == SIZE) {
            saveValues();

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        // TODO - detekceChovaniZRulesu() ... a v tom případě vyslat request na telefon a tak - na to bude ale nový objekt
        // TODO - manager, který to úplně pozaobstará - řekne přes bluetooth telefonu - ukaž notifikaci!
    }

    private long currentMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }

    public void saveValues() { // na jiném vlákně? fixme?

        //otočit buffery a začít od nuly
        firstFilled = !firstFilled;
        int lastPoint = pointer; //kdybychom volali dříve než konec bufferu, pak zapíšeme pouze dosud zapsané hodnoty
        pointer = 0;

        try {

            String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/"
                    + Pref.getFolderName(context) + "/"
                    + sensorType.name() + ".txt";
            File file = new File(fileName);

            file.getParentFile().mkdirs(); //složky nad

            boolean append = false;
            final StringBuilder builder = new StringBuilder(); //JÍŤA NECHCE HLAVIČKU
            if (file.exists()) {
                append = true;
            }

            for (int i = 0; i < lastPoint; i++) {
                if (sensorType.getColumnCount() == 1) {
                    builder.append(String.format("%s,%s,%s\n", firstFilled ? timestamp1[i] : timestamp2[i],
                            values[firstFilled ? 0 : (0 + columnCount)][i], batLevel
                    ));
                } else {
                    builder.append(String.format("%s,%s,%s,%s,%s\n", firstFilled ? timestamp1[i] : timestamp2[i],
                            values[firstFilled ? 0 : (0 + columnCount)][i],
                            values[firstFilled ? 1 : (1 + columnCount)][i],
                            values[firstFilled ? 2 : (2 + columnCount)][i], batLevel
                    ));
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, append));
            out.write(builder.toString());
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
