package cz.weissar.weartracker.dto;

import java.util.ArrayList;
import java.util.List;

import cz.weissar.weartracker.database.Rule;
import cz.weissar.weartracker.database.UserQuestionnaire;

/**
 * Created by vlado on 26/07/2017.
 */
public class ContextualUserQuestionnaire extends UserQuestionnaire {

    private List<Rule> startRules = new ArrayList<>();

    private List<Rule> endRules = new ArrayList<>();

    private int countPerDay;

    /**
     * Minimal interval from other contextual questionnaires in milliseconds.
     */
    private int minIntervalMs;

    private int validityMs;

    private String notificationTitle;

    private String notificationBody;

    public ContextualUserQuestionnaire() {
    }

    /*public ContextualUserQuestionnaire(List<Questionnaire> questionnaires) {
        super(questionnaires);
    }*/

    public List<Rule> getStartRules() {
        return startRules;
    }

    public void setStartRules(List<Rule> startRules) {
        this.startRules = startRules;
    }

    public List<Rule> getEndRules() {
        return endRules;
    }

    public void setEndRules(List<Rule> endRules) {
        this.endRules = endRules;
    }

    public int getCountPerDay() {
        return countPerDay;
    }

    public void setCountPerDay(int countPerDay) {
        this.countPerDay = countPerDay;
    }

    public int getMinIntervalMs() {
        return minIntervalMs;
    }

    public void setMinIntervalMs(int minInterval) {
        this.minIntervalMs = minInterval;
    }

    public int getValidityMs() {
        return validityMs;
    }

    public void setValidityMs(int validityMs) {
        this.validityMs = validityMs;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }
}
