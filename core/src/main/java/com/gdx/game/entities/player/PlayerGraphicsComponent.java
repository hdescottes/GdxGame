package com.gdx.game.entities.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.component.GraphicsComponent;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.map.MapManager;

public class PlayerGraphicsComponent extends GraphicsComponent {

    protected Vector2 previousPosition;

    public PlayerGraphicsComponent() {
        previousPosition = new Vector2(0,0);
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(MESSAGE_TOKEN);

        if(string.length == 0) {
            return;
        }

        if(string.length == 1) {
            if(string[0].equalsIgnoreCase(MESSAGE.RESET_POSITION.toString())) {
                currentPosition = null;
            }
        }

        //Specifically for messages with 1 object payload
        if(string.length == 2) {
            if(string[0].equalsIgnoreCase(MESSAGE.CURRENT_POSITION.toString())) {
                currentPosition = json.fromJson(Vector2.class, string[1]);
            } else if(string[0].equalsIgnoreCase(MESSAGE.INIT_START_POSITION.toString())) {
                currentPosition = json.fromJson(Vector2.class, string[1]);
            } else if(string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
                currentState = json.fromJson(Entity.State.class, string[1]);
            } else if(string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
                currentDirection = json.fromJson(Entity.Direction.class, string[1]);
            } else if(string[0].equalsIgnoreCase(MESSAGE.LOAD_ANIMATIONS.toString())) {
                EntityConfig entityConfig = json.fromJson(EntityConfig.class, string[1]);
                Array<EntityConfig.AnimationConfig> animationConfigs = entityConfig.getAnimationConfig();

                for(EntityConfig.AnimationConfig animationConfig : animationConfigs) {
                    Array<String> textureNames = animationConfig.getTexturePaths();
                    Array<GridPoint2> points = animationConfig.getGridPoints();
                    Entity.AnimationType animationType = animationConfig.getAnimationType();
                    float frameDuration = animationConfig.getFrameDuration();
                    Animation<TextureRegion> animation = null;

                    if(textureNames.size == 1) {
                        animation = loadAnimation(textureNames.get(0), points, frameDuration);
                    } else if(textureNames.size == 2) {
                        animation = loadAnimation(textureNames.get(0), textureNames.get(1), points, frameDuration);
                    }

                    animations.put(animationType, animation);
                }
            }
        }
    }

    @Override
    public void update(Entity entity, MapManager mapMgr, Batch batch, float delta) {
        updateAnimations(delta);

        //Player has moved
        if(previousPosition.x != currentPosition.x || previousPosition.y != currentPosition.y) {
            previousPosition = currentPosition.cpy();
        }

        Camera camera = mapMgr.getCamera();
        camera.position.set(currentPosition.x, currentPosition.y, 0f);
        camera.update();

        batch.begin();
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1, 1);
        batch.end();

        //Used to graphically debug boundingboxes
        /*
        Rectangle rect = entity.getCurrentBoundingBox();
        _shapeRenderer.setProjectionMatrix(camera.combined);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        _shapeRenderer.setColor(Color.RED);
        _shapeRenderer.rect(rect.getX() * Map.UNIT_SCALE , rect.getY() * Map.UNIT_SCALE, rect.getWidth() * Map.UNIT_SCALE, rect.getHeight()*Map.UNIT_SCALE);
        _shapeRenderer.end();
        */
    }

    @Override
    public void dispose() {
        // Nothing
    }

}
