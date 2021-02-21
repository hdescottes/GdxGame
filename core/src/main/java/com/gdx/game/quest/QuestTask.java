package com.gdx.game.quest;

import com.badlogic.gdx.utils.ObjectMap;

public class QuestTask {

    public enum QuestType{
        FETCH,
        KILL,
        DELIVERY,
        GUARD,
        ESCORT,
        RETURN,
        DISCOVER
    }

    public enum QuestTaskPropertyType {
        IS_TASK_COMPLETE,
        TARGET_TYPE,
        TARGET_NUM,
        TARGET_LOCATION,
        NONE
    }

    private ObjectMap<String, Object> taskProperties;
    private String id;
    private String taskPhrase;
    private QuestType questType;

    public QuestTask() {
        taskProperties = new ObjectMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskPhrase() {
        return taskPhrase;
    }

    public void setTaskPhrase(String taskPhrase) {
        this.taskPhrase = taskPhrase;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }

    public ObjectMap<String, Object> getTaskProperties() {
        return taskProperties;
    }

    public void setTaskProperties(ObjectMap<String, Object> taskProperties) {
        this.taskProperties = taskProperties;
    }

    public boolean isTaskComplete() {
        if(!taskProperties.containsKey(QuestTaskPropertyType.IS_TASK_COMPLETE.toString())) {
            setPropertyValue(QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "false");
            return false;
        }
        String val = taskProperties.get(QuestTaskPropertyType.IS_TASK_COMPLETE.toString()).toString();
        return Boolean.parseBoolean(val);
    }

    public void setTaskComplete() {
        setPropertyValue(QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
    }

    public void resetAllProperties() {
        taskProperties.put(QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "false");
    }

    public void setPropertyValue(String key, String value) {
        taskProperties.put(key, value);
    }

    public String getPropertyValue(String key) {
        Object propertyVal = taskProperties.get(key);
        if(propertyVal == null) {
            return "";
        }
        return propertyVal.toString();
    }

    public String toString() {
        return taskPhrase;
    }
}
