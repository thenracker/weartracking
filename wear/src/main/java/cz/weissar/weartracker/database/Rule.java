package cz.weissar.weartracker.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.time.temporal.ChronoUnit;

import cz.weissar.weartracker.dto.ContextualUserQuestionnaire;

@Table(database = AppDatabase.class)
public class Rule extends BaseModel { // fixme - bude využíváno aj jako DTOčko

    @PrimaryKey
    private Long id;
    @Column
    private int windowSize;
    @Column
    private int lookBack;
    @Column
    private TimeUnit timeUnit;
    @Column
    private int maxOutliers;
    @Column
    private Function function;
    @Column
    private RecordType recordType;
    @Column
    private Operator operator;
    @Column
    private double threshold;
    @Column
    private boolean canBeNull;

    //todo
    private ContextualUserQuestionnaire startsQuestionnaire;
    //todo
    private ContextualUserQuestionnaire endsQuestionnaire;

    public Rule() {
    }

    public Rule(int windowMinutes, TimeUnit timeUnit, int maxOutliers, Function function, Operator operator, double threshold, boolean canBeNull) {
        this.windowSize = windowMinutes;
        this.timeUnit = timeUnit;
        this.maxOutliers = maxOutliers;
        this.function = function;
        this.operator = operator;
        this.threshold = threshold;
        this.canBeNull = canBeNull;
    }

    public Rule(int windowSize, int lookBack, TimeUnit timeUnit, int maxOutliers, Function function, Operator operator, double threshold, boolean canBeNull) {
        this.windowSize = windowSize;
        this.maxOutliers = maxOutliers;
        this.lookBack = lookBack;
        this.timeUnit = timeUnit;
        this.function = function;
        this.operator = operator;
        this.threshold = threshold;
        this.canBeNull = canBeNull;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int window) {
        this.windowSize = window;
    }

    public int getMaxOutliers() {
        return maxOutliers;
    }

    public void setMaxOutliers(int maxOutliers) {
        this.maxOutliers = maxOutliers;
    }

    public int getLookBack() {
        return lookBack;
    }

    public void setLookBack(int lookBack) {
        this.lookBack = lookBack;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isCanBeNull() {
        return canBeNull;
    }

    public void setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public UserQuestionnaire getUserQuestionnaire() {
        if (endsQuestionnaire != null) {
            return endsQuestionnaire;
        }
        return startsQuestionnaire;
    }

    public void setStartsQuestionnaire(ContextualUserQuestionnaire startsQuestionnaire) {
        this.startsQuestionnaire = startsQuestionnaire;
    }

    public void setEndsQuestionnaire(ContextualUserQuestionnaire endsQuestionnaire) {
        this.endsQuestionnaire = endsQuestionnaire;
    }

    public enum Function {
        SUM,
        AVG
//        EVERY_VALUE
    }

    public enum Operator {
        LESS_THAN("<"),
        GREATER_THAN(">"),
        LESS_THAN_OR_EQUAL("<="),
        GREATER_THAN_OR_EQUAL(">="),
        EQUAL("=");

        private String desc;

        Operator(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }

    public enum TimeUnit {

        MILLIS(ChronoUnit.MILLIS),
        SECONDS(ChronoUnit.SECONDS),
        MINUTES(ChronoUnit.MINUTES),
        HOURS(ChronoUnit.HOURS);

        private ChronoUnit chronoUnit;

        TimeUnit(ChronoUnit chronoUnit) {
            this.chronoUnit = chronoUnit;
        }

        public ChronoUnit getUnit() {
            return chronoUnit;
        }
    }

    public enum RecordType {

        HR(1, "Tep"),
        STEPS(2, "Kroky");

        private int id;
        private String name;

        RecordType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
