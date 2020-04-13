package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.manager.AnimationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
class AnimationManagerTest {

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
        Gdx.gl = mock(GL20.class);
    }

    @Test
    void testSetTextureRegion_ShouldSucceed() {
        AnimationManager animationManager = new AnimationManager();
        TextureRegion[] textureRegions = TextureRegion.split(Media.heroWalkUp, 32, 37)[0];
        Animation<TextureRegion> animation = new Animation<>(0.1f, textureRegions);

        TextureRegion textureRegion = animationManager.setTextureRegion(animation, 0);

        assertThat(textureRegion).isEqualTo(textureRegions[0]);
    }

    @Test
    void testSetTextureRegions_ShouldSucceed() {
        AnimationManager animationManager = new AnimationManager();

        TextureRegion[] textureRegions = animationManager.setTextureRegions(Media.heroWalkUp, 32, 37);

        assertThat(textureRegions.length).isEqualTo(3);
    }

    @Test
    void testSetTextureRegionsDouble_ShouldSucceed() {
        AnimationManager animationManager = new AnimationManager();

        TextureRegion[][] textureRegions = animationManager.setTextureRegionsDouble(Media.heroWalkUp, 32, 37);

        assertThat(textureRegions.length).isEqualTo(1);
        assertThat(textureRegions[0].length).isEqualTo(3);
    }

    @Test
    void testSetAnimation_ShouldSucceed() {
        AnimationManager animationManager = new AnimationManager();
        TextureRegion[] textureRegions = TextureRegion.split(Media.heroWalkUp, 32, 37)[0];

        Animation<TextureRegion> animation = animationManager.setAnimation(textureRegions);

        assertThat(animation.getFrameDuration()).isEqualTo(0.1f);
        assertThat(animation.getKeyFrame(0.1f)).isEqualTo(textureRegions[1]);
    }

    @Test
    void testSetFlipped_ShouldSucceed() {
        AnimationManager animationManager = new AnimationManager();
        Vector3 destVec = new Vector3();
        destVec.set(10,10,0);
        TextureRegion textureRegion = new TextureRegion();
        textureRegion.setTexture(Media.heroWalkRight);
        textureRegion.setRegion(1,1,10,10);

        animationManager.setFlipped(destVec, textureRegion);

        assertThat(textureRegion.getRegionX()).isEqualTo(11);
    }
}
