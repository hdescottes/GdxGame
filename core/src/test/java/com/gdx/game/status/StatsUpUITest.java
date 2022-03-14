package com.gdx.game.status;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class StatsUpUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testStatsUpUI_ShouldSucceed() {
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.setProperty("currentPlayerCharacterAP", 5);
        profileManager.setProperty("currentPlayerCharacterDP", 5);

        StatsUpUI statsUpUI = new StatsUpUI(1);

        assertThat(statsUpUI).isNotNull();
        assertThat(statsUpUI.getChildren().size).isEqualTo(16);
        assertThat(Arrays.stream(statsUpUI.getChildren().items).count()).isEqualTo(24);
    }
}
