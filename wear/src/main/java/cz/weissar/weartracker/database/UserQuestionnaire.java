package cz.weissar.weartracker.database;

import java.io.Serializable;

/**
 * Created by vlado on 26/07/2017.
 */
public class UserQuestionnaire implements Serializable {

    /*
    Long id;

    List<User> users = new ArrayList<>();

    List<Group> groups = new ArrayList<>();

    LocalDateTime dateValidFromTimestamp;

    LocalDateTime dateValidToTimestamp;

    Long expiresInMs;

    int repeatDayBits;

    TimeOfDay timeOfDay;

    List<Questionnaire> questionnaires = new ArrayList<>();

    LocalDateTime deletedAt;

    boolean forEveryone;

    Long copiedFromId;

    public UserQuestionnaire() {
    }

    public UserQuestionnaire(Questionnaire questionnaire, User user) {
        this.dateValidFromTimestamp = LocalDate.now().atStartOfDay();
        this.dateValidToTimestamp = LocalDateTime.now().plusHours(3);
        this.timeOfDay = TimeOfDay.IMMEDIATE;
        this.questionnaires.add(questionnaire);
        this.users = Collections.singletonList(user);
    }

    public UserQuestionnaire(List<Questionnaire> questionnaires) {
        this.dateValidFromTimestamp = LocalDate.now().atStartOfDay();
        this.dateValidToTimestamp = LocalDateTime.now().plusHours(3);
        this.timeOfDay = TimeOfDay.IMMEDIATE;
        this.questionnaires = questionnaires;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public LocalDateTime getDateValidFromTimestamp() {
        return dateValidFromTimestamp;
    }

    public void setDateValidFromTimestamp(LocalDateTime dateValidFromTimestamp) {
        this.dateValidFromTimestamp = dateValidFromTimestamp;
    }

    public LocalDateTime getDateValidToTimestamp() {
        return dateValidToTimestamp;
    }

    public void setDateValidToTimestamp(LocalDateTime dateValidToTimestamp) {
        this.dateValidToTimestamp = dateValidToTimestamp;
    }

    public Long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(Long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public int getRepeatDayBits() {
        return repeatDayBits;
    }

    public void setRepeatDayBits(int repeatDayBits) {
        this.repeatDayBits = repeatDayBits;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isForEveryone() {
        return forEveryone;
    }

    public void setForEveryone(boolean forEveryone) {
        this.forEveryone = forEveryone;
    }

    public Long getCopiedFromId() {
        return copiedFromId;
    }

    public void setCopiedFromId(Long copiedFromId) {
        this.copiedFromId = copiedFromId;
    }

    @Override
    public String toString() {
        String questionnaireNames = getFormattedQuestionnaireNames();
        if (questionnaireNames.isEmpty()) {
            return String.valueOf(id);
        } else {
            return id + " - " + questionnaireNames;
        }
    }

    public String getFormattedQuestionnaireNames() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Questionnaire questionnaire : questionnaires) {
            stringBuilder.append(questionnaire.getName());
            stringBuilder.append(", ");
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }

        return stringBuilder.toString();
    }

    public Set<User> getUniqueUsers() {
        Set<User> uniqueUsers = new HashSet<>(users);
        for (Group group : getGroups()) {
            uniqueUsers.addAll(group.getUsers());
        }

        return uniqueUsers;
    }

    public <T extends UserQuestionnaire> T copy(T userQuestionnaire) {
        userQuestionnaire.copiedFromId = userQuestionnaire.getId();

        userQuestionnaire.users = new ArrayList<>(this.users);
        userQuestionnaire.groups = new ArrayList<>(this.groups);
        userQuestionnaire.dateValidFromTimestamp = this.dateValidFromTimestamp;
        userQuestionnaire.dateValidToTimestamp = this.dateValidToTimestamp;
        userQuestionnaire.expiresInMs = this.expiresInMs;
        userQuestionnaire.repeatDayBits = this.repeatDayBits;
        userQuestionnaire.timeOfDay = this.timeOfDay;
        userQuestionnaire.questionnaires = new ArrayList<>(this.questionnaires);
        userQuestionnaire.deletedAt = this.deletedAt;
        userQuestionnaire.forEveryone = this.forEveryone;

        return userQuestionnaire;
    }
    */
}