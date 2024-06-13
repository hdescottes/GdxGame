package com.gdx.game.entities;

public class EntityBonus {

    private String entityProperty;
    private String value;

    public EntityBonus() {
    }

    public EntityBonus(String entityProperty, String value) {
        this.entityProperty = entityProperty;
        this.value = value;
    }

    public String getEntityProperty() {
        return entityProperty;
    }

    public void setEntityProperty(String entityProperty) {
        this.entityProperty = entityProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
