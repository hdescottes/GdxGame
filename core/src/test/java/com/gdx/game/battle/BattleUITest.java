package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityConfig;
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
        EntityBonus entityBonusAtk = new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.3");
        EntityBonus entityBonusDef = new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1");
        profileManager.setProperty("bonusSet", new Array<>(new EntityBonus[]{entityBonusAtk, entityBonusDef}));

        BattleState battleState = new BattleState();
        BattleInventoryUI battleInventoryUI = new BattleInventoryUI();
        BattleUI battleUI = new BattleUI(battleState, battleInventoryUI);

        assertThat(battleUI).isNotNull();
        assertThat(battleUI.getChildren()).hasSize(2);
    }
}
