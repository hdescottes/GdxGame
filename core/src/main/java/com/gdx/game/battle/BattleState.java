package com.gdx.game.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleState extends BattleSubject {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleState.class);

    private Entity currentOpponent;
    private Entity player;
    private int currentZoneLevel = 0;
    private int currentPlayerAP;
    private int currentPlayerDP;
    private int currentPlayerWandAPPoints = 0;
    private final int chanceOfAttack = 25;
    private final int chanceOfEscape = 40;
    private final int criticalChance = 90;
    private Timer.Task playerAttackCalculations;
    private Timer.Task opponentAttackCalculations;
    private Timer.Task checkPlayerMagicUse;

    public BattleState() {
        playerAttackCalculations = getPlayerAttackCalculationTimer();
        opponentAttackCalculations = getOpponentAttackCalculationTimer();
        checkPlayerMagicUse = getPlayerMagicUseCheckTimer();

        currentPlayerAP = ProfileManager.getInstance().getProperty("currentPlayerAP", Integer.class);
        currentPlayerDP = ProfileManager.getInstance().getProperty("currentPlayerDP", Integer.class);
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
    }

    public void setCurrentZoneLevel(int zoneLevel) {
        currentZoneLevel = zoneLevel;
    }

    public int getCurrentZoneLevel() {
        return currentZoneLevel;
    }

    public boolean isOpponentReady() {
        if(currentZoneLevel == 0) {
            return false;
        }
        int randomVal = MathUtils.random(1,100);

        //Gdx.app.debug(TAG, "CHANGE OF ATTACK: " + _chanceOfAttack + " randomval: " + randomVal);

        if(chanceOfAttack > randomVal) {
            //setCurrentOpponent();
            return true;
        } else {
            return false;
        }
    }

    public void setCurrentOpponent(Entity entity) {
        LOGGER.debug("Entered BATTLE ZONE: " + currentZoneLevel);
        //Entity entity = MonsterFactory.getInstance().getRandomMonster(currentZoneLevel);
        if(entity == null) {
            return;
        }
        this.currentOpponent = entity;
        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED);
    }

    public void setPlayer(Entity entity) {
        if(entity == null) {
            return;
        }
        this.player = entity;
        notify(entity, BattleObserver.BattleEvent.PLAYER_ADDED);
    }

    public void playerAttacks() {
        if(currentOpponent == null) {
            return;
        }

        //Check for magic if used in attack; If we don't have enough MP, then return
        int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
        notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_START);

        if(currentPlayerWandAPPoints == 0) {
            if(!playerAttackCalculations.isScheduled()) {
                Timer.schedule(playerAttackCalculations, 1);
            }
        } else if(currentPlayerWandAPPoints > mpVal) {
            BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
            return;
        } else {
            if(!checkPlayerMagicUse.isScheduled() && !playerAttackCalculations.isScheduled()) {
                Timer.schedule(checkPlayerMagicUse, .5f);
                Timer.schedule(playerAttackCalculations, 1);
            }
        }
    }

    public void opponentAttacks() {
        if(currentOpponent == null) {
            return;
        }

        if(!opponentAttackCalculations.isScheduled()) {
            Timer.schedule(opponentAttackCalculations, 1);
        }
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

    private Timer.Task getPlayerAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentHP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
                int currentOpponentDP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()));

                int damage = MathUtils.clamp(currentPlayerAP - currentOpponentDP, 0, currentPlayerAP);

                LOGGER.debug("ENEMY HAS " + currentOpponentHP + " hit with damage: " + damage);

                currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP);
                currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), String.valueOf(currentOpponentHP));

                LOGGER.debug("Player attacks " + currentOpponent.getEntityConfig().getEntityID() + " leaving it with HP: " + currentOpponentHP);

                currentOpponent.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(damage));
                if(damage > 0) {
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);
                }

                if(currentOpponentHP == 0) {
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
                }

                BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_DONE);
            }
        };
    }

    private Timer.Task getOpponentAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentHP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));

                if(currentOpponentHP <= 0) {
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
                    return;
                }

                int currentOpponentAP = Integer.parseInt(currentOpponent.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_ATTACK_POINTS.toString()));
                int damage = MathUtils.clamp(currentOpponentAP - currentPlayerDP, 0, currentOpponentAP);
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                hpVal = MathUtils.clamp( hpVal - damage, 0, hpVal);
                player.getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(damage));
                ProfileManager.getInstance().setProperty("currentPlayerHP", hpVal);

                if(damage > 0) {
                    BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);
                }

                LOGGER.debug("Player HIT for " + damage + " BY " + currentOpponent.getEntityConfig().getEntityID() + " leaving player with HP: " + hpVal);

                BattleState.this.notify(currentOpponent, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
            }
        };
    }

    public void playerRuns() {
        int randomVal = MathUtils.random(1,100);
        if(chanceOfEscape > randomVal) {
            notify(currentOpponent, BattleObserver.BattleEvent.PLAYER_RUNNING);
        } else if(randomVal > criticalChance) {
            opponentAttacks();
        }
    }
}
