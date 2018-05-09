package cz.weissar.weartracker.service;

import android.hardware.Sensor;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SensorHandler {

    public enum Type {

        ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, 3),
        GYROSCOPE(Sensor.TYPE_GYROSCOPE, 3),
        PRESSURE(Sensor.TYPE_PRESSURE, 1),
        HEART_RATE(Sensor.TYPE_HEART_RATE, 1),
        HEART_BEAT(Sensor.TYPE_HEART_BEAT, 1);

        private int sensorType;
        private int columnCount;

        Type(int sensorType, int columnCount) {
            this.sensorType = sensorType;
            this.columnCount = columnCount;
        }

        public int getType() {
            return sensorType;
        }

        public int getColumnCount() {
            return columnCount;
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

    public static SensorHandler newInstance(Type sensorType) {
        return new SensorHandler(sensorType);
    }

    private SensorHandler() { //nepoužívat
    }

    private SensorHandler(Type sensorType) {
        this.sensorType = sensorType;
        this.columnCount = sensorType.columnCount;

        values = new float[columnCount * 2][];
        for (int i = 0; i < (columnCount * 2); i++) {
            values[i] = new float[SIZE];
        }
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
            timestamp1[pointer] = System.currentTimeMillis();
        } else {
            timestamp2[pointer] = System.currentTimeMillis();
        }
        pointer++;

        if (pointer == SIZE) {
            saveValues();
        }
    }

    public void saveValues() {
        //otočit buffery a začít od nuly
        firstFilled = !firstFilled;
        int lastPoint = pointer; //kdybychom volali dříve než konec bufferu, pak zapíšeme pouze dosud zapsané hodnoty
        pointer = 0;

        //nyní otočené hodnoty
        /*final List<Measurement> measurements = new ArrayList<>();
        for (int i = 0; i < lastPoint; i++) {
            Measurement measurement = new Measurement(sensorType, firstFilled ? timestamp1[i] : timestamp2[i]);
            for (int j = 0; i < columnCount; i++) {
                measurement.setVal(firstFilled ? j : (j + columnCount), values[firstFilled ? j : (j + columnCount)][i]);
            }
            measurements.add(measurement);
        }*/

        //SQLITE nestíhala
        /*FlowManager.getDatabase(AppDatabase.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Measurement measurement : measurements) {
                    measurement.save(databaseWrapper);
                }
            }
        }).build().execute();*/

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
            String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/"
                    + sdf.format(Calendar.getInstance().getTime()) + "/"
                    + sensorType.name() + ".txt";
            File file = new File(fileName);

            file.getParentFile().mkdirs(); //složky nad

            boolean append = false;
            final StringBuilder builder;
            if (!file.exists()) {
                builder = new StringBuilder("timestamp," + (sensorType.equals(Type.PRESSURE)? "hpa":(sensorType.equals(Type.HEART_RATE)? "bpm":"x")) + (sensorType.getColumnCount() == 1 ? "\n" : ",y,z\n"));
            } else {
                builder = new StringBuilder();
                append = true;
            }

            for (int i = 0; i < lastPoint; i++){
                if (sensorType.getColumnCount() == 1) {
                    builder.append(String.format("%s,%s\n", firstFilled ? timestamp1[i] : timestamp2[i],
                            values[firstFilled ? 0 : (0 + columnCount)][i]
                    ));
                } else {
                    builder.append(String.format("%s,%s,%s,%s\n", firstFilled ? timestamp1[i] : timestamp2[i],
                            values[firstFilled ? 0 : (0 + columnCount)][i],
                            values[firstFilled ? 1 : (1 + columnCount)][i],
                            values[firstFilled ? 2 : (2 + columnCount)][i]
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
