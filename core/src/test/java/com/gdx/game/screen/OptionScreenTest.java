package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.gdx.game.GdxGame;
import com.gdx.game.GdxRunner;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class OptionScreenTest {

    private MockedConstruction<Stage> mockStage;
    private MockedConstruction<VfxManager> mockVfxManager;
    private MockedConstruction<GaussianBlurEffect> mockGaussianEffect;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockStage = mockConstruction(Stage.class);
        mockVfxManager = mockConstruction(VfxManager.class);
        mockGaussianEffect = mockConstruction(GaussianBlurEffect.class);
    }

    @AfterEach
    void end() {
        mockStage.close();
        mockVfxManager.close();
        mockGaussianEffect.close();
    }

    @Test
    void screen_instance() {
        GdxGame gdxGame = mock(GdxGame.class);
        BaseScreen baseScreen = mock(BaseScreen.class);
        ResourceManager resourceManager = new ResourceManager();
        OptionScreen screen = new OptionScreen(gdxGame, baseScreen, resourceManager);

        assertNotNull(screen);
    }
}
