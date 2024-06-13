package com.gdx.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.inventory.item.InventoryItemLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class InventoryItemLocationTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testInventoryItemLocation_ShouldSucceed() {
        int locationIndex = 0;
        String itemTypeAtLocation = "ARMOR04";
        int numberItemAtLocation = 1;
        String itemNameProperty = "Player_Inventory";

        InventoryItemLocation inventoryItemLocation = new InventoryItemLocation(locationIndex, itemTypeAtLocation, numberItemAtLocation, itemNameProperty);

        assertThat(inventoryItemLocation).isNotNull();
        assertThat(inventoryItemLocation.getItemTypeAtLocation()).isEqualTo(itemTypeAtLocation);
        assertThat(inventoryItemLocation.getLocationIndex()).isEqualTo(locationIndex);
        assertThat(inventoryItemLocation.getNumberItemsAtLocation()).isEqualTo(numberItemAtLocation);
        assertThat(inventoryItemLocation.getItemNameProperty()).isEqualTo(itemNameProperty);
    }
}
