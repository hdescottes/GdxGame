package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.entities.player.PlayerGraphicsComponent;
import com.gdx.game.inventory.InventoryObserver;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(GdxRunner.class)
public class BattleStateTest {

    private MockedConstruction<PlayerGraphicsComponent> mockPlayerGraphics;

    private MockedConstruction<NPCGraphicsComponent> mockNPCGraphics;

    private final ProfileManager profileManager = ProfileManager.getInstance();

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockPlayerGraphics = mockConstruction(PlayerGraphicsComponent.class);
        mockNPCGraphics = mockConstruction(NPCGraphicsComponent.class);
        profileManager.setProperty("currentPlayerAP", 5);
        profileManager.setProperty("currentPlayerDP", 5);
        profileManager.setProperty("currentPlayerMP", 5);
        profileManager.setProperty("currentPlayerHP", 20);
        EntityBonus entityBonusAtk = new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "0.3");
        EntityBonus entityBonusDef = new EntityBonus(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "0.1");
        profileManager.setProperty("bonusSet", new Array<>(new EntityBonus[]{entityBonusAtk, entityBonusDef}));
    }

    @AfterEach
    void end() {
        mockPlayerGraphics.close();
        mockNPCGraphics.close();
    }

    @Test
    void playerAttack_doNotKill() {
        BattleState battleState = spy(new BattleState());
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), "20");
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.toString(), "4");
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);

        battleState.getPlayerAttackCalculationTimer().run();

        assertThat(enemy.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString())).isLessThanOrEqualTo("18");
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
        verify(battleState, never()).notify(enemy, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
    }

    @Test
    void playerAttack_killOpponent() {
        String dropId = "1";
        EntityConfig.Drop drop = new EntityConfig.Drop();
        drop.setProbability(1);
        drop.setItemTypeID(dropId);
        BattleState battleState = spy(new BattleState());
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), "1");
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.toString(), "4");
        enemy.getEntityConfig().addDrop(drop);
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);

        battleState.getPlayerAttackCalculationTimer().run();

        assertThat(enemy.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString())).isEqualTo("0");
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
        verify(battleState).notify(dropId, InventoryObserver.InventoryEvent.DROP_ITEM_ADDED);
    }

    @Test
    void opponentAttack() {
        BattleState battleState = spy(new BattleState());
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.toString(), "6");
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);

        battleState.getOpponentAttackCalculationTimer().run();

        assertThat(player.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString())).isLessThanOrEqualTo("18");
        verify(battleState).notify(player, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);
        verify(battleState).notify(enemy, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
    }

    @ParameterizedTest
    @MethodSource("determineRun")
    void playerRuns(float speedRatio, BattleObserver.BattleEvent event) {
        BattleState battleState = spy(new BattleState());
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        battleState.setCurrentOpponent(enemy);
        battleState.setSpeedRatio(speedRatio);

        battleState.playerRuns();

        verify(battleState).notify(enemy, event);
    }

    private static Stream<Arguments> determineRun() {
        return Stream.of(
                Arguments.of(100f, BattleObserver.BattleEvent.PLAYER_RUNNING),
                Arguments.of(-1.2f, BattleObserver.BattleEvent.PLAYER_TURN_DONE)
        );
    }

    @ParameterizedTest
    @MethodSource("determineTurn")
    void determineTurn(float speedRatio, boolean expectPlayerTurn) {
        BattleState battleState = spy(new BattleState());
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        Entity enemy = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENEMY);
        enemy.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), "5");
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);
        battleState.setSpeedRatio(speedRatio);

        battleState.getTurnTimer().run();

        if (expectPlayerTurn) {
            verify(battleState).notify(player, BattleObserver.BattleEvent.PLAYER_TURN_START);
        } else {
            verify(battleState, never()).notify(player, BattleObserver.BattleEvent.PLAYER_TURN_START);
        }
    }

    private static Stream<Arguments> determineTurn() {
        return Stream.of(
                Arguments.of(1.2f, true),
                Arguments.of(0.8f, false)
        );
    }
}
