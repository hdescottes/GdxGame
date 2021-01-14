package com.gdx.game.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.entities.Entity;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;

import java.util.Hashtable;

public abstract class GraphicsComponent extends ComponentSubject implements Component {
    protected TextureRegion currentFrame = null;
    protected float frameTime = 0f;
    protected Entity.State currentState;
    protected Entity.Direction currentDirection;
    protected Json json;
    protected Vector2 currentPosition;
    protected Hashtable<Entity.AnimationType, Animation<TextureRegion>> animations;
    protected ShapeRenderer shapeRenderer;

    protected GraphicsComponent() {
        currentPosition = new Vector2(0,0);
        currentState = Entity.State.WALKING;
        currentDirection = Entity.Direction.DOWN;
        json = new Json();
        animations = new Hashtable<>();
        shapeRenderer = new ShapeRenderer();
    }

    public Vector2 getCurrentPosition() {
        return currentPosition;
    }

    public abstract void update(Entity entity, MapManager mapManager, Batch batch, float delta);

    protected void updateAnimations(float delta) {
        frameTime = (frameTime + delta)%5; //Want to avoid overflow

        //Look into the appropriate variable when changing position
        switch(currentDirection) {
            case DOWN:
                if(currentState == Entity.State.WALKING) {
                    Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_DOWN);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_DOWN);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrames()[0];
                } else if(currentState == Entity.State.IMMOBILE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case LEFT:
                if(currentState == Entity.State.WALKING) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_LEFT);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_LEFT);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrames()[0];
                } else if(currentState == Entity.State.IMMOBILE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case UP:
                if(currentState == Entity.State.WALKING) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_UP);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_UP);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrames()[0];
                } else if(currentState == Entity.State.IMMOBILE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case RIGHT:
                if(currentState == Entity.State.WALKING) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_RIGHT);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.WALK_RIGHT);
                    if( animation == null ) {
                        return;
                    }
                    currentFrame = animation.getKeyFrames()[0];
                } else if(currentState == Entity.State.IMMOBILE) {
					Animation<TextureRegion> animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if(animation == null) {
                        return;
                    }
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            default:
                break;
        }
    }

    //Specific to two frame animations where each frame is stored in a separate texture
    protected Animation<TextureRegion> loadAnimation(String firstTexture, String secondTexture, Array<GridPoint2> points, float frameDuration) {
        ResourceManager.loadTextureAsset(firstTexture);
        Texture texture1 = ResourceManager.getTextureAsset(firstTexture);

        ResourceManager.loadTextureAsset(secondTexture);
        Texture texture2 = ResourceManager.getTextureAsset(secondTexture);

        TextureRegion[][] texture1Frames = TextureRegion.split(texture1, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);
        TextureRegion[][] texture2Frames = TextureRegion.split(texture2, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        GridPoint2 point = points.first();

		Animation<TextureRegion> animation = new Animation<>(frameDuration, texture1Frames[point.x][point.y],texture2Frames[point.x][point.y]);
		animation.setPlayMode(Animation.PlayMode.LOOP);

        return animation;
    }

    protected Animation<TextureRegion> loadAnimation(String textureName, Array<GridPoint2> points, float frameDuration) {
        ResourceManager.loadTextureAsset(textureName);
        Texture texture = ResourceManager.getTextureAsset(textureName);

        TextureRegion[][] textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        TextureRegion[] animationKeyFrames = new TextureRegion[points.size];

        for(int i=0; i < points.size; i++) {
			animationKeyFrames[i] = textureFrames[points.get(i).x][points.get(i).y];
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, animationKeyFrames);
		animation.setPlayMode(Animation.PlayMode.LOOP);

        return animation;
    }

    public Animation<TextureRegion> getAnimation(Entity.AnimationType type) {
        return animations.get(type);
    }
}
