package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.manager.ControlManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
class ControlManagerTest {

    private ControlManager controlManager = new ControlManager(100,50, new OrthographicCamera());

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
    }

    @Test
    void testKeyDown_ShouldSucceed() {
        controlManager.keyDown(20);
        assertThat(controlManager.isDown()).isEqualTo(true);

        controlManager.keyDown(19);
        assertThat(controlManager.isUp()).isEqualTo(true);

        controlManager.keyDown(21);
        assertThat(controlManager.isLeft()).isEqualTo(true);

        controlManager.keyDown(22);
        assertThat(controlManager.isRight()).isEqualTo(true);
    }

    @Test
    void testKeyUp_ShouldSucceed() {
        controlManager.keyUp(20);
        assertThat(controlManager.isDown()).isEqualTo(false);

        controlManager.keyUp(19);
        assertThat(controlManager.isUp()).isEqualTo(false);

        controlManager.keyUp(21);
        assertThat(controlManager.isLeft()).isEqualTo(false);

        controlManager.keyUp(22);
        assertThat(controlManager.isRight()).isEqualTo(false);

        controlManager.setDebug(false);
        controlManager.keyUp(67);
        assertThat(controlManager.isDebug()).isEqualTo(true);
    }

    @Test
    void testKeyTyped_ShouldSucceed() {
        Boolean isKeyTyped = controlManager.keyTyped('A');

        assertThat(isKeyTyped).isEqualTo(false);
    }

    @Test
    void testGetMapCoords_ShouldSucceed() {
        int screenWidth = controlManager.getScreenWidth();
        int screenHeight = controlManager.getScreenHeight();
        Vector2 mouseCoords = new Vector2();
        mouseCoords.set(10, 10);
        given(Gdx.graphics.getWidth()).willReturn(screenWidth);
        given(Gdx.graphics.getHeight()).willReturn(screenHeight);

        Vector2 mapCoords = controlManager.getMapCoords(mouseCoords);

        assertThat(mapCoords.x).isEqualTo((2*mouseCoords.x) / screenWidth - 1);
        assertThat(mapCoords.y).isEqualTo((2*(screenHeight-(screenHeight-mouseCoords.y)-1)) / screenHeight - 1);
    }

    @Test
    void testTouchDragged_ShouldSucceed() {
        int screenHeight = controlManager.getScreenHeight();
        Boolean touchDragged = controlManager.touchDragged(10, 5, 1);

        assertThat(controlManager.getMouseClickPos().x).isEqualTo(10);
        assertThat(controlManager.getMouseClickPos().y).isEqualTo(screenHeight-5);
        assertThat(controlManager.getMapClickPos()).isEqualTo(controlManager.getMapClickPos());
        assertThat(touchDragged).isEqualTo(false);
    }

    @Test
    void testMouseMoved_ShouldSucceed() {
        Boolean hasMouseMoved = controlManager.mouseMoved(controlManager.getScreenWidth(), controlManager.getScreenHeight());

        assertThat(hasMouseMoved).isEqualTo(false);
    }

    @Test
    void testScrolled_ShouldSucceed() {
        Boolean hasMouseScrolled = controlManager.scrolled(1);

        assertThat(hasMouseScrolled).isEqualTo(false);
    }
}
