package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
class ControlTest {

    private Control control = new Control(100,50, new OrthographicCamera());

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
    }

    @Test
    void testKeyDown_ShouldSucceed() {
        control.keyDown(20);
        assertThat(control.isDown()).isEqualTo(true);

        control.keyDown(19);
        assertThat(control.isUp()).isEqualTo(true);

        control.keyDown(21);
        assertThat(control.isLeft()).isEqualTo(true);

        control.keyDown(22);
        assertThat(control.isRight()).isEqualTo(true);
    }

    @Test
    void testKeyUp_ShouldSucceed() {
        control.keyUp(20);
        assertThat(control.isDown()).isEqualTo(false);

        control.keyUp(19);
        assertThat(control.isUp()).isEqualTo(false);

        control.keyUp(21);
        assertThat(control.isLeft()).isEqualTo(false);

        control.keyUp(22);
        assertThat(control.isRight()).isEqualTo(false);

        control.setDebug(false);
        control.keyUp(67);
        assertThat(control.isDebug()).isEqualTo(true);
    }

    @Test
    void testKeyTyped_ShouldSucceed() {
        Boolean isKeyTyped = control.keyTyped('A');

        assertThat(isKeyTyped).isEqualTo(false);
    }

    @Test
    void testGetMapCoords_ShouldSucceed() {
        int screenWidth = control.getScreenWidth();
        int screenHeight = control.getScreenHeight();
        Vector2 mouseCoords = new Vector2();
        mouseCoords.set(10, 10);
        given(Gdx.graphics.getWidth()).willReturn(screenWidth);
        given(Gdx.graphics.getHeight()).willReturn(screenHeight);

        Vector2 mapCoords = control.getMapCoords(mouseCoords);

        assertThat(mapCoords.x).isEqualTo((2*mouseCoords.x) / screenWidth - 1);
        assertThat(mapCoords.y).isEqualTo((2*(screenHeight-(screenHeight-mouseCoords.y)-1)) / screenHeight - 1);
    }

    @Test
    void testTouchDragged_ShouldSucceed() {
        int screenHeight = control.getScreenHeight();
        Boolean touchDragged = control.touchDragged(10, 5, 1);

        assertThat(control.getMouseClickPos().x).isEqualTo(10);
        assertThat(control.getMouseClickPos().y).isEqualTo(screenHeight-5);
        assertThat(control.getMapClickPos()).isEqualTo(control.getMapClickPos());
        assertThat(touchDragged).isEqualTo(false);
    }

    @Test
    void testMouseMoved_ShouldSucceed() {
        Boolean hasMouseMoved = control.mouseMoved(control.getScreenWidth(), control.getScreenHeight());

        assertThat(hasMouseMoved).isEqualTo(false);
    }

    @Test
    void testScrolled_ShouldSucceed() {
        Boolean hasMouseScrolled = control.scrolled(1);

        assertThat(hasMouseScrolled).isEqualTo(false);
    }
}
