package com.gdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.Control;
import com.gdx.game.Enums.ENTITYSTATE;
import com.gdx.game.Enums.ENTITYTYPE;
import com.gdx.game.Media;
import com.gdx.game.animation.AnimationUtils;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;

import java.util.Optional;

public class Hero extends Entity {

    private static final int heroSpeed = 40;
    private TextureRegion tRegion;

    public Hero(Vector3 pos3, Box2dWorld box2d, ENTITYSTATE state) {
        super(Media.hero,null,8, 8);
        this.type = ENTITYTYPE.HERO;
        this.getPos3().x = pos3.x;
        this.getPos3().y = pos3.y;
        this.state = state;
        this.body = Box2dHelper.createBody(box2d.getWorld(), getWidth()/2, getHeight()/2, getWidth()/4, 0, pos3, null, BodyDef.BodyType.DynamicBody);
    }

    public void update(Control control) {
        float directionX = getDirectionX();
        float directionY = getDirectionY();

        if(control.isDown()) {
            directionY = -1 ;
            state = ENTITYSTATE.WALKING_DOWN;
        } else if(state == ENTITYSTATE.WALKING_DOWN){
            state = ENTITYSTATE.LOOK_DOWN;
        }
        if(control.isUp()) {
            directionY = 1 ;
            state = ENTITYSTATE.WALKING_UP;
        } else if(state == ENTITYSTATE.WALKING_UP){
            state = ENTITYSTATE.LOOK_UP;
        }
        if(control.isLeft()) {
            directionX = -1;
            state = ENTITYSTATE.WALKING_LEFT;
        } else if(state == ENTITYSTATE.WALKING_LEFT){
            state = ENTITYSTATE.LOOK_LEFT;
        }
        if(control.isRight()) {
            directionX = 1;
            state = ENTITYSTATE.WALKING_RIGHT;
        } else if(state == ENTITYSTATE.WALKING_RIGHT){
            state = ENTITYSTATE.LOOK_RIGHT;
        }

        body.setLinearVelocity(directionX * heroSpeed, directionY * heroSpeed);

        updatePositions();
    }

    @Override
    public void draw(SpriteBatch batch) {
        setHeroTextureRegion();
        //AnimationUtils.setFlipped(destVec, tRegion);

        batch.draw(Media.heroShadow, getPos3().x, getPos3().y - (float)1.6, getWidth(), getHeight()/2);
        Optional.ofNullable(tRegion)
                .ifPresent(t -> batch.draw(t, getPos3().x, getPos3().y, getWidth(), getHeight()*(float)1.2));
    }

    private void setHeroTextureRegion() {
        if(isWalkingUp()) {
            tRegion = AnimationUtils.setTextureRegion(Media.heroWalkUpAnim, time);
        } else if(isWalkingDown()) {
            tRegion = AnimationUtils.setTextureRegion(Media.heroWalkDownAnim, time);
        } else if(isWalkingRight()) {
            tRegion = AnimationUtils.setTextureRegion(Media.heroWalkRightAnim, time);
        } else if(isWalkingLeft()) {
            tRegion = AnimationUtils.setTextureRegion(Media.heroWalkLeftAnim, time);
        } else if(isLookingUp()) {
            tRegion = Media.heroWalkUpFrames[1];
        } else if(isLookingDown()) {
            tRegion = Media.heroWalkDownFrames[1];
        } else if(isLookingRight()) {
            tRegion = Media.heroWalkRightFrames[1];
        } else if(isLookingLeft()) {
            tRegion = Media.heroWalkLeftFrames[1];
        }
    }

    public float getCameraX() {
        return getPos3().x + getWidth()/2;
    }

    public float getCameraY() {
        return getPos3().y + getHeight()/2;
    }

    private boolean isWalkingUp(){
        return state == ENTITYSTATE.WALKING_UP;
    }

    private boolean isWalkingDown(){
        return state == ENTITYSTATE.WALKING_DOWN;
    }

    private boolean isWalkingRight(){
        return state == ENTITYSTATE.WALKING_RIGHT;
    }

    private boolean isWalkingLeft(){
        return state == ENTITYSTATE.WALKING_LEFT;
    }

    private boolean isLookingUp() {
        return state == ENTITYSTATE.LOOK_UP;
    }

    private boolean isLookingDown() {
        return state == ENTITYSTATE.LOOK_DOWN;
    }

    private boolean isLookingRight() {
        return state == ENTITYSTATE.LOOK_RIGHT;
    }

    private boolean isLookingLeft() {
        return state == ENTITYSTATE.LOOK_LEFT;
    }

}
