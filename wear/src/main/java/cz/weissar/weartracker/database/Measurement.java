package cz.weissar.weartracker.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cz.weissar.weartracker.database.AppDatabase;
import cz.weissar.weartracker.service.SensorHandler;

@Deprecated //Je to blbý nápad zapisovat do sqlite.. nehodí se pro stovky tisíc záznamů
//@Table(database = AppDatabase.class)
public class Measurement extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private SensorHandler.Type sensorType;

    @Column
    private float val1;

    @Column
    private float val2;

    @Column
    private float val3;

    @Column
    private float val4;

    @Column
    private long time;

    public Measurement() {
    }

    public Measurement(SensorHandler.Type sensorType, long time) {
        this.sensorType = sensorType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SensorHandler.Type getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorHandler.Type sensorType) {
        this.sensorType = sensorType;
    }

    public float getVal1() {
        return val1;
    }

    public void setVal1(float val1) {
        this.val1 = val1;
    }

    public float getVal2() {
        return val2;
    }

    public void setVal2(float val2) {
        this.val2 = val2;
    }

    public float getVal3() {
        return val3;
    }

    public void setVal3(float val3) {
        this.val3 = val3;
    }

    public float getVal4() {
        return val4;
    }

    public void setVal4(float val4) {
        this.val4 = val4;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     *
     * @param i 0 represents first value !
     * @param v value itself
     */
    public void setVal(int i, float v) {
        switch (i) {
            case 0:
                setVal1(v);
                break;
            case 1:
                setVal2(v);
                break;
            case 2:
                setVal3(v);
                break;
            case 3:
                setVal4(v);
                break;

        }
    }
}
