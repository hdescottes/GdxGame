package com.gdx.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class InventoryUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testInventoryUI_ShouldSucceed() {
        InventoryUI inventoryUI = new InventoryUI();

        assertThat(inventoryUI).isNotNull();
        assertThat(inventoryUI.getDragAndDrop()).isNotNull();
    }
}
