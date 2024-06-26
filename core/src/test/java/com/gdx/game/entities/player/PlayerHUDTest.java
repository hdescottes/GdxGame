package com.gdx.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.GdxRunner;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.dialog.ConversationGraph;
import com.gdx.game.dialog.ConversationGraphObserver;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.characterClass.ClassObserver;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemFactory;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.inventory.store.StoreInventoryObserver;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.profile.ProfileObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.gdx.game.component.Component.MESSAGE_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class PlayerHUDTest {

    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;

    private final ProfileManager profileManager = ProfileManager.getInstance();
    private static final int HP_MAX_VALUE = 100;
    private static final int HP_VALUE = 80;
    private static final int MP_MAX_VALUE = 10;
    private static final int MP_VALUE = 0;
    private static final int XP_MAX_VALUE = 20;
    private static final int XP_VALUE = 10;
    private static final int GOLD_VALUE = 100;
    private static final int LEVEL_VALUE = 2;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        profileManager.setProperty("characterClass", "WARRIOR");
        profileManager.setProperty("currentPlayerCharacterAP", 15);
        profileManager.setProperty("currentPlayerCharacterDP", 15);
        profileManager.setProperty("currentPlayerCharacterSPDP", 10);
        profileManager.setProperty("currentPlayerXPMax", XP_MAX_VALUE);
        profileManager.setProperty("currentPlayerXP", XP_VALUE);
        profileManager.setProperty("currentPlayerHPMax", HP_MAX_VALUE);
        profileManager.setProperty("currentPlayerHP", HP_VALUE);
        profileManager.setProperty("currentPlayerMPMax", MP_MAX_VALUE);
        profileManager.setProperty("currentPlayerMP", MP_VALUE);
        profileManager.setProperty("currentPlayerLevel", LEVEL_VALUE);
        profileManager.setProperty("currentPlayerGP", GOLD_VALUE);
        new ResourceManager();
    }

    @AfterEach
    void end() {
        mockShapeRenderer.close();
        mockSpriteBatch.close();
    }

    @ParameterizedTest
    @MethodSource("profileLoad")
    void profile_load(ProfileObserver.ProfileEvent event, boolean isNewProfile, Map<String, Integer> stats) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());
        profileManager.setIsNewProfile(isNewProfile);

        hud.onNotify(profileManager, event);

        assertEquals(stats.get("goldValue"), hud.getStatusUI().getGoldValue());
        assertEquals(stats.get("hpValue"), hud.getStatusUI().getHPValue());
        assertEquals(stats.get("hpMaxValue"), hud.getStatusUI().getHPValueMax());
        assertEquals(stats.get("mpValue"), hud.getStatusUI().getMPValue());
        assertEquals(stats.get("mpMaxValue"), hud.getStatusUI().getMPValueMax());
        assertEquals(stats.get("xpValue"), hud.getStatusUI().getXPValue());
        assertEquals(stats.get("xpMaxValue"), hud.getStatusUI().getXPValueMax());
        assertEquals(stats.get("levelValue"), hud.getStatusUI().getLevelValue());
    }

    private static Stream<Arguments> profileLoad() {
        Map<String, Integer> newProfileStats = new HashMap<>();
        newProfileStats.put("goldValue", 20);
        newProfileStats.put("hpValue", 50);
        newProfileStats.put("hpMaxValue", 50);
        newProfileStats.put("xpValue", 0);
        newProfileStats.put("xpMaxValue", 200);
        newProfileStats.put("mpValue", 50);
        newProfileStats.put("mpMaxValue", 50);
        newProfileStats.put("levelValue", 1);
        Map<String, Integer> profileStats = new HashMap<>();
        profileStats.put("goldValue", GOLD_VALUE);
        profileStats.put("hpValue", HP_VALUE);
        profileStats.put("hpMaxValue", HP_MAX_VALUE);
        profileStats.put("xpValue", XP_VALUE);
        profileStats.put("xpMaxValue", XP_MAX_VALUE);
        profileStats.put("mpValue", MP_VALUE);
        profileStats.put("mpMaxValue", MP_MAX_VALUE);
        profileStats.put("levelValue", LEVEL_VALUE);
        return Stream.of(
                Arguments.of(ProfileObserver.ProfileEvent.PROFILE_LOADED, true, newProfileStats),
                Arguments.of(ProfileObserver.ProfileEvent.PROFILE_LOADED, false, profileStats)
        );
    }

    @ParameterizedTest
    @MethodSource("profileStates")
    void profile_states(ProfileObserver.ProfileEvent event, Map<String, Integer> stats) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());
        profileManager.setIsNewProfile(false);
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        hud.onNotify(profileManager, event);

        assertEquals(stats.get("goldValue"), profileManager.getProperty("currentPlayerGP", Integer.class));
        assertEquals(stats.get("hpValue"), profileManager.getProperty("currentPlayerHP", Integer.class));
        assertEquals(stats.get("hpMaxValue"), profileManager.getProperty("currentPlayerHPMax", Integer.class));
        assertEquals(stats.get("mpValue"), profileManager.getProperty("currentPlayerMP", Integer.class));
        assertEquals(stats.get("mpMaxValue"), profileManager.getProperty("currentPlayerMPMax", Integer.class));
        assertEquals(stats.get("xpValue"), profileManager.getProperty("currentPlayerXP", Integer.class));
        assertEquals(stats.get("xpMaxValue"), profileManager.getProperty("currentPlayerXPMax", Integer.class));
        assertEquals(stats.get("levelValue"), profileManager.getProperty("currentPlayerLevel", Integer.class));
    }

    private static Stream<Arguments> profileStates() {
        Map<String, Integer> profileStats = new HashMap<>();
        profileStats.put("goldValue", GOLD_VALUE);
        profileStats.put("hpValue", HP_VALUE);
        profileStats.put("hpMaxValue", HP_MAX_VALUE);
        profileStats.put("xpValue", XP_VALUE);
        profileStats.put("xpMaxValue", XP_MAX_VALUE);
        profileStats.put("mpValue", MP_VALUE);
        profileStats.put("mpMaxValue", MP_MAX_VALUE);
        profileStats.put("levelValue", LEVEL_VALUE);
        Map<String, Integer> clearProfileStats = new HashMap<>();
        clearProfileStats.put("goldValue", 0);
        clearProfileStats.put("hpValue", 0);
        clearProfileStats.put("hpMaxValue", 0);
        clearProfileStats.put("xpValue", 0);
        clearProfileStats.put("xpMaxValue", 0);
        clearProfileStats.put("mpValue", 0);
        clearProfileStats.put("mpMaxValue", 0);
        clearProfileStats.put("levelValue", 0);
        return Stream.of(
                Arguments.of(ProfileObserver.ProfileEvent.SAVING_PROFILE, profileStats),
                Arguments.of(ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE, clearProfileStats)
        );
    }

    @Test
    void load_dialog() {
        Json json = new Json();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity blacksmith = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);

        hud.onNotify(json.toJson(blacksmith.getEntityConfig()), ComponentObserver.ComponentEvent.LOAD_CONVERSATION);

        assertEquals(blacksmith.getEntityConfig().getEntityID(), hud.getConversationUI().getCurrentEntityID());
    }

    @ParameterizedTest
    @MethodSource("dialogVisibility")
    void dialog_visibility(ComponentObserver.ComponentEvent event, boolean isVisible) {
        Json json = new Json();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity blacksmith = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        hud.onNotify(json.toJson(blacksmith.getEntityConfig()), ComponentObserver.ComponentEvent.LOAD_CONVERSATION);

        hud.onNotify(json.toJson(blacksmith.getEntityConfig()), event);

        assertEquals(isVisible, hud.getConversationUI().isVisible());
    }

    private static Stream<Arguments> dialogVisibility() {
        return Stream.of(
                Arguments.of(ComponentObserver.ComponentEvent.SHOW_CONVERSATION, true),
                Arguments.of(ComponentObserver.ComponentEvent.HIDE_CONVERSATION, false)
        );
    }

    @Test
    void load_store_inventory() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity blacksmith = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        mapManager.setCurrentSelectedMapEntity(blacksmith);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);

        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.LOAD_STORE_INVENTORY);

        assertFalse(hud.getConversationUI().isVisible());
        assertTrue(hud.getStoreInventoryUI().isVisible());
    }

    @Test
    void update_total_gold() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        int totalGold = 10;

        hud.onNotify(String.valueOf(totalGold), StoreInventoryObserver.StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);

        assertEquals(totalGold, hud.getStatusUI().getGoldValue());
    }

    @Test
    void check_upgrade_class() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);

        hud.onNotify("", ClassObserver.ClassEvent.CHECK_UPGRADE_TREE_CLASS);

        assertEquals("KNIGHT", ProfileManager.getInstance().getProperty("characterClass", String.class));
        assertFalse(hud.getStatusUI().isVisible());
    }

    @Test
    void opponent_defeated() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        hud.onNotify(enemy, BattleObserver.BattleEvent.OPPONENT_DEFEATED);

        assertEquals(GOLD_VALUE + 5, hud.getStatusUI().getGoldValue());
        assertEquals(XP_VALUE + 10, hud.getStatusUI().getXPValue());
    }

    @Test
    void player_hit_damage() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());

        hud.onNotify(enemy, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);

        assertEquals(HP_VALUE, hud.getStatusUI().getHPValue());
    }

    @ParameterizedTest
    @MethodSource("consumeItems")
    void consume_items(InventoryItem.ItemTypeID itemTypeId, int value, String property) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());
        InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeId);
        String itemInfo = item.getItemUseType() + MESSAGE_TOKEN + item.getItemUseTypeValue();
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        hud.onNotify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);

        if (InventoryItem.doesRestoreHP(item.getItemUseType())) {
            assertEquals(value, hud.getStatusUI().getHPValue());
        } else {
            assertEquals(value, hud.getStatusUI().getMPValue());
        }
        assertEquals(value, ProfileManager.getInstance().getProperty(property, Integer.class));
    }

    private static Stream<Arguments> consumeItems() {
        return Stream.of(
                Arguments.of(InventoryItem.ItemTypeID.SCROLL01, 90, "currentPlayerHP"),
                Arguments.of(InventoryItem.ItemTypeID.POTIONS01, 10, "currentPlayerMP")
        );
    }
}
