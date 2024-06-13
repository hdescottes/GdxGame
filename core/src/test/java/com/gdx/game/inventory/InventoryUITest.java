package com.gdx.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemLocation;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.inventory.slot.InventorySlotObserver;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.stream.Stream;

import static com.gdx.game.common.UtilityClass.calculateBonus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(GdxRunner.class)
public class InventoryUITest {

    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;

    private static final int PLAYER_AP = 15;
    private static final int PLAYER_DP = 15;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        new ResourceManager();
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.setProperty("playerCharacter", EntityFactory.EntityType.WARRIOR);
        profileManager.setProperty("currentPlayerCharacterAP", PLAYER_AP);
        profileManager.setProperty("currentPlayerCharacterDP", PLAYER_DP);
        profileManager.setProperty("currentPlayerCharacterSPDP", 10);
    }

    @AfterEach
    void end() {
        mockShapeRenderer.close();
        mockSpriteBatch.close();
    }

    @Test
    public void testInventoryUI_ShouldSucceed() {
        InventoryUI inventoryUI = new InventoryUI();

        assertThat(inventoryUI).isNotNull();
        assertThat(inventoryUI.getDragAndDrop()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("itemEventData")
    public void add_item_event(InventoryItem item, int playerStat, String profileSave, InventoryObserver.InventoryEvent inventoryEvent) {
        InventorySlot inventorySlot = new InventorySlot();
        inventorySlot.add(item);

        InventoryUI inventoryUI = spy(new InventoryUI());
        Table equipSlotTable = inventoryUI.getEquipSlotTable();
        inventoryUI.onNotify(inventorySlot, InventorySlotObserver.SlotEvent.ADDED_ITEM);

        verify(inventoryUI, times(1)).notify(String.valueOf(playerStat + item.getItemUseTypeValue()), inventoryEvent);
        assertEquals(playerStat + item.getItemUseTypeValue(), ProfileManager.getInstance().getProperty(profileSave, Integer.class));
        verify(inventoryUI, times(1)).isSetEquipped(InventoryUI.getInventory(equipSlotTable));
    }

    @ParameterizedTest
    @MethodSource("itemEventData")
    public void remove_item_event(InventoryItem item, int playerStat, String profileSave, InventoryObserver.InventoryEvent inventoryEvent) {
        InventorySlot inventorySlot = new InventorySlot();
        inventorySlot.add(item);

        InventoryUI inventoryUI = spy(new InventoryUI());
        inventoryUI.onNotify(inventorySlot, InventorySlotObserver.SlotEvent.REMOVED_ITEM);

        verify(inventoryUI, times(1)).notify(String.valueOf(playerStat - item.getItemUseTypeValue()), inventoryEvent);
        assertEquals(playerStat - item.getItemUseTypeValue(), ProfileManager.getInstance().getProperty(profileSave, Integer.class));
    }

    private static Stream<Arguments> itemEventData() {
        return Stream.of(
                Arguments.of(getInventoryItem(8), PLAYER_AP, "currentPlayerAP", InventoryObserver.InventoryEvent.UPDATED_AP),
                Arguments.of(getInventoryItem(128), PLAYER_DP, "currentPlayerDP", InventoryObserver.InventoryEvent.UPDATED_DP)
        );
    }

    private static InventoryItem getInventoryItem(int itemUseType) {
        InventoryItem item = new InventoryItem();
        item.setItemAttributes(2);
        item.setItemUseType(itemUseType);
        item.setItemUseTypeValue(10);
        item.setItemTypeID(InventoryItem.ItemTypeID.WEAPON01);
        item.setItemSetID(InventoryItem.ItemSetID.PEASANT_SET);
        return item;
    }

    @ParameterizedTest
    @MethodSource("itemSetData")
    public void should_return_if_set_equipped(Array<InventoryItemLocation> equipSlots, boolean expectedResult) {
        InventoryUI inventoryUI = new InventoryUI();
        boolean result = inventoryUI.isSetEquipped(equipSlots);

        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> itemSetData() {
        return Stream.of(
                Arguments.of(getInventoryItemLocations(), true),
                Arguments.of(getInventoryItemLocationsWithoutHelmet(), false),
                Arguments.of(getInventoryItemLocationsWithDifferentHelmet(), false)
        );
    }

    private static Array<InventoryItemLocation> getInventoryItemLocations() {
        InventoryItemLocation itemHelmet = new InventoryItemLocation(1, "HELMET05", 1, "Player_Inventory");
        InventoryItemLocation itemWeapon = new InventoryItemLocation(2, "WEAPON01", 1, "Player_Inventory");
        InventoryItemLocation itemArmor = new InventoryItemLocation(3, "ARMOR04", 1, "Player_Inventory");
        InventoryItemLocation itemShield = new InventoryItemLocation(4, "SHIELD02", 1, "Player_Inventory");
        InventoryItemLocation itemBoots = new InventoryItemLocation(5, "BOOTS03", 1, "Player_Inventory");

        Array<InventoryItemLocation> equipSlots = new Array<>();
        equipSlots.add(itemHelmet);
        equipSlots.add(itemWeapon);
        equipSlots.add(itemArmor);
        equipSlots.add(itemShield);
        equipSlots.add(itemBoots);
        return equipSlots;
    }

    private static Array<InventoryItemLocation> getInventoryItemLocationsWithoutHelmet() {
        Array<InventoryItemLocation> equipSlots = getInventoryItemLocations();
        equipSlots.removeIndex(0);
        return equipSlots;
    }

    private static Array<InventoryItemLocation> getInventoryItemLocationsWithDifferentHelmet() {
        Array<InventoryItemLocation> equipSlots = getInventoryItemLocations();
        InventoryItemLocation itemHelmet = new InventoryItemLocation(1, "HELMET03", 1, "Player_Inventory");
        equipSlots.add(itemHelmet);
        return equipSlots;
    }

    @Test
    public void should_calculate_bonus_from_set() {
        ProfileManager.getInstance().setProperty("currentPlayerAP", 10);
        ProfileManager.getInstance().setProperty("currentPlayerDP", 25);
        InventoryUI.setBonusFromSet(getInventoryItem(8));

        HashMap<String, Integer> bonusMap = calculateBonus("bonusSet");

        assertEquals(1, bonusMap.get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name()));
        assertEquals(2, bonusMap.get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name()));
    }
}
