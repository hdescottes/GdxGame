package com.gdx.game.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class AnimationUtils {

    public static TextureRegion setTextureRegion(Animation<TextureRegion> animation, float time) {
        return animation.getKeyFrame(time, true);
    }

    public static void setFlipped(Vector3 destVec, TextureRegion tRegion) {
        if(destVec != null && ((destVec.x > 0 && !tRegion.isFlipX()) || (destVec.x < 0 && tRegion.isFlipX()))) {
            tRegion.flip(true, false);
        }
    }
}
