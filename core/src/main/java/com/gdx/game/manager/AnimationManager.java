package com.gdx.game.manager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class AnimationManager {

    public TextureRegion setTextureRegion(Animation<TextureRegion> animation, float time) {
        return animation.getKeyFrame(time, true);
    }

    public TextureRegion[] setTextureRegions(Texture texture, int tileWidth, int tileHeight) {
        return TextureRegion.split(texture, tileWidth, tileHeight)[0];
    }

    public TextureRegion[][] setTextureRegionsDouble(Texture texture, int tileWidth, int tileHeight) {
        return TextureRegion.split(texture, tileWidth, tileHeight);
    }

    public Animation<TextureRegion> setAnimation(TextureRegion[] textureRegions) {
        return new Animation<>(0.1f, textureRegions);
    }

    public void setFlipped(Vector3 destVec, TextureRegion tRegion) {
        if(destVec != null && ((destVec.x > 0 && !tRegion.isFlipX()) || (destVec.x < 0 && tRegion.isFlipX()))) {
            tRegion.flip(true, false);
        }
    }
}
