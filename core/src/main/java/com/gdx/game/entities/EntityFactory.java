package com.gdx.game.entities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.component.Component;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.entities.npc.NPCInputComponent;
import com.gdx.game.entities.npc.NPCPhysicsComponent;
import com.gdx.game.entities.player.PlayerGraphicsComponent;
import com.gdx.game.entities.player.PlayerInputComponent;
import com.gdx.game.entities.player.PlayerPhysicsComponent;

import java.util.Hashtable;

public class EntityFactory {

    private static Json json = new Json();
    private static EntityFactory instance = null;
    private Hashtable<String, EntityConfig> entities;

    public enum EntityType {
        WARRIOR,
        MAGE,
        THIEF,
        GRAPPLER,
        CLERIC,
        PLAYER_DEMO,
        ENEMY,
        NPC
    }

    public enum EntityName {
        TOWN_GUARD_WALKING,
        TOWN_BLACKSMITH,
        TOWN_MAGE,
        TOWN_INNKEEPER,
        TOWN_FOLK1, TOWN_FOLK2, TOWN_FOLK3, TOWN_FOLK4, TOWN_FOLK5,
        TOWN_FOLK6, TOWN_FOLK7, TOWN_FOLK8, TOWN_FOLK9, TOWN_FOLK10,
        TOWN_FOLK11, TOWN_FOLK12, TOWN_FOLK13, TOWN_FOLK14, TOWN_FOLK15,
        RABITE, RABITE2,
        QUEST003_TASK002,
        FIRE
    }

    public static final String PLAYER_WARRIOR_CONFIG = "scripts/player_warrior.json";
    public static final String PLAYER_MAGE_CONFIG = "scripts/player_mage.json";
    public static final String PLAYER_THIEF_CONFIG = "scripts/player_thief.json";
    public static final String PLAYER_GRAPPLER_CONFIG = "scripts/player_grappler.json";
    public static final String PLAYER_CLERIC_CONFIG = "scripts/player_cleric.json";

    public static final String TOWN_GUARD_WALKING_CONFIG = "scripts/town_guard_walking.json";
    public static final String TOWN_BLACKSMITH_CONFIG = "scripts/town_blacksmith.json";
    public static final String TOWN_MAGE_CONFIG = "scripts/town_mage.json";
    public static final String TOWN_INNKEEPER_CONFIG = "scripts/town_innkeeper.json";
    public static final String TOWN_FOLK_CONFIGS = "scripts/town_folk.json";
    public static final String ENEMY_CONFIG = "scripts/enemies.json";
    public static final String ENVIRONMENTAL_ENTITY_CONFIGS = "scripts/environmental_entities.json";

    private EntityFactory() {
        entities = new Hashtable<>();

        Array<EntityConfig> townFolkConfigs = Entity.getEntityConfigs(TOWN_FOLK_CONFIGS);
        for(EntityConfig config: townFolkConfigs) {
            entities.put(config.getEntityID(), config);
        }

        Array<EntityConfig> enemyConfigs = Entity.getEntityConfigs(ENEMY_CONFIG);
        for(EntityConfig config: enemyConfigs) {
            entities.put(config.getEntityID(), config);
        }

        Array<EntityConfig> environmentalEntityConfigs = Entity.getEntityConfigs(ENVIRONMENTAL_ENTITY_CONFIGS);
        for(EntityConfig config: environmentalEntityConfigs) {
            entities.put(config.getEntityID(), config);
        }

        entities.put(EntityName.TOWN_GUARD_WALKING.toString(), Entity.loadEntityConfigByPath(TOWN_GUARD_WALKING_CONFIG));
        entities.put(EntityName.TOWN_BLACKSMITH.toString(), Entity.loadEntityConfigByPath(TOWN_BLACKSMITH_CONFIG));
        entities.put(EntityName.TOWN_MAGE.toString(), Entity.loadEntityConfigByPath(TOWN_MAGE_CONFIG));
        entities.put(EntityName.TOWN_INNKEEPER.toString(), Entity.loadEntityConfigByPath(TOWN_INNKEEPER_CONFIG));
    }

    public static EntityFactory getInstance() {
        if(instance == null) {
            instance = new EntityFactory();
        }

        return instance;
    }

    public Entity getEntity(EntityType entityType) {
        Entity entity;
        switch(entityType) {
            case WARRIOR:
                entity = new Entity(new PlayerInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                entity.setEntityConfig(Entity.getEntityConfig(EntityFactory.PLAYER_WARRIOR_CONFIG));
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
                return entity;
            case MAGE:
                entity = new Entity(new PlayerInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                entity.setEntityConfig(Entity.getEntityConfig(EntityFactory.PLAYER_MAGE_CONFIG));
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
                return entity;
            case THIEF:
                entity = new Entity(new PlayerInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                entity.setEntityConfig(Entity.getEntityConfig(EntityFactory.PLAYER_THIEF_CONFIG));
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
                return entity;
            case GRAPPLER:
                entity = new Entity(new PlayerInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                entity.setEntityConfig(Entity.getEntityConfig(EntityFactory.PLAYER_GRAPPLER_CONFIG));
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
                return entity;
            case CLERIC:
                entity = new Entity(new PlayerInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                entity.setEntityConfig(Entity.getEntityConfig(EntityFactory.PLAYER_CLERIC_CONFIG));
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
                return entity;
            case PLAYER_DEMO:
                entity = new Entity(new NPCInputComponent(), new PlayerPhysicsComponent(), new PlayerGraphicsComponent());
                return entity;
            case NPC:
            case ENEMY:
                entity = new Entity(new NPCInputComponent(), new NPCPhysicsComponent(), new NPCGraphicsComponent());
                return entity;
            default:
                return null;
        }
    }

    public Entity getEntityByName(EntityName entityName) {
        EntityConfig config = new EntityConfig(entities.get(entityName.toString()));
        return Entity.initEntity(config);
    }

}
