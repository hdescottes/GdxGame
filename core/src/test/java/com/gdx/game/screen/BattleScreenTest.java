package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.GdxGame;
import com.gdx.game.GdxRunner;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.component.Component;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerGraphicsComponent;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class BattleScreenTest {

    private MockedConstruction<PlayerGraphicsComponent> mockPlayerGraphics;
    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;
    private MockedConstruction<Stage> mockStage;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockPlayerGraphics = mockConstruction(PlayerGraphicsComponent.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        mockStage = mockConstruction(Stage.class);
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.setProperty("characterClass", "WARRIOR");
        profileManager.setProperty("currentPlayerBonusClassAP", 15);
        profileManager.setProperty("currentPlayerBonusClassDP", 15);
        profileManager.setProperty("currentPlayerCharacterAP", 15);
        profileManager.setProperty("currentPlayerCharacterDP", 15);
        profileManager.setProperty("currentPlayerCharacterSPDP", 10);
        profileManager.setProperty("currentPlayerAP", 15);
        profileManager.setProperty("currentPlayerDP", 15);
        profileManager.setProperty("currentPlayerSPDP", 10);
        profileManager.setProperty("currentPlayerXPMax", 200);
        profileManager.setProperty("currentPlayerXP", 0);
        profileManager.setProperty("currentPlayerHPMax", 50);
        profileManager.setProperty("currentPlayerHP", 40);
        profileManager.setProperty("currentPlayerMPMax", 50);
        profileManager.setProperty("currentPlayerMP", 45);
        profileManager.setProperty("currentPlayerLevel", 1);
        profileManager.setProperty("playerQuests", new Array<>());
        profileManager.setProperty("playerInventory", new Array<>());
        profileManager.setProperty("playerEquipInventory", new Array<>());
    }

    @AfterEach
    void end() {
        mockPlayerGraphics.close();
        mockShapeRenderer.close();
        mockSpriteBatch.close();
        mockStage.close();
    }

    @ParameterizedTest
    @MethodSource("fightOver")
    void fight_over(BattleObserver.BattleEvent event) {
        Json json = new Json();
        GdxGame gdxGame = mock(GdxGame.class);
        ResourceManager resourceManager = new ResourceManager();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        Vector2 entityPosition = new Vector2(10, 10);
        Entity entity = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE);
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(entityPosition));
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        mapManager.loadMap(MapFactory.MapType.TOPPLE_ROAD_1);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        BattleScreen battleScreen = new BattleScreen(gdxGame, hud, mapManager, resourceManager);
        int hpValue = 10;
        battleScreen.getBattleHUD().getBattleStatusUI().setHPValue(hpValue);

        battleScreen.onNotify(player, event);

        assertEquals(hpValue, hud.getStatusUI().getHPValue());
        assertNull(player.getEntityEncounteredType());
        assertEquals(1, mapManager.getCurrentMapEntities().size);
    }

    private static Stream<Arguments> fightOver() {
        return Stream.of(
                Arguments.of(BattleObserver.BattleEvent.RESUME_OVER),
                Arguments.of(BattleObserver.BattleEvent.PLAYER_RUNNING)
        );
    }

    @Test
    void game_over() {
        GdxGame gdxGame = mock(GdxGame.class);
        ResourceManager resourceManager = new ResourceManager();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        mapManager.loadMap(MapFactory.MapType.TOPPLE_ROAD_1);
        Camera camera = new OrthographicCamera();
        PlayerHUD hud = new PlayerHUD(camera, player, mapManager);
        BattleScreen battleScreen = new BattleScreen(gdxGame, hud, mapManager, resourceManager);
        GameScreen.setGameState(GameScreen.GameState.GAME_OVER);

        battleScreen.onNotify(player, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);

        assertFalse(battleScreen.getBattleHUD().getDmgOpponentValLabel().isVisible());
        assertFalse(battleScreen.getBattleHUD().getDmgPlayerValLabel().isVisible());
        assertFalse(battleScreen.getBattleHUD().getBattleUI().isVisible());
        assertFalse(battleScreen.getBattleHUD().getBattleStatusUI().isVisible());
    }
}
