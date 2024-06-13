package com.gdx.game.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.gdx.game.common.UtilityClass.calculateBonus;

public class BattleState extends BattleSubject {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleState.class);

    private Entity currentOpponent;
    private Entity player;
    private float speedRatioInit = 0;
    private float speedRatio = 0;
    private final float criticalMultiplier = 1.5f;
    private int currentZoneLevel = 0;
    private int currentPlayerAP;
    private int currentPlayerDP;
    private int currentPlayerWandAPPoints = 0;
    private Timer.Task playerAttackCalculations;
    private Timer.Task opponentAttackCalculations;
    private Timer.Task checkPlayerMagicUse;
    private Timer.Task determineTurn;

    public BattleState() {
        playerAttackCalculations = getPlayerAttackCalculationTimer();
        opponentAttackCalculations = getOpponentAttackCalculationTimer();
        checkPlayerMagicUse = getPlayerMagicUseCheckTimer();
        determineTurn = getTurnTimer();

        currentPlayerAP = ProfileManager.getInstance().getProperty("currentPlayerAP", Integer.class);
        currentPlayerDP = ProfileManager.getInstance().getProperty("currentPlayerDP", Integer.class);

        updateStatWithBonus("bonusSet");
    }

    public void resetDefaults() {
        LOGGER.debug("Resetting defaults...");
        currentZoneLevel = 0;
        currentPlayerAP = 0;
        currentPlayerDP = 0;
        currentPlayerWandAPPoints = 0;
        playerAttackCalculations.cancel();
        opponentAttackCalculations.cancel();
        checkPlayerMagicUse.cancel();
        determineTurn.cancel();
    }

    private void resetEntityBattleProps() {
        player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_RECEIVED_CRITICAL.toString(), "false");
        player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(0));
        currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_RECEIVED_CRITICAL.toString(), "false");
        currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(0));
    }

    public void setCurrentOpponent(Entity entity) {
        LOGGER.debug("Entered BATTLE ZONE: {}", currentZoneLevel);
        if (entity == null) {
            return;
        }
        this.currentOpponent = entity;
        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED);
    }

    public void setPlayer(Entity entity) {
        if (entity == null) {
            return;
        }
        this.player = entity;
        notify(entity, BattleObserver.BattleEvent.PLAYER_ADDED);
    }

    public void setSpeedRatio(float speedRatio) {
        this.speedRatioInit = speedRatio;
        this.speedRatio = speedRatio;
    }

    public void determineTurn() {
        Timer.schedule(determineTurn, 1);
    }

    private void updateStatWithBonus(String bonusAttribute) {
        HashMap<String, Integer> bonusMap = calculateBonus(bonusAttribute);
        Map<String, String> mapping = Map.of(
                EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "currentPlayerAP",
                EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "currentPlayerDP"
        );

        mapping.forEach((key, value) -> {
            if (bonusMap.get(key) != null) {
                try {
                    Field valueField = this.getClass().getDeclaredField(value);
                    int currentPlayerValue = valueField.getInt(this);
                    int newPlayerValue = currentPlayerValue + bonusMap.get(key);
                    valueField.setInt(this, newPlayerValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("A value in the mapping map does not exist", e);
                }
            }
        });
    }

    public void playerAttacks() {
        if (currentOpponent == null) {
            return;
        }

        //Check for magic if used in attack; If we don't have enough MP, then return
        int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
        notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_PHASE_START);

        if (currentPlayerWandAPPoints == 0) {
            if (!playerAttackCalculations.isScheduled()) {
                Timer.schedule(playerAttackCalculations, 1);
            }
        } else if (currentPlayerWandAPPoints > mpVal) {
            notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
        } else {
            if (!checkPlayerMagicUse.isScheduled() && !playerAttackCalculations.isScheduled()) {
                Timer.schedule(checkPlayerMagicUse, .5f);
                Timer.schedule(playerAttackCalculations, 1);
            }
        }
    }

    public void opponentAttacks() {
        if (currentOpponent == null) {
            return;
        }

        int currentOpponentHP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
        if (!opponentAttackCalculations.isScheduled() && currentOpponentHP > 0) {
            Timer.schedule(opponentAttackCalculations, 1);
        }
    }

    public void startPlayerTurn() {
        notify(player, BattleObserver.BattleEvent.PLAYER_TURN_START);
    }

    public void resumeOver() {
        notify(currentOpponent, BattleObserver.BattleEvent.RESUME_OVER);
    }

    protected Timer.Task getTurnTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                resetEntityBattleProps();
                if (speedRatio < 1) {
                    speedRatio += speedRatioInit;
                    opponentAttacks();
                } else {
                    speedRatio--;
                    startPlayerTurn();
                }
            }
        };
    }

    private Timer.Task getPlayerMagicUseCheckTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
                mpVal -= currentPlayerWandAPPoints;
                ProfileManager.getInstance().setProperty("currentPlayerMP", mpVal);
                BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_USED_MAGIC);
            }
        };
    }

    protected Timer.Task getPlayerAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentHP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
                int currentOpponentDP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.toString()));

                double criticalChance = BattleUtils.criticalChance(currentPlayerAP);
                int damage = MathUtils.clamp(currentPlayerAP - currentOpponentDP, 0, currentPlayerAP);
                if (BattleUtils.isSuccessful(criticalChance)) {
                    damage *= criticalMultiplier;
                    currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_RECEIVED_CRITICAL.toString(), "true");
                    LOGGER.debug("Critical hit !");
                }

                LOGGER.debug("ENEMY HAS {} hit with damage: {}", currentOpponentHP, damage);

                currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP);
                currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), String.valueOf(currentOpponentHP));

                LOGGER.debug("Player attacks {} leaving it with HP: {}", currentOpponent.getEntityConfig().getEntityID(), currentOpponentHP);

                currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(damage));
                if (damage > 0) {
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);
                }

                if (currentOpponentHP == 0) {
                    calculateDrops();
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
                }

                BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
            }
        };
    }

    private void calculateDrops() {
        Array<EntityConfig.Drop> drops = currentOpponent.getEntityConfig().getDrops();
        for (EntityConfig.Drop drop : drops) {
            boolean isSuccessful = BattleUtils.isSuccessful(drop.getProbability());
            if (isSuccessful) {
                BattleState.this.notify(drop.getItemTypeID(), InventoryObserver.InventoryEvent.DROP_ITEM_ADDED);
            }
        }
    }

    protected Timer.Task getOpponentAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentAP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.toString()));

                double criticalChance = BattleUtils.criticalChance(currentOpponentAP);
                int damage = MathUtils.clamp(currentOpponentAP - currentPlayerDP, 0, currentOpponentAP);
                if (BattleUtils.isSuccessful(criticalChance)) {
                    damage *= criticalMultiplier;
                    player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_RECEIVED_CRITICAL.toString(), "true");
                    LOGGER.debug("Critical hit !");

                }
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                hpVal = MathUtils.clamp( hpVal - damage, 0, hpVal);
                player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(damage));
                ProfileManager.getInstance().setProperty("currentPlayerHP", hpVal);

                if (damage > 0) {
                    BattleState.this.notify(player, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);
                }

                LOGGER.debug("Player HIT for {} BY {} leaving player with HP: {}", damage, currentOpponent.getEntityConfig().getEntityID(), hpVal);

                BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
            }
        };
    }

    public void playerRuns() {
        notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_PHASE_START);

        double escapeChance = BattleUtils.escapeChance(speedRatio);

        if (BattleUtils.isSuccessful(escapeChance)) {
            LOGGER.debug("Player flees with {}% escape chance", escapeChance * 100);
            notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_RUNNING);
        } else {
            notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
        }
    }
}
