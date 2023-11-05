package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
public class MenuLoadGameScreenTest {

    private MockedConstruction<Stage> mockStage;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockStage = mockConstruction(Stage.class);
    }

    @AfterEach
    void end() {
        mockStage.close();
    }

    @Test
    void screen_instance() {
        GdxGame gdxGame = mock(GdxGame.class);
        BaseScreen baseScreen = mock(BaseScreen.class);
        ResourceManager resourceManager = new ResourceManager();
        MenuLoadGameScreen screen = new MenuLoadGameScreen(gdxGame, baseScreen, resourceManager);

        assertNotNull(screen);
    }
}
