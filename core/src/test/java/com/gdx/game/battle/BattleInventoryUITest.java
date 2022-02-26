package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class BattleInventoryUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testBattleInventoryUI_ShouldSucceed() {
        BattleInventoryUI battleInventoryUI = new BattleInventoryUI();

        assertThat(battleInventoryUI).isNotNull();
        assertThat(battleInventoryUI.getChildren().size).isEqualTo(2);
        assertThat(Arrays.stream(battleInventoryUI.getChildren().items).count()).isEqualTo(4);
    }
}
