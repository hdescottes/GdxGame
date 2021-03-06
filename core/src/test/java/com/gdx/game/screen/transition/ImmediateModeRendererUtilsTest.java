package com.gdx.game.screen.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.badlogic.gdx.math.Matrix4.M00;
import static com.badlogic.gdx.math.Matrix4.M03;
import static com.badlogic.gdx.math.Matrix4.M11;
import static com.badlogic.gdx.math.Matrix4.M13;
import static com.badlogic.gdx.math.Matrix4.M22;
import static com.badlogic.gdx.math.Matrix4.M23;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Disabled
@ExtendWith(GdxRunner.class)
class ImmediateModeRendererUtilsTest {

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    void testGetProjectionMatrix_ShouldSucceed() {
        Matrix4 matrix4 = ImmediateModeRendererUtils.getProjectionMatrix();

        assertThat(matrix4).isNotNull();
        assertThat(matrix4.val[M00]).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(matrix4.val[M11]).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(matrix4.val[M22]).isEqualTo(-2.0f);
        assertThat(matrix4.val[M03]).isNaN();
        assertThat(matrix4.val[M13]).isNaN();
        assertThat(matrix4.val[M23]).isEqualTo(-1.0f);
    }
}
