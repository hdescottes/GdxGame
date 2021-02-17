package com.gdx.game.entities.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.component.GraphicsComponent;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapManager;

public class NPCGraphicsComponent extends GraphicsComponent {

    private boolean isSelected = false;
    private boolean wasSelected = false;

    private boolean sentShowConversationMessage = false;
    private boolean sentHideConversationMessage = false;

    public NPCGraphicsComponent() {
        // Nothing
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(MESSAGE_TOKEN);

        if(string.length == 0) {
            return;
        }

        if(string.length == 1) {
            if(string[0].equalsIgnoreCase(MESSAGE.ENTITY_SELECTED.toString())) {
                isSelected = !wasSelected;
            } else if(string[0].equalsIgnoreCase(MESSAGE.ENTITY_DESELECTED.toString())) {
                wasSelected = isSelected;
                isSelected = false;
            }
        }

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

        if(isSelected) {
            drawSelected(entity, mapMgr);
            mapMgr.setCurrentSelectedMapEntity(entity);
            if(!sentShowConversationMessage) {
                notify(json.toJson(entity.getEntityConfig()), ComponentObserver.ComponentEvent.SHOW_CONVERSATION);
                sentShowConversationMessage = true;
                sentHideConversationMessage = false;
            }
        } else {
            if(!sentHideConversationMessage) {
                notify(json.toJson(entity.getEntityConfig()), ComponentObserver.ComponentEvent.HIDE_CONVERSATION);
                sentHideConversationMessage = true;
                sentShowConversationMessage = false;
            }
        }

        batch.begin();
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1, 1);
        batch.end();

        //Used to graphically debug boundingboxes
        /*
        Rectangle rect = entity.getCurrentBoundingBox();
        Camera camera = mapMgr.getCamera();
        _shapeRenderer.setProjectionMatrix(camera.combined);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        _shapeRenderer.setColor(Color.BLACK);
        _shapeRenderer.rect(rect.getX() * Map.UNIT_SCALE, rect.getY() * Map.UNIT_SCALE, rect.getWidth() * Map.UNIT_SCALE, rect.getHeight() * Map.UNIT_SCALE);
        _shapeRenderer.end();
        */
    }

    private void drawSelected(Entity entity, MapManager mapMgr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Camera camera = mapMgr.getCamera();
        Rectangle rect = entity.getCurrentBoundingBox();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.0f, 1.0f, 1.0f, 0.5f);

        float width =  rect.getWidth() * Map.UNIT_SCALE*2f;
        float height = rect.getHeight() * Map.UNIT_SCALE/2f;
        float x = rect.x * Map.UNIT_SCALE - width/4;
        float y = rect.y * Map.UNIT_SCALE - height/2;

        shapeRenderer.ellipse(x,y,width,height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


    @Override
    public void dispose() {
        // Nothing
    }
}
