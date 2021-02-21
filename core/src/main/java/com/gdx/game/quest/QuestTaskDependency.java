package com.gdx.game.quest;

public class QuestTaskDependency {
    private String sourceId;
    private String destinationId;

    public QuestTaskDependency() {

    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }
}
