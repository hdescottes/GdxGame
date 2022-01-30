package com.gdx.game.component;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PhysicsComponent extends ComponentSubject implements Component{

    private static final Logger LOGGER = LoggerFactory.getLogger(PhysicsComponent.class);

    public abstract void update(Entity entity, MapManager mapMgr, float delta);

    protected Vector2 nextEntityPosition;
    protected Vector2 currentEntityPosition;
    protected Entity.Direction currentDirection;
    public EntityFactory.EntityName entityEncounteredType;
    protected Json json;
    protected Vector2 velocity;

    protected Array<Entity> tempEntities;

    public Rectangle boundingBox;
    protected BoundingBoxLocation boundingBoxLocation;
    protected Ray selectionRay;
    protected static final float SELECT_RAY_MAXIMUM_DISTANCE = 32.0f;

    public enum BoundingBoxLocation {
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        CENTER,
    }

    public PhysicsComponent() {
        this.nextEntityPosition = new Vector2(0,0);
        this.currentEntityPosition = new Vector2(0,0);
        this.velocity = new Vector2(2f,2f);
        this.boundingBox = new Rectangle();
        this.json = new Json();
        this.tempEntities = new Array<>();
        boundingBoxLocation = BoundingBoxLocation.BOTTOM_LEFT;
        selectionRay = new Ray(new Vector3(), new Vector3());
    }

    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapMgr) {
        tempEntities.clear();
        tempEntities.addAll(mapMgr.getCurrentMapEntities());
        tempEntities.addAll(mapMgr.getCurrentMapQuestEntities());
        boolean isCollisionWithMapEntities = false;

        for(Entity mapEntity: tempEntities) {
            //Check for testing against self
            if(mapEntity.equals(entity)) {
                continue;
            }

            Rectangle targetRect = mapEntity.getCurrentBoundingBox();
            if(boundingBox.overlaps(targetRect)){
                //Collision
                if("FOE".equals(mapEntity.getEntityConfig().getEntityStatus())) {
                    entity.sendMessage(MESSAGE.COLLISION_WITH_FOE, mapEntity.getEntityConfig().getEntityID());
                } else {
                    entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY, mapEntity.getEntityConfig().getEntityID());
                }
                isCollisionWithMapEntities = true;
                break;
            }
        }
        tempEntities.clear();
        return isCollisionWithMapEntities;
    }

    protected boolean isCollision(Entity entitySource, Entity entityTarget) {
        boolean isCollisionWithMapEntities = false;

        if(entitySource.equals(entityTarget)) {
            return false;
        }

        if(entitySource.getCurrentBoundingBox().overlaps(entityTarget.getCurrentBoundingBox())) {
            //Collision
            entitySource.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
            isCollisionWithMapEntities = true;
        }

        return isCollisionWithMapEntities;
    }

    protected boolean isCollisionWithMapLayer(Entity entity, MapManager mapMgr) {
        MapLayer mapCollisionLayer =  mapMgr.getCollisionLayer();

        if(mapCollisionLayer == null){
            return false;
        }

        for(MapObject object: mapCollisionLayer.getObjects()) {
            if(object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
                if(boundingBox.overlaps(rectangle)) {
                    //Collision
                    entity.sendMessage(MESSAGE.COLLISION_WITH_MAP);
                    return true;
                }
            }
        }

        return false;
    }

    protected void setNextPositionToCurrent(Entity entity) {
        this.currentEntityPosition.x = nextEntityPosition.x;
        this.currentEntityPosition.y = nextEntityPosition.y;

        entity.sendMessage(MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition));
    }

    protected void calculateNextPosition(float deltaTime) {
        if(currentDirection == null) {
            return;
        }

        if(deltaTime > .7) {
            return;
        }

        float testX = currentEntityPosition.x;
        float testY = currentEntityPosition.y;

        velocity.scl(deltaTime);

        switch(currentDirection) {
            case LEFT :
                testX -=  velocity.x;
                break;
            case RIGHT :
                testX += velocity.x;
                break;
            case UP :
                testY += velocity.y;
                break;
            case DOWN :
                testY -= velocity.y;
                break;
            default:
                break;
        }

        nextEntityPosition.x = testX;
        nextEntityPosition.y = testY;

        //velocity
        velocity.scl(1 / deltaTime);
    }

    protected void initBoundingBox(float percentageWidthReduced, float percentageHeightReduced) {
        //Update the current bounding box
        float width;
        float height;

        float origWidth =  Entity.FRAME_WIDTH;
        float origHeight = Entity.FRAME_HEIGHT;

        float widthReductionAmount = 1.0f - percentageWidthReduced; //.8f for 20% (1 - .20)
        float heightReductionAmount = 1.0f - percentageHeightReduced; //.8f for 20% (1 - .20)

        if(widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = Entity.FRAME_WIDTH * widthReductionAmount;
        } else {
            width = Entity.FRAME_WIDTH;
        }

        if(heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = Entity.FRAME_HEIGHT * heightReductionAmount;
        } else {
            height = Entity.FRAME_HEIGHT;
        }

        if(width == 0 || height == 0) {
            LOGGER.debug("Width and Height are 0!! " + width + ":" + height);
        }

        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;

        minX = nextEntityPosition.x / Map.UNIT_SCALE;
        minY = nextEntityPosition.y / Map.UNIT_SCALE;

        boundingBox.setWidth(width);
        boundingBox.setHeight(height);

        switch(boundingBoxLocation) {
            case BOTTOM_LEFT:
                boundingBox.set(minX, minY, width, height);
                break;
            case BOTTOM_CENTER:
                boundingBox.setCenter(minX + origWidth/2, minY + origHeight/4);
                break;
            case CENTER:
                boundingBox.setCenter(minX + origWidth/2, minY + origHeight/2);
                break;
        }
    }

    protected void updateBoundingBoxPosition(Vector2 position) {
        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;

        minX = position.x / Map.UNIT_SCALE;
        minY = position.y / Map.UNIT_SCALE;

        switch(boundingBoxLocation) {
            case BOTTOM_LEFT:
                boundingBox.set(minX, minY, boundingBox.getWidth(), boundingBox.getHeight());
                break;
            case BOTTOM_CENTER:
                boundingBox.setCenter(minX + Entity.FRAME_WIDTH/2, minY + Entity.FRAME_HEIGHT/4);
                break;
            case CENTER:
                boundingBox.setCenter(minX + Entity.FRAME_WIDTH/2, minY + Entity.FRAME_HEIGHT/2);
                break;
        }
    }
}
