package com.gdx.game.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxGame;
import com.gdx.game.GdxRunner;
import com.gdx.game.battle.BattleHUD;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.dialog.ConversationGraph;
import com.gdx.game.dialog.ConversationGraphObserver;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.inventory.InventoryUI;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemFactory;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.profile.ProfileObserver;
import com.gdx.game.screen.BattleScreen;
import com.gdx.game.status.StatsUpUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(GdxRunner.class)
class BonusCalculationIntegrationTest {

    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;

    private GdxGame gdxGame;
    private ResourceManager resourceManager;
    private ProfileManager profileManager;
    private static final String CHARACTER_CLASS = "WARRIOR";
    private static final int INIT_CHARACTER_AP = 6;
    private static final int INIT_CHARACTER_DP = 5;
    private static final int BONUS_POINT_LEVEL = 5;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        gdxGame = mock(GdxGame.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        profileManager = ProfileManager.getInstance();
        profileManager.setProperty("characterClass", CHARACTER_CLASS);
        profileManager.setProperty("currentPlayerBonusClassAP", INIT_CHARACTER_AP);
        profileManager.setProperty("currentPlayerBonusClassDP", INIT_CHARACTER_DP);
        profileManager.setProperty("currentPlayerCharacterSPDP", 2);
        profileManager.setProperty("currentPlayerCharacterAP", INIT_CHARACTER_AP);
        profileManager.setProperty("currentPlayerCharacterDP", INIT_CHARACTER_DP);
        profileManager.setProperty("currentPlayerCharacterSPDP", 2);
        profileManager.setProperty("currentPlayerSPDP", 2);
        resourceManager = new ResourceManager();
        when(gdxGame.getBatch()).thenReturn(new SpriteBatch());
    }

    @AfterEach
    void end() {
        mockShapeRenderer.close();
        mockSpriteBatch.close();
        gdxGame.dispose();
    }

    @Test
    void equip_set_levelup_and_change_class_with_quest_and_levelup_battle() throws NoSuchFieldException, IllegalAccessException {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity townFolk2 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK2);
        EntityConfig entityConfig = new EntityConfig(townFolk2.getEntityConfig());
        profileManager.setProperty("TOWN_FOLK2", entityConfig);
        profileManager.setIsNewProfile(true);

        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);

        // Initialize the game
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        // Simulate set equipment
        InventoryUI inventoryUI = hud.getInventoryUI();
        equipItem(inventoryUI, 1, InventoryItem.ItemTypeID.HELMET05);
        equipItem(inventoryUI, 2, InventoryItem.ItemTypeID.WEAPON01);
        equipItem(inventoryUI, 3, InventoryItem.ItemTypeID.ARMOR04);
        equipItem(inventoryUI, 4, InventoryItem.ItemTypeID.SHIELD02);
        equipItem(inventoryUI, 6, InventoryItem.ItemTypeID.BOOTS03);
        Array<EntityBonus> bonusSet = new Array<>();
        bonusSet.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.1"));
        bonusSet.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1"));
        assertEquals(bonusSet.get(0).getValue(), ((EntityBonus) profileManager.getProperty("bonusSet", Array.class).get(0)).getValue());
        assertEquals(bonusSet.get(1).getValue(), ((EntityBonus) profileManager.getProperty("bonusSet", Array.class).get(1)).getValue());
        assertEquals(16, profileManager.getProperty("currentPlayerAP", Integer.class));
        assertEquals(45, profileManager.getProperty("currentPlayerDP", Integer.class));

        // Simulate quest acceptance and completion
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ACCEPT_QUEST);
        Entity baby = mapManager.getCurrentMapQuestEntities().get(0);
        mapManager.setCurrentSelectedMapEntity(baby);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY);
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.RETURN_QUEST);
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.SAVING_PROFILE);
        assertEquals("conversations/quest_finished.json", profileManager.getProperty(EntityFactory.EntityName.TOWN_FOLK2.name(), EntityConfig.class).getConversationConfigPath());
        assertEquals(3, profileManager.getProperty("currentPlayerLevel", Integer.class));

        // Simulate level up
        int nbrLevelUp = hud.getStatusUI().getNbrLevelUp();
        assertEquals(2, nbrLevelUp);
        StatsUpUI statsUpUI = (StatsUpUI) hud.getStage().getActors().get(9);
        int bonusPoints = Integer.parseInt(statsUpUI.getChildren().get(2).toString().split(": ")[1]);
        assertEquals(nbrLevelUp * BONUS_POINT_LEVEL, bonusPoints);
        ImageButton atkPlus = (ImageButton) (statsUpUI.getChildren().get(6));
        ImageButton defPlus = (ImageButton) (statsUpUI.getChildren().get(12));
        for (int i = 0; i <= bonusPoints/2 ; i++) {
            pressButton(statsUpUI, atkPlus);
            pressButton(statsUpUI, defPlus);
        }
        int firstLvlUpAP = INIT_CHARACTER_AP + bonusPoints/2;
        int firstLvlUpDP = INIT_CHARACTER_DP + bonusPoints/2;
        TextButton saveButton = (TextButton) (statsUpUI.getChildren().get(21));
        pressButton(statsUpUI, saveButton);
        assertEquals(firstLvlUpAP, profileManager.getProperty("currentPlayerCharacterAP", Integer.class));
        assertEquals(firstLvlUpDP, profileManager.getProperty("currentPlayerCharacterDP", Integer.class));
        assertEquals("KNIGHT", profileManager.getProperty("characterClass", String.class));
        Array<EntityBonus> bonusClass = new Array<>();
        bonusClass.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.1"));
        bonusClass.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1"));
        assertEquals(bonusClass.get(0).getValue(), ((EntityBonus) profileManager.getProperty("bonusClass", Array.class).get(0)).getValue());
        assertEquals(bonusClass.get(1).getValue(), ((EntityBonus) profileManager.getProperty("bonusClass", Array.class).get(1)).getValue());
        assertEquals(12, profileManager.getProperty("currentPlayerBonusClassAP", Integer.class));
        assertEquals(11, profileManager.getProperty("currentPlayerBonusClassDP", Integer.class));

        // Check displayed value in inventory
        assertEquals(24, hud.getInventoryUI().getAPVal());
        assertEquals(56, hud.getInventoryUI().getDPVal());

        // Simulate battle and level up
        mapManager.loadMap(MapFactory.MapType.TOPPLE_ROAD_1);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE2);
        Entity rabite2 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE2);
        BattleScreen battleScreen = new BattleScreen(gdxGame, hud, mapManager, resourceManager);
        Field field = BattleScreen.class.getDeclaredField("battleHUD");
        field.setAccessible(true);
        BattleHUD battleHud = (BattleHUD) field.get(battleScreen);
        battleHud.onNotify(rabite2, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
        hud.onNotify(rabite2, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
        assertEquals(4, profileManager.getProperty("currentPlayerLevel", Integer.class));

        // Simulate level up again
        int nbrLevelUp2 = battleHud.getBattleStatusUI().getNbrLevelUp();
        assertEquals(1, nbrLevelUp2);
        StatsUpUI statsUpUI2 = (StatsUpUI) battleHud.getBattleHUDStage().getActors().get(9);
        int bonusPoints2 = Integer.parseInt(statsUpUI2.getChildren().get(2).toString().split(": ")[1]);
        assertEquals(nbrLevelUp2 * BONUS_POINT_LEVEL, bonusPoints2);
        ImageButton atkPlus2 = (ImageButton) (statsUpUI2.getChildren().get(6));
        for (int i = 0; i <= bonusPoints2 ; i++) {
            pressButton(statsUpUI2, atkPlus2);
        }
        int secondLvlUpAP = firstLvlUpAP + bonusPoints2;
        TextButton saveButton2 = (TextButton) (statsUpUI2.getChildren().get(21));
        pressButton(statsUpUI2, saveButton2);
        assertEquals(secondLvlUpAP, profileManager.getProperty("currentPlayerCharacterAP", Integer.class));
        assertEquals(firstLvlUpDP, profileManager.getProperty("currentPlayerCharacterDP", Integer.class));
        battleScreen.onNotify(null, BattleObserver.BattleEvent.RESUME_OVER);

        // Check displayed value in inventory
        assertEquals(29, hud.getInventoryUI().getAPVal());
        assertEquals(56, hud.getInventoryUI().getDPVal());
    }

    @Test
    void equip_set_levelup_and_change_class_with_battle_and_levelup_quest() throws NoSuchFieldException, IllegalAccessException {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity townFolk2 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK2);
        EntityConfig entityConfig = new EntityConfig(townFolk2.getEntityConfig());
        profileManager.setProperty("TOWN_FOLK2", entityConfig);
        profileManager.setIsNewProfile(true);

        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        mapManager.loadMap(MapFactory.MapType.TOPPLE_ROAD_1);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);

        // Initialize the game
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        // Simulate set equipment
        InventoryUI inventoryUI = hud.getInventoryUI();
        equipItem(inventoryUI, 1, InventoryItem.ItemTypeID.HELMET05);
        equipItem(inventoryUI, 2, InventoryItem.ItemTypeID.WEAPON01);
        equipItem(inventoryUI, 3, InventoryItem.ItemTypeID.ARMOR04);
        equipItem(inventoryUI, 4, InventoryItem.ItemTypeID.SHIELD02);
        equipItem(inventoryUI, 6, InventoryItem.ItemTypeID.BOOTS03);
        Array<EntityBonus> bonusSet = new Array<>();
        bonusSet.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.1"));
        bonusSet.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1"));
        assertEquals(bonusSet.get(0).getValue(), ((EntityBonus) profileManager.getProperty("bonusSet", Array.class).get(0)).getValue());
        assertEquals(bonusSet.get(1).getValue(), ((EntityBonus) profileManager.getProperty("bonusSet", Array.class).get(1)).getValue());
        assertEquals(16, profileManager.getProperty("currentPlayerAP", Integer.class));
        assertEquals(45, profileManager.getProperty("currentPlayerDP", Integer.class));
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.SAVING_PROFILE);

        // Simulate battle and level up
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE2);
        Entity rabite2 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE2);
        BattleScreen battleScreen = new BattleScreen(gdxGame, hud, mapManager, resourceManager);
        Field field = BattleScreen.class.getDeclaredField("battleHUD");
        field.setAccessible(true);
        BattleHUD battleHud = (BattleHUD) field.get(battleScreen);
        battleHud.onNotify(rabite2, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
        hud.onNotify(rabite2, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
        assertEquals(3, profileManager.getProperty("currentPlayerLevel", Integer.class));

        // Simulate level up
        int nbrLevelUp = battleHud.getBattleStatusUI().getNbrLevelUp();
        assertEquals(2, nbrLevelUp);
        StatsUpUI statsUpUI = (StatsUpUI) battleHud.getBattleHUDStage().getActors().get(9);
        int bonusPoints = Integer.parseInt(statsUpUI.getChildren().get(2).toString().split(": ")[1]);
        assertEquals(nbrLevelUp * BONUS_POINT_LEVEL, bonusPoints);
        ImageButton atkPlus = (ImageButton) (statsUpUI.getChildren().get(6));
        ImageButton defPlus = (ImageButton) (statsUpUI.getChildren().get(12));
        for (int i = 0; i <= bonusPoints/2 ; i++) {
            pressButton(statsUpUI, atkPlus);
            pressButton(statsUpUI, defPlus);
        }
        int firstLvlUpAP = INIT_CHARACTER_AP + bonusPoints/2;
        int firstLvlUpDP = INIT_CHARACTER_DP + bonusPoints/2;
        TextButton saveButton = (TextButton) (statsUpUI.getChildren().get(21));
        pressButton(statsUpUI, saveButton);
        assertEquals(firstLvlUpAP, profileManager.getProperty("currentPlayerCharacterAP", Integer.class));
        assertEquals(firstLvlUpDP, profileManager.getProperty("currentPlayerCharacterDP", Integer.class));
        assertEquals("KNIGHT", profileManager.getProperty("characterClass", String.class));
        Array<EntityBonus> bonusClass = new Array<>();
        bonusClass.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.1"));
        bonusClass.add(new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1"));
        assertEquals(bonusClass.get(0).getValue(), ((EntityBonus) profileManager.getProperty("bonusClass", Array.class).get(0)).getValue());
        assertEquals(bonusClass.get(1).getValue(), ((EntityBonus) profileManager.getProperty("bonusClass", Array.class).get(1)).getValue());
        battleScreen.onNotify(null, BattleObserver.BattleEvent.RESUME_OVER);

        // Check displayed value in inventory
        assertEquals(24, hud.getInventoryUI().getAPVal());
        assertEquals(56, hud.getInventoryUI().getDPVal());

        // Simulate quest acceptance and completion
        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ACCEPT_QUEST);
        Entity baby = mapManager.getCurrentMapQuestEntities().get(0);
        mapManager.setCurrentSelectedMapEntity(baby);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY);
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.RETURN_QUEST);
        hud.onNotify(profileManager, ProfileObserver.ProfileEvent.SAVING_PROFILE);
        assertEquals("conversations/quest_finished.json", profileManager.getProperty(EntityFactory.EntityName.TOWN_FOLK2.name(), EntityConfig.class).getConversationConfigPath());
        assertEquals(4, profileManager.getProperty("currentPlayerLevel", Integer.class));

        // Simulate level up again
        int nbrLevelUp2 = hud.getStatusUI().getNbrLevelUp();
        assertEquals(1, nbrLevelUp2);
        StatsUpUI statsUpUI2 = (StatsUpUI) hud.getStage().getActors().get(9);
        int bonusPoints2 = Integer.parseInt(statsUpUI2.getChildren().get(2).toString().split(": ")[1]);
        assertEquals(nbrLevelUp2 * BONUS_POINT_LEVEL, bonusPoints2);
        ImageButton atkPlus2 = (ImageButton) (statsUpUI2.getChildren().get(6));
        for (int i = 0; i <= bonusPoints2 ; i++) {
            pressButton(statsUpUI2, atkPlus2);
        }
        int secondLvlUpAP = firstLvlUpAP + bonusPoints2;
        TextButton saveButton2 = (TextButton) (statsUpUI2.getChildren().get(21));
        pressButton(statsUpUI, saveButton2);
        assertEquals(secondLvlUpAP, profileManager.getProperty("currentPlayerCharacterAP", Integer.class));
        assertEquals(firstLvlUpDP, profileManager.getProperty("currentPlayerCharacterDP", Integer.class));
        assertEquals(17, profileManager.getProperty("currentPlayerBonusClassAP", Integer.class));
        assertEquals(11, profileManager.getProperty("currentPlayerBonusClassDP", Integer.class));

        // Check displayed value in inventory
        assertEquals(29, hud.getInventoryUI().getAPVal());
        assertEquals(56, hud.getInventoryUI().getDPVal());
    }

    private void pressButton(StatsUpUI statsUpUI, Button button) {
        InputEvent touchDown = inputEvent(statsUpUI.getStage(), InputEvent.Type.touchDown, button);
        button.fire(touchDown);
        InputEvent touchUp = inputEvent(statsUpUI.getStage(), InputEvent.Type.touchUp, button);
        button.fire(touchUp);
    }

    private InputEvent inputEvent(Stage stage, InputEvent.Type type, Button button) {
        InputEvent event = new InputEvent();
        event.setType(type);
        event.setStage(stage);
        event.setTarget(button);
        return event;
    }

    private static void equipItem(InventoryUI inventoryUI, int slotTableIndex, InventoryItem.ItemTypeID itemTypeID) {
        InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeID);
        InventorySlot inventorySlot = (InventorySlot) inventoryUI.getEquipSlotTable().getCells().get(slotTableIndex).getActor();
        inventorySlot.add(item);
    }
}
