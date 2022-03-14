package com.gdx.game.status;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.gdx.game.manager.ResourceManager.STATUS_UI_SKIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class StatusUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testStatusUI_ShouldSucceed() {
        StatusUI statusUI = new StatusUI();
        ImageButton inventoryButton = new ImageButton(STATUS_UI_SKIN, "inventory-button");
        ImageButton questButton = new ImageButton(STATUS_UI_SKIN, "quest-button");

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getInventoryButton()).isNotNull();
        assertThat(statusUI.getInventoryButton().getImage().getName()).isEqualTo(inventoryButton.getImage().getName());
        assertThat(statusUI.getQuestButton()).isNotNull();
        assertThat(statusUI.getQuestButton().getImage().getName()).isEqualTo(questButton.getImage().getName());
    }

    @Test
    public void testSetLevelValue_ShouldSucceed() {
        StatusUI statusUI = new StatusUI();
        statusUI.setLevelValue(1, false);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getLevelValue()).isEqualTo(1);
    }

    @Test
    public void testSetGoldValue_ShouldSucceed() {
        StatusUI statusUI = new StatusUI();
        statusUI.setGoldValue(10);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getGoldValue()).isEqualTo(10);
    }

    @Test
    public void testSetXpValue_ShouldSucceedWithoutLevelUp() {
        StatusUI statusUI = new StatusUI();
        statusUI.setLevelValue(1, false);
        statusUI.setXPValueMax(200);
        statusUI.setXPValue(1);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getXPValue()).isEqualTo(1);
        assertThat(statusUI.getLevelValue()).isEqualTo(1);
    }

    @Test
    public void testSetXpValue_ShouldSucceedWithLevelUp() {
        StatusUI statusUI = new StatusUI();
        statusUI.setLevelValue(1, false);
        statusUI.setXPValueMax(200);
        statusUI.setXPValue(210);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getXPValue()).isEqualTo(10);
        assertThat(statusUI.getLevelValue()).isEqualTo(2);
    }

    @Test
    public void testSetXpValue_ShouldSucceedWithTwoLevelUp() {
        StatusUI statusUI = new StatusUI();
        statusUI.setLevelValue(1, false);
        statusUI.setXPValueMax(200);
        statusUI.setXPValue(650);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getXPValue()).isEqualTo(50);
        assertThat(statusUI.getLevelValue()).isEqualTo(3);
    }

    @Test
    public void testSetStatusForLevel_ShouldSucceed() {
        StatusUI statusUI = new StatusUI();

        statusUI.setStatusForLevel(1);

        assertThat(statusUI).isNotNull();
        assertThat(statusUI.getLevelValue()).isEqualTo(1);
        assertThat(statusUI.getXPValue()).isEqualTo(0);
        assertThat(statusUI.getHPValue()).isEqualTo(50);
        assertThat(statusUI.getHPValueMax()).isEqualTo(50);
        assertThat(statusUI.getMPValue()).isEqualTo(50);
        assertThat(statusUI.getMPValueMax()).isEqualTo(50);
    }
}
