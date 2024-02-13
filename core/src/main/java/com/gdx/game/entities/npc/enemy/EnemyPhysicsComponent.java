package com.gdx.game.entities.npc.enemy;

import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.npc.NPCPhysicsComponent;
import com.gdx.game.map.MapManager;

public class EnemyPhysicsComponent extends NPCPhysicsComponent {

    private static final float followRadius = 75.0f;

    @Override
    public void update(Entity entity, MapManager mapMgr, float delta) {
        updateBoundingBoxPosition(nextEntityPosition);

        if (calculateDistance(mapMgr) <= followRadius) {
            followPlayer(mapMgr, entity, delta);
            return;
        }

        if (state == Entity.State.IMMOBILE) {
            return;
        }

        if (!isCollisionWithMapLayer(entity, mapMgr) && !isCollisionWithMapEntities(entity, mapMgr) && state == Entity.State.WALKING) {
            setNextPositionToCurrent(entity);
        } else {
            updateBoundingBoxPosition(currentEntityPosition);
        }
        calculateNextPosition(delta);
    }

    private void followPlayer(MapManager mapMgr, Entity entity, float delta) {
        float speed = Float.parseFloat(entity.getEntityConfig().getEntityProperties().get(EntityConfig.EntityProperties.ENTITY_SPEED_POINTS.name()));

        float dx = mapMgr.getPlayer().getCurrentPosition().x - currentEntityPosition.x;
        float dy = mapMgr.getPlayer().getCurrentPosition().y - currentEntityPosition.y;

        // Check which axis has the greater distance
        if (Math.abs(dx) > Math.abs(dy)) {
            nextEntityPosition.x += Math.signum(dx) * speed/3 * delta;
        } else {
            nextEntityPosition.y += Math.signum(dy) * speed/3 * delta;
        }
        setNextPositionToCurrent(entity);
        updateBoundingBoxPosition(currentEntityPosition);
    }
}
