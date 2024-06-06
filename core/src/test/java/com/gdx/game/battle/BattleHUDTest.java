package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.inventory.InventoryItem;
import com.gdx.game.inventory.InventoryItemFactory;
import com.gdx.game.inventory.InventoryItemLocation;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.manager.ResourceManager;
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

import static com.gdx.game.component.Component.MESSAGE_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(GdxRunner.class)
public class BattleHUDTest {

    private MockedConstruction<ShapeRenderer> mockShapeRenderer;
    private MockedConstruction<SpriteBatch> mockSpriteBatch;

    private final ProfileManager profileManager = ProfileManager.getInstance();

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        profileManager.setProperty("currentPlayerAP", 5);
        profileManager.setProperty("currentPlayerDP", 5);
        profileManager.setProperty("currentPlayerSPDP", 10);
        profileManager.setProperty("currentPlayerXPMax", 0);
        profileManager.setProperty("currentPlayerXP", 0);
        profileManager.setProperty("currentPlayerHPMax", 50);
        profileManager.setProperty("currentPlayerHP", 20);
        profileManager.setProperty("currentPlayerMPMax", 30);
        profileManager.setProperty("currentPlayerMP", 10);
        profileManager.setProperty("currentPlayerLevel", 1);
        profileManager.setProperty("playerInventory", new Array<InventoryItemLocation>());
        profileManager.setProperty("currentPlayerCharacterAP", 5);
        profileManager.setProperty("currentPlayerCharacterDP", 5);
        profileManager.setProperty("currentPlayerCharacterSPDP", 10);
        new ResourceManager();
    }

    @AfterEach
    void end() {
        mockShapeRenderer.close();
        mockSpriteBatch.close();
    }

    @ParameterizedTest
    @MethodSource("playerStartSteps")
    void player_start_battle_step(BattleObserver.BattleEvent event, boolean isVisible, boolean isTouchable) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState  =new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        hud.onNotify(player, event);

        assertEquals(hud.getBattleUI().isVisible(), isVisible);
        assertEquals(hud.getBattleUI().isTouchable(), isTouchable);
    }

    private static Stream<Arguments> playerStartSteps() {
        return Stream.of(
                Arguments.of(BattleObserver.BattleEvent.PLAYER_TURN_START, true, true),
                Arguments.of(BattleObserver.BattleEvent.PLAYER_PHASE_START, false, false)
        );
    }

    @ParameterizedTest
    @MethodSource("addEntitySteps")
    void add_entity_battle_step(BattleObserver.BattleEvent event, int actorNumber, Entity.AnimationType animationType) {
        EntityFactory.EntityName entityEncountered = EntityFactory.EntityName.RABITE;
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(entityEncountered);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        AnimatedImage entityImage = (AnimatedImage) hud.getBattleHUDStage().getActors().get(actorNumber);

        hud.onNotify(player, event);

        if (BattleObserver.BattleEvent.PLAYER_ADDED.equals(event)) {
            assertEquals(entityImage.getEntity(), player);
        } else {
            assertEquals(entityImage.getEntity().getEntityEncounteredType(), entityEncountered);
        }
        assertEquals(entityImage.getCurrentAnimationType(), animationType);
    }

    private static Stream<Arguments> addEntitySteps() {
        return Stream.of(
                Arguments.of(BattleObserver.BattleEvent.PLAYER_ADDED, 0, Entity.AnimationType.WALK_RIGHT),
                Arguments.of(BattleObserver.BattleEvent.OPPONENT_ADDED, 1, Entity.AnimationType.IMMOBILE)
        );
    }

    @Test
    void add_player_hit_damage_battle_step() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), "6");
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        Label dmgEntityValLabel = (Label) ((Table) hud.getBattleHUDStage().getActors().get(2)).getCells().get(0).getActor();

        hud.onNotify(player, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);

        assertTrue(dmgEntityValLabel.isVisible());
        assertEquals("6", dmgEntityValLabel.getText().toString());
    }

    @Test
    void add_opponent_hit_damage_battle_step() {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), "6");
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        Label dmgEntityValLabel = (Label) ((Table) hud.getBattleHUDStage().getActors().get(3)).getCells().get(0).getActor();

        hud.onNotify(enemy, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);

        assertTrue(dmgEntityValLabel.isVisible());
        assertEquals("6", dmgEntityValLabel.getText().toString());
    }

    @Test
    void opponent_defeated_battle_step() {
        String xpReward = "10";
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString(), xpReward);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        Label dmgEntityValLabel = (Label) ((Table) hud.getBattleHUDStage().getActors().get(3)).getCells().get(0).getActor();

        hud.onNotify(enemy, BattleObserver.BattleEvent.OPPONENT_DEFEATED);

        assertFalse(dmgEntityValLabel.isVisible());
        assertFalse(hud.getBattleStatusUI().isVisible());
        assertEquals(xpReward, String.valueOf(hud.getBattleStatusUI().getXPValue()));
        assertFalse(hud.getBattleUI().isVisible());
        assertFalse(hud.getBattleUI().isTouchable());
    }

    @ParameterizedTest
    @MethodSource("entityTurnDoneSteps")
    void entity_turn_done_battle_step(BattleObserver.BattleEvent event) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = spy(new BattleState());
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);

        hud.onNotify(player, event);

        verify(battleState).determineTurn();
    }

    private static Stream<Arguments> entityTurnDoneSteps() {
        return Stream.of(
                Arguments.of(BattleObserver.BattleEvent.PLAYER_TURN_DONE),
                Arguments.of(BattleObserver.BattleEvent.OPPONENT_TURN_DONE)
        );
    }

    @ParameterizedTest
    @MethodSource("consumeItems")
    void consume_items(InventoryItem.ItemTypeID itemTypeId, int value, String property) {
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        player.setEntityEncounteredType(EntityFactory.EntityName.RABITE);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);
        BattleState battleState = new BattleState();
        Stage battleStage = new Stage();
        BattleHUD hud = new BattleHUD(mapManager, battleStage, battleState);
        InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeId);
        String itemInfo = item.getItemUseType() + MESSAGE_TOKEN + item.getItemUseTypeValue();

        hud.onNotify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);

        if (InventoryItem.doesRestoreHP(item.getItemUseType())) {
            assertEquals(value, hud.getBattleStatusUI().getHPValue());
        } else {
            assertEquals(value, hud.getBattleStatusUI().getMPValue());
        }
        assertEquals(value, ProfileManager.getInstance().getProperty(property, Integer.class));
    }

    private static Stream<Arguments> consumeItems() {
        return Stream.of(
                Arguments.of(InventoryItem.ItemTypeID.SCROLL01, 30, "currentPlayerHP"),
                Arguments.of(InventoryItem.ItemTypeID.POTIONS01, 20, "currentPlayerMP")
        );
    }
}
