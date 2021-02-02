package com.gdx.game.inventory.slot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class InventorySlotTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testInventorySlot_ShouldSucceed() {
        InventorySlot inventorySlot = new InventorySlot();

        assertThat(inventorySlot).isNotNull();
        assertThat(inventorySlot.hasItem()).isFalse();
    }
}
