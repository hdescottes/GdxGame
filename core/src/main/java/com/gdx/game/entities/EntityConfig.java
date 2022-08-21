package com.gdx.game.entities;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gdx.game.inventory.InventoryItem;

public class EntityConfig {
    private Array<AnimationConfig> animationConfig;
    private Array<InventoryItem.ItemTypeID> inventory;
    private Entity.State state = Entity.State.IDLE;
    private Entity.Direction direction = Entity.Direction.DOWN;
    private String entityID;
    private String entityStatus;
    private String conversationConfigPath;
    private String classTreePath;
    private String resumeConfigPath;
    private String questConfigPath;
    private String currentQuestID;
    private String itemTypeID;
    private ObjectMap<String, String> entityProperties;

    public enum EntityProperties {
        ENTITY_HEALTH_POINTS,
        ENTITY_ATTACK_POINTS,
        ENTITY_DEFENSE_POINTS,
        ENTITY_HIT_DAMAGE_TOTAL,
        ENTITY_XP_REWARD,
        ENTITY_GP_REWARD,
        NONE
    }

    EntityConfig() {
        animationConfig = new Array<>();
        inventory = new Array<>();
        entityProperties = new ObjectMap<>();
    }

    public EntityConfig(EntityConfig config) {
        state = config.getState();
        direction = config.getDirection();
        entityID = config.getEntityID();
        entityStatus = config.getEntityStatus();
        conversationConfigPath = config.getConversationConfigPath();
        classTreePath = config.getClassTreePath();
        resumeConfigPath = config.getResumeConfigPath();
        questConfigPath = config.getQuestConfigPath();
        currentQuestID = config.getCurrentQuestID();
        itemTypeID = config.getItemTypeID();

        animationConfig = new Array<>();
        animationConfig.addAll(config.getAnimationConfig());

        inventory = new Array<>();
        inventory.addAll(config.getInventory());

        entityProperties = new ObjectMap<>();
        entityProperties.putAll(config.entityProperties);
    }

    public ObjectMap<String, String> getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(ObjectMap<String, String> entityProperties) {
        this.entityProperties = entityProperties;
    }

    public void setPropertyValue(String key, String value) {
        entityProperties.put(key, value);
    }

    public String getPropertyValue(String key) {
        Object propertyVal = entityProperties.get(key);
        if (propertyVal == null) {
            return "";
        }
        return propertyVal.toString();
    }

    public String getCurrentQuestID() {
        return currentQuestID;
    }

    public void setCurrentQuestID(String currentQuestID) {
        this.currentQuestID = currentQuestID;
    }

    public String getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(String itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public String getQuestConfigPath() {
        return questConfigPath;
    }

    public void setQuestConfigPath(String questConfigPath) {
        this.questConfigPath = questConfigPath;
    }

    public String getConversationConfigPath() {
        return conversationConfigPath;
    }

    public void setConversationConfigPath(String conversationConfigPath) {
        this.conversationConfigPath = conversationConfigPath;
    }

    public String getClassTreePath() {
        return classTreePath;
    }

    public void setClassTreePath(String classTreePath) {
        this.classTreePath = classTreePath;
    }

    public String getResumeConfigPath() {
        return resumeConfigPath;
    }

    public void setResumeConfigPath(String resumeConfigPath) {
        this.resumeConfigPath = resumeConfigPath;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(String entityStatus) {
        this.entityStatus = entityStatus;
    }

    public Entity.Direction getDirection() {
        return direction;
    }

    public void setDirection(Entity.Direction direction) {
        this.direction = direction;
    }

    public Entity.State getState() {
        return state;
    }

    public void setState(Entity.State state) {
        this.state = state;
    }

    public Array<AnimationConfig> getAnimationConfig() {
        return animationConfig;
    }

    public void addAnimationConfig(AnimationConfig animationConfig) {
        this.animationConfig.add(animationConfig);
    }

    public Array<InventoryItem.ItemTypeID> getInventory() {
        return inventory;
    }

    public void setInventory(Array<InventoryItem.ItemTypeID> inventory) {
        this.inventory = inventory;
    }

    public static class AnimationConfig {
        private float frameDuration = 1.0f;
        private Entity.AnimationType animationType;
        private Array<String> texturePaths;
        private Array<GridPoint2> gridPoints;

        public AnimationConfig() {
            animationType = Entity.AnimationType.IDLE;
            texturePaths = new Array<>();
            gridPoints = new Array<>();
        }

        public float getFrameDuration() {
            return frameDuration;
        }

        public void setFrameDuration(float frameDuration) {
            this.frameDuration = frameDuration;
        }

        public Array<String> getTexturePaths() {
            return texturePaths;
        }

        public void setTexturePaths(Array<String> texturePaths) {
            this.texturePaths = texturePaths;
        }

        public Array<GridPoint2> getGridPoints() {
            return gridPoints;
        }

        public void setGridPoints(Array<GridPoint2> gridPoints) {
            this.gridPoints = gridPoints;
        }

        public Entity.AnimationType getAnimationType() {
            return animationType;
        }

        public void setAnimationType(Entity.AnimationType animationType) {
            this.animationType = animationType;
        }
    }

}
