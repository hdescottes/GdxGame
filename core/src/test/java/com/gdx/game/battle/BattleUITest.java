package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class BattleUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testBattleUI_ShouldSucceed() {
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.setProperty("currentPlayerAP", 5);
        profileManager.setProperty("currentPlayerDP", 5);

        BattleState battleState = new BattleState();
        BattleInventoryUI battleInventoryUI = new BattleInventoryUI();
        BattleUI battleUI = new BattleUI(battleState, battleInventoryUI);

        assertThat(battleUI).isNotNull();
        assertThat(battleUI.getChildren()).hasSize(2);
    }
}
