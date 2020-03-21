package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.animation.AnimationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.gdx.game.animation.AnimationUtils.setTextureRegion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
class AnimationUtilsTest {

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
        Gdx.gl = mock(GL20.class);
    }

    @Test
    void testSetTextureRegion_ShouldSucceed() {
        TextureRegion textureRegion = setTextureRegion(Media.heroWalkUpAnim, 0);

        assertThat(textureRegion).isEqualTo(Media.heroWalkUpFrames[0]);
    }

    @Test
    void testSetFlipped() {
        Vector3 destVec = new Vector3();
        destVec.set(10,10,0);
        TextureRegion textureRegion = new TextureRegion();
        textureRegion.setTexture(Media.heroWalkRight);
        textureRegion.setRegion(1,1,10,10);

        AnimationUtils.setFlipped(destVec, textureRegion);

        assertThat(textureRegion.getRegionX()).isEqualTo(11);
    }
}
