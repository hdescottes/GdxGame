package com.gdx.game.factory;

import com.badlogic.gdx.math.Vector3;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.Bird;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityEnums;
import com.gdx.game.entities.Hero;
import com.gdx.game.entities.Rabite;
import com.gdx.game.manager.ResourceManager;

public class EntityFactory {

    private EntityFactory() {
    }

    public static Entity getEntity(EntityEnums.ENTITYCLASS entityclass, Vector3 vec3, Box2dWorld box2d, ResourceManager resourceManager) {
        Entity entity;
        switch(entityclass){
            case HERO:
                entity = new Hero(vec3, box2d, EntityEnums.ENTITYSTATE.WALKING_DOWN, resourceManager);
                return entity;
            case RABITE:
                entity = new Rabite(vec3, box2d, EntityEnums.ENTITYSTATE.IDLE, resourceManager);
                return entity;
            case BIRD:
                entity = new Bird(vec3, box2d, EntityEnums.ENTITYSTATE.FLYING, resourceManager);
                return entity;
            default:
                return null;
        }
    }
}
