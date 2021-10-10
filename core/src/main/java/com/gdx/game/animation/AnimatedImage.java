package com.gdx.game.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.gdx.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedImage extends Image {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimatedImage.class);
    private float frameTime = 0;
    protected Entity entity;
    private Entity.AnimationType currentAnimationType = Entity.AnimationType.IDLE;

    public AnimatedImage() {
        super();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        //set default
        setCurrentAnimation(Entity.AnimationType.IDLE);
    }

    public Entity.AnimationType getCurrentAnimationType() {
        return this.currentAnimationType;
    }

    public void setCurrentAnimation(Entity.AnimationType animationType){
        Animation<TextureRegion> animation = entity.getAnimation(animationType);
        if( animation == null ){
            LOGGER.debug("Animation type " + animationType.toString() + " does not exist!");
            return;
        }

        this.currentAnimationType = animationType;
        this.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(0)));
        this.setScaling(Scaling.stretch);
        this.setAlign(Align.center);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Drawable drawable = this.getDrawable();
        if(drawable == null) {
            //Gdx.app.debug(TAG, "Drawable is NULL!");
            return;
        }
        frameTime = (frameTime + delta)%5;
        TextureRegion currentRegion = entity.getAnimation(currentAnimationType).getKeyFrame(frameTime, true);
        //Gdx.app.debug(TAG, "Keyframe number is " + _animation.getKeyFrameIndex(_frameTime));
        ((TextureRegionDrawable) drawable).setRegion(currentRegion);

        for (Action action : this.getActions()) {
            action.act(delta);
        }
    }

}
