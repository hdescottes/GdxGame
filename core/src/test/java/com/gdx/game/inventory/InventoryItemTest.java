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
public class InventoryItemTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testInventoryItem_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem();
        InventoryItem inventoryItem1 = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR02));

        assertThat(inventoryItem).isNotNull();
        assertThat(inventoryItem1).isNotNull();
        assertThat(inventoryItem1.getItemTypeID()).isEqualTo(InventoryItem.ItemTypeID.ARMOR02);
        assertThat(inventoryItem1.getItemValue()).isEqualTo(100);
        assertThat(inventoryItem1.getItemAttributes()).isEqualTo(2);
        assertThat(inventoryItem1.getItemUseType()).isEqualTo(512);
        assertThat(inventoryItem1.getItemUseTypeValue()).isEqualTo(30);
        assertThat(inventoryItem1.getItemShortDescription()).isEqualTo("Top tier armor blessed by fairies");
    }

    @Test
    public void testIsStackable_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS01));

        boolean isStackable = inventoryItem.isStackable();

        assertThat(isStackable).isTrue();
    }

    @Test
    public void testIsStackable_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean isStackable = inventoryItem.isStackable();

        assertThat(isStackable).isFalse();
    }

    @Test
    public void testIsConsumable_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS01));

        boolean isConsumable = inventoryItem.isConsumable();

        assertThat(isConsumable).isTrue();
    }

    @Test
    public void testIsConsumable_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean isConsumable = inventoryItem.isConsumable();

        assertThat(isConsumable).isFalse();
    }

    @Test
    public void testIsSameItemType_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS01));
        InventoryItem inventoryItem1 = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS01));

        boolean isSameItemType = inventoryItem.isSameItemType(inventoryItem1);

        assertThat(isSameItemType).isTrue();
    }

    @Test
    public void testIsSameItemType_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));
        InventoryItem inventoryItem1 = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS02));

        boolean isSameItemType = inventoryItem.isSameItemType(inventoryItem1);

        assertThat(isSameItemType).isFalse();
    }

    @Test
    public void testDoesRestoreHP_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.SCROLL01));

        boolean doesRestoreHP = InventoryItem.doesRestoreHP(inventoryItem.getItemUseType());

        assertThat(doesRestoreHP).isTrue();
    }

    @Test
    public void testDoesRestoreHP_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean doesRestoreHP = InventoryItem.doesRestoreHP(inventoryItem.getItemUseType());

        assertThat(doesRestoreHP).isFalse();
    }

    @Test
    public void testDoesRestoreMP_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.POTIONS01));

        boolean doesRestoreMP = InventoryItem.doesRestoreMP(inventoryItem.getItemUseType());

        assertThat(doesRestoreMP).isTrue();
    }

    @Test
    public void testDoesRestoreMP_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean doesRestoreMP = InventoryItem.doesRestoreMP(inventoryItem.getItemUseType());

        assertThat(doesRestoreMP).isFalse();
    }

    @Test
    public void testIsInventoryItemOffensiveWand_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.WANDS01));

        boolean isInventoryItemOffensiveWand = inventoryItem.isInventoryItemOffensiveWand();

        assertThat(isInventoryItemOffensiveWand).isTrue();
    }

    @Test
    public void testIsInventoryItemOffensiveWand_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean isInventoryItemOffensiveWand = inventoryItem.isInventoryItemOffensiveWand();

        assertThat(isInventoryItemOffensiveWand).isFalse();
    }

    @Test
    public void testIsInventoryItemOffensive_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.WEAPON01));

        boolean isInventoryItemOffensive = inventoryItem.isInventoryItemOffensive();

        assertThat(isInventoryItemOffensive).isTrue();
    }

    @Test
    public void testIsInventoryItemOffensive_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean isInventoryItemOffensive = inventoryItem.isInventoryItemOffensive();

        assertThat(isInventoryItemOffensive).isFalse();
    }

    @Test
    public void testIsInventoryItemDefensive_ShouldSucceed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.ARMOR03));

        boolean isInventoryItemDefensive = inventoryItem.isInventoryItemDefensive();

        assertThat(isInventoryItemDefensive).isTrue();
    }

    @Test
    public void testIsInventoryItemDefensive_ShouldFailed() {
        InventoryItem inventoryItem = new InventoryItem(InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.WANDS02));

        boolean isInventoryItemDefensive = inventoryItem.isInventoryItemDefensive();

        assertThat(isInventoryItemDefensive).isFalse();
    }
}
