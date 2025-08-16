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
import com.gdx.game.GdxRunner;
import com.gdx.game.dialog.ConversationGraph;
import com.gdx.game.dialog.ConversationGraphObserver;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.profile.ProfileObserver;
import com.gdx.game.status.StatsUpUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
class BonusCalculationTest {

    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;

    private final ProfileManager profileManager = ProfileManager.getInstance();
    private static final String CHARACTER_CLASS = "WARRIOR";

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        profileManager.setProperty("characterClass", CHARACTER_CLASS);
        profileManager.setProperty("currentPlayerBonusClassAP", 6);
        profileManager.setProperty("currentPlayerBonusClassDP", 5);
        profileManager.setProperty("currentPlayerCharacterSPDP", 2);
        profileManager.setProperty("currentPlayerCharacterAP", 6);
        profileManager.setProperty("currentPlayerCharacterDP", 5);
        new ResourceManager();
    }

    @AfterEach
    void end() {
        mockShapeRenderer.close();
        mockSpriteBatch.close();
    }

    @Test
    void levelup_and_change_class_with_quest_and_levelup_battle() {
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

        // Simulate quest acceptance and completion
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ACCEPT_QUEST);
        Entity baby = mapManager.getCurrentMapQuestEntities().get(0);
        mapManager.setCurrentSelectedMapEntity(baby);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY);
        mapManager.setCurrentSelectedMapEntity(townFolk2);
        hud.onNotify(new ConversationGraph(), ConversationGraphObserver.ConversationCommandEvent.RETURN_QUEST);
        assertEquals("conversations/quest_finished.json", profileManager.getProperty(EntityFactory.EntityName.TOWN_FOLK2.name(), EntityConfig.class).getConversationConfigPath());

        // Simulate level up
        StatsUpUI statsUpUI = (StatsUpUI) hud.getStage().getActors().get(9);
        int bonusPoints = Integer.parseInt(statsUpUI.getChildren().get(2).toString().split(": ")[1]);
        assertEquals(10, bonusPoints);
        ImageButton atkPlus = (ImageButton) (statsUpUI.getChildren().get(6));
        for (int i = 0; i <= bonusPoints/2 ; i++) {
            pressButton(statsUpUI, atkPlus);
        }
        ImageButton defPlus = (ImageButton) (statsUpUI.getChildren().get(12));
        for (int i = 0; i <= bonusPoints/2 ; i++) {
            pressButton(statsUpUI, defPlus);
        }
        TextButton saveButton = (TextButton) (statsUpUI.getChildren().get(21));
        pressButton(statsUpUI, saveButton);
        //TODO: Check if the stats are updated correctly
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
}
