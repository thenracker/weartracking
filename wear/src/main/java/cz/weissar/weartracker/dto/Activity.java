package cz.weissar.weartracker.dto;

public class Activity {

    private int contextualQuestionnaireId;
    private boolean isEmergency;

    public Activity(int contextualQuestionnaireId) {
        this.contextualQuestionnaireId = contextualQuestionnaireId;
        this.isEmergency = true;
    }
}
