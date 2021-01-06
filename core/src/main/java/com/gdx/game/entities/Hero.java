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
import com.gdx.game.entities.EntityEnums.ENTITYSTATE;
import com.gdx.game.entities.EntityEnums.ENTITYTYPE;
import com.gdx.game.manager.AnimationManager;
import com.gdx.game.manager.ControlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class Hero extends Entity {

    private static final int HERO_SPEED = 40;
    private boolean collision = false;
    private Entity entityCollision;
    private TextureRegion tRegion;
    private AnimationManager animationManager = new AnimationManager();
    private ResourceManager resourceManager;
    private ArrayList<Entity> interactEntities = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(Hero.class);

    public Hero(Vector3 pos3, Box2dWorld box2d, ENTITYSTATE state, ResourceManager resourceManager) {
        super(resourceManager.hero,null,8, 8);
        this.resourceManager = resourceManager;
        this.type = ENTITYTYPE.PLAYER;
        this.getPos3().x = pos3.x;
        this.getPos3().y = pos3.y;
        this.state = state;
        this.body = Box2dHelper.createBody(box2d.getWorld(), getWidth()/2, getHeight()/2, getWidth()/4, 0, pos3, null, BodyDef.BodyType.DynamicBody);
        this.hashcode = body.getFixtureList().get(0).hashCode();
    }

    public void update(ControlManager controlManager) {
        float directionX = getDirectionX();
        float directionY = getDirectionY();

        if(controlManager.isDown()) {
            directionY = -1 ;
            state = ENTITYSTATE.WALKING_DOWN;
        } else if(state == ENTITYSTATE.WALKING_DOWN){
            state = ENTITYSTATE.LOOK_DOWN;
        }
        if(controlManager.isUp()) {
            directionY = 1 ;
            state = ENTITYSTATE.WALKING_UP;
        } else if(state == ENTITYSTATE.WALKING_UP){
            state = ENTITYSTATE.LOOK_UP;
        }
        if(controlManager.isLeft()) {
            directionX = -1;
            state = ENTITYSTATE.WALKING_LEFT;
        } else if(state == ENTITYSTATE.WALKING_LEFT){
            state = ENTITYSTATE.LOOK_LEFT;
        }
        if(controlManager.isRight()) {
            directionX = 1;
            state = ENTITYSTATE.WALKING_RIGHT;
        } else if(state == ENTITYSTATE.WALKING_RIGHT){
            state = ENTITYSTATE.LOOK_RIGHT;
        }

        body.setLinearVelocity(directionX * HERO_SPEED, directionY * HERO_SPEED);

        updatePositions();

        interactAction(controlManager);
    }

    private void interactAction(ControlManager controlManager) {
        Stream.of(interactEntities)
                .filter(i -> controlManager.isInteract() && !i.isEmpty())
                .forEach(i -> i.get(0).interact());

        controlManager.setInteract(false);
    }

    @Override
    public void collision(Entity entity, boolean begin) {
        if(begin){
            // Hero entered hitbox
            interactEntities.add(entity);
            LOGGER.info(String.format("You encountered a %s", entity.getClass().getSimpleName()));
            setCollision(true);
            setEntityCollision(entity);
        } else {
            // Hero Left hitbox
            interactEntities.remove(entity);
            setCollision(false);
            setEntityCollision(null);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        setHeroTextureRegion();
        //AnimationUtils.setFlipped(destVec, tRegion);

        batch.draw(resourceManager.heroShadow, getPos3().x, getPos3().y - (float)1.6, getWidth(), getHeight()/2);
        Optional.ofNullable(tRegion)
                .ifPresent(t -> batch.draw(t, getPos3().x, getPos3().y, getWidth(), getHeight()*(float)1.2));
    }

    private void setHeroTextureRegion() {
        if(isWalkingUp()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkUp)), time);
        } else if(isWalkingDown()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkDown)), time);
        } else if(isWalkingRight()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkRight)), time);
        } else if(isWalkingLeft()) {
            tRegion = animationManager.setTextureRegion(animation(textureRegions(resourceManager.heroWalkLeft)), time);
        } else if(isLookingUp()) {
            tRegion = textureRegions(resourceManager.heroWalkUp)[1];
        } else if(isLookingDown()) {
            tRegion = textureRegions(resourceManager.heroWalkDown)[1];
        } else if(isLookingRight()) {
            tRegion = textureRegions(resourceManager.heroWalkRight)[1];
        } else if(isLookingLeft()) {
            tRegion = textureRegions(resourceManager.heroWalkLeft)[1];
        }
    }

    private TextureRegion[] textureRegions(Texture texture) {
        return animationManager.setTextureRegions(texture, 32,37);
    }

    private Animation<TextureRegion> animation(TextureRegion[] textureRegions) {
        return animationManager.setAnimation(textureRegions);
    }

    public float getCameraX() {
        return getPos3().x + getWidth()/2;
    }

    public float getCameraY() {
        return getPos3().y + getHeight()/2;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public Entity getEntityCollision() {
        return entityCollision;
    }

    public void setEntityCollision(Entity entityCollision) {
        this.entityCollision = entityCollision;
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
