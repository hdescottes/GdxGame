package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
class AnimationManagerTest {

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
        Gdx.gl = mock(GL20.class);
    }

    @Test
    void testSetTextureRegionsDouble_ShouldSucceed() {
        ResourceManager resourceManager = new ResourceManager();
        AnimationManager animationManager = new AnimationManager();

        TextureRegion[][] textureRegions = animationManager.setTextureRegionsDouble(resourceManager.heroWalkUp, 32, 37);

        assertThat(textureRegions.length).isEqualTo(1);
        assertThat(textureRegions[0].length).isEqualTo(3);
    }

    @Test
    void testSetAnimation_ShouldSucceed() {
        ResourceManager resourceManager = new ResourceManager();
        AnimationManager animationManager = new AnimationManager();
        TextureRegion[] textureRegions = TextureRegion.split(resourceManager.heroWalkUp, 32, 37)[0];

        Animation<TextureRegion> animation = animationManager.setAnimation(textureRegions);

        assertThat(animation.getFrameDuration()).isEqualTo(0.1f);
        assertThat(animation.getKeyFrame(0.1f)).isEqualTo(textureRegions[1]);
    }
}
