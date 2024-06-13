package com.gdx.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class InventoryItemFactoryTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testGetInstance_ShouldSucceed() {
        InventoryItemFactory inventoryItemFactory = InventoryItemFactory.getInstance();

        assertThat(inventoryItemFactory).isNotNull();
    }

    @Test
    public void testGetInventoryItem_ShouldSucceed() {
        InventoryItemFactory inventoryItemFactory = InventoryItemFactory.getInstance();

        InventoryItem inventoryItem = inventoryItemFactory.getInventoryItem(InventoryItem.ItemTypeID.ARMOR01);

        assertThat(inventoryItemFactory).isNotNull();
        assertThat(inventoryItem).isNotNull();
    }
}
