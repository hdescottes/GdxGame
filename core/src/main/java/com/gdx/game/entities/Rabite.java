package com.gdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.manager.AnimationManager;
import com.gdx.game.map.demo.Tile;

import java.util.Optional;

public class Rabite extends Entity{

    private static final int RABITE_SPEED = 15;
    private Tile destTile;
    private TextureRegion tRegion;
    private AnimationManager animationManager = new AnimationManager();
    private ResourceManager resourceManager;

    public Rabite(Vector3 pos3, Box2dWorld box2d, EntityEnums.ENTITYSTATE state, ResourceManager resourceManager) {
        super(resourceManager.rabite,null,8, 8);
        this.resourceManager = resourceManager;
        this.type = EntityEnums.ENTITYTYPE.ENEMY;
        this.getPos3().x = pos3.x;
        this.getPos3().y = pos3.y;
        this.body = Box2dHelper.createBody(box2d.getWorld(), getWidth()/2, getHeight()/2, getWidth()/4 + 0.1f, +2, pos3, null, BodyDef.BodyType.StaticBody);
        this.sensor = Box2dHelper.createSensor(box2d.getWorld(), getWidth()*.6f, getHeight()*.6f,getWidth()/4 - 0.3f, 1.6f, pos3, null, BodyDef.BodyType.StaticBody);
        this.hashcode = sensor.getFixtureList().get(0).hashCode();
        this.state = state;
        this.ticks = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        state = EntityEnums.ENTITYSTATE.WALKING_DOWN;
        setRabiteTextureRegion();
        //animationManager.setFlipped(destVec, tRegion);

        batch.draw(resourceManager.heroShadow, getPos3().x + 0.1f, getPos3().y, getWidth(), getHeight()/2);
        Optional.ofNullable(tRegion)
                .ifPresent(t -> batch.draw(t, getPos3().x, getPos3().y, getWidth(), getHeight()*(float)1.2));
    }

    private void setRabiteTextureRegion() {
        if(isWalkingUp()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkUp)), time);
        } else if(isWalkingDown()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.rabiteWalkDown)), time);
        } else if(isWalkingRight()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkRight)), time);
        } else if(isWalkingLeft()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkLeft)), time);
        } else if(isLookingUp()) {
            tRegion = textureRegions(resourceManager.heroWalkUp)[1];
        } else if(isLookingDown()) {
            tRegion = textureRegions(resourceManager.rabiteWalkDown)[3];
        } else if(isLookingRight()) {
            tRegion = textureRegions(resourceManager.heroWalkRight)[1];
        } else if(isLookingLeft()) {
            tRegion = textureRegions(resourceManager.heroWalkLeft)[1];
        }
    }

    private TextureRegion[] textureRegions(Texture texture) {
        return animationManager.setTextureRegions(texture, 27,31);
    }

    private Animation<TextureRegion> animation(TextureRegion[] textureRegions) {
        return animationManager.setAnimation(textureRegions);
    }
/*
    @Override
    public void tick(float delta, Chunk chunk) {
        if(needsDestination()) {
            newDestination(chunk);
        } else if(hasDestination()) {
            moveToDestination(delta);
            clearDestination();
            setNewState(delta);
        }
    }

    private void newDestination(Chunk chunk) {
        // 15% chance a new destination is set, unless over water then always
        // get a new destination
        if(MathUtils.randomBoolean(.85f) || currentTile.isWater()) {
            setDestination(chunk);
        } else {
            state = EntityEnums.ENTITYSTATE.IDLE;
        }
    }

    private void setDestination(Chunk chunk){
        for(ArrayList<Tile> row : chunk.getTiles()) {
            if(destTile != null) {
                break;
            }
            for(Tile tile : row) {
                if (tile.isGrass() && MathUtils.random(100) > 99 && tile != currentTile) {
                    destTile = tile;
                    getVector(destTile.getPos3());
                    break;
                }
            }
        }
    }

    private void setNewState(float delta) {
        if(coolDown > 0){
            coolDown -= delta;
            /*if(isWalking()){
                walk(delta);
            }
        } else {
            if(MathUtils.randomBoolean(.25f)) {
                state = EntityEnums.ENTITYSTATE.WALKING_UP;
                coolDown = 1f;
            } else if(MathUtils.randomBoolean(.25f)) {
                state = EntityEnums.ENTITYSTATE.WALKING_DOWN;
                coolDown = 1f;
            } else if(MathUtils.randomBoolean(.25f)) {
                state = EntityEnums.ENTITYSTATE.WALKING_LEFT;
                coolDown = 1f;
            } else if(MathUtils.randomBoolean(.25f)) {
                state = EntityEnums.ENTITYSTATE.WALKING_RIGHT;
                coolDown = 1f;
            }
        }
    }

    private void clearDestination() {
        if(isAtDestination()) {
            destVec = null;
            destTile = null;
        }
    }

    private void moveToDestination(float delta) {
        body.setTransform(body.getPosition().interpolate(new Vector2(destTile.getPos3().x + getWidth(),
                destTile.getPos3().y + getHeight()), delta * RABITE_SPEED / 4, Interpolation.circle), 0);
        sensor.setTransform(body.getPosition().interpolate(new Vector2(destTile.getPos3().x + getWidth(),
                destTile.getPos3().y + getHeight()), delta * RABITE_SPEED / 4, Interpolation.circle), 0);

        updatePositions();
    }

    private void walk(float delta) {
        if(currentTile.isPassable()) {
            if(tRegion.isFlipX()) {
                body.setTransform(body.getPosition().x - RABITE_SPEED / 4 * delta, body.getPosition().y,0);
                sensor.setTransform(body.getPosition().x - RABITE_SPEED / 4 * delta, body.getPosition().y,0);
            } else {
                body.setTransform(body.getPosition().x + RABITE_SPEED / 4 * delta, body.getPosition().y,0);
                sensor.setTransform(body.getPosition().x + RABITE_SPEED / 4 * delta, body.getPosition().y,0);
            }
            updatePositions();
        }
    }*/

    private boolean isWalkingUp(){
        return state == EntityEnums.ENTITYSTATE.WALKING_UP;
    }

    private boolean isWalkingDown(){
        return state == EntityEnums.ENTITYSTATE.WALKING_DOWN;
    }

    private boolean isWalkingRight(){
        return state == EntityEnums.ENTITYSTATE.WALKING_RIGHT;
    }

    private boolean isWalkingLeft(){
        return state == EntityEnums.ENTITYSTATE.WALKING_LEFT;
    }

    private boolean isLookingUp() {
        return state == EntityEnums.ENTITYSTATE.LOOK_UP;
    }

    private boolean isLookingDown() {
        return state == EntityEnums.ENTITYSTATE.LOOK_DOWN;
    }

    private boolean isLookingRight() {
        return state == EntityEnums.ENTITYSTATE.LOOK_RIGHT;
    }

    private boolean isLookingLeft() {
        return state == EntityEnums.ENTITYSTATE.LOOK_LEFT;
    }

    private boolean hasDestination() {
        return destVec != null;
    }

    private boolean isAtDestination() {
        return currentTile.getPos3().epsilonEquals(destTile.getPos3(), 20);
    }

    private boolean needsDestination() {
        return destVec == null;
    }

}
