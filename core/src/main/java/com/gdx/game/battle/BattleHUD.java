package com.gdx.game.battle;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.GameScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleHUD implements Screen, BattleObserver {

    private static  final Logger LOGGER = LoggerFactory.getLogger(BattleHUD.class);

    private Stage battleHUDStage;

    private BattleUI battleUI;
    private BattleStatusUI battleStatusUI;

    private MapManager mapManager;
    private PlayerHUD playerHUD;
    private AnimatedImage playerImage;
    private Entity player;
    private AnimatedImage opponentImage;
    private Entity enemy;
    private BattleState battleState;

    private final int enemyWidth = 50;
    private final int enemyHeight = 50;
    private final int playerWidth = 50;
    private final int playerHeight = 50;

    private float origDmgPlayerValLabelY = 0;
    private float origDmgOpponentValLabelY = 0;
    private Label dmgPlayerValLabel;
    private Label dmgOpponentValLabel;
    private Table dmgPlayerLabelTable = new Table();
    private Table dmgOpponentLabelTable = new Table();
    private Vector2 currentOpponentImagePosition = new Vector2(0,0);
    private Vector2 currentPlayerImagePosition = new Vector2(0,0);

    public BattleHUD(MapManager mapManager_, Stage battleStage, BattleState battleState_) {
        this.mapManager = mapManager_;
        this.battleState = battleState_;

        battleState.addObserver(this);

        battleHUDStage = new Stage(battleStage.getViewport());
        player = mapManager.getPlayer();
        playerImage = new AnimatedImage();
        playerImage.setTouchable(Touchable.disabled);
        enemy = EntityFactory.getInstance().getEntityByName(mapManager.getPlayer().getEntityEncounteredType());
        opponentImage = new AnimatedImage();
        opponentImage.setTouchable(Touchable.disabled);
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);

        dmgPlayerValLabel = new Label("0", ResourceManager.skin);
        dmgPlayerValLabel.setVisible(false);
        origDmgPlayerValLabelY = dmgPlayerValLabel.getY() + playerHeight;
        dmgOpponentValLabel = new Label("0", ResourceManager.skin);
        dmgOpponentValLabel.setVisible(false);
        origDmgOpponentValLabelY = dmgOpponentValLabel.getY() + enemyHeight;

        battleStatusUI = new BattleStatusUI();
        battleStatusUI.setKeepWithinStage(false);
        battleStatusUI.setVisible(true);
        battleStatusUI.setPosition(0, 0);
        battleStatusUI.setWidth(battleStage.getWidth() / 2);
        battleStatusUI.setHeight(battleStage.getHeight() / 4);

        battleUI = new BattleUI(battleState);
        battleUI.setMovable(false);
        battleUI.setVisible(true);
        battleUI.setKeepWithinStage(false);
        battleUI.setPosition(battleStage.getWidth() / 10, 2 * battleStage.getHeight() / 3);
        battleUI.setWidth(battleStage.getWidth() / 2);
        battleUI.setHeight(battleStage.getHeight() / 4);

        battleUI.validate();
        battleStatusUI.validate();

        dmgPlayerLabelTable.add(dmgPlayerValLabel).padLeft(playerWidth / 2).padBottom(playerHeight * 4);
        dmgPlayerLabelTable.setPosition(currentPlayerImagePosition.x, currentPlayerImagePosition.y);
        dmgOpponentLabelTable.add(dmgOpponentValLabel).padLeft(enemyWidth / 2).padBottom(enemyHeight * 4);
        dmgOpponentLabelTable.setPosition(currentOpponentImagePosition.x, currentOpponentImagePosition.y);

        battleHUDStage.addActor(playerImage);
        battleHUDStage.addActor(opponentImage);
        battleHUDStage.addActor(dmgPlayerLabelTable);
        battleHUDStage.addActor(dmgOpponentLabelTable);
        battleHUDStage.addActor(battleUI);
        battleHUDStage.addActor(battleStatusUI);
    }

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch(event) {
            case PLAYER_TURN_START:
                LOGGER.debug("Player turn start");
                battleUI.setVisible(false);
                battleUI.setTouchable(Touchable.disabled);
                break;
            case PLAYER_ADDED:
                playerImage.setEntity(entity);
                playerImage.setCurrentAnimation(Entity.AnimationType.WALK_RIGHT);
                playerImage.setSize(playerWidth, playerHeight);
                playerImage.setPosition(0, 200);
                playerImage.addAction(Actions.moveTo(200, 200, 2));

                currentPlayerImagePosition.set(((MoveToAction) playerImage.getActions().get(0)).getX(), playerImage.getY());
                LOGGER.debug("Player added on battle map");
                break;
            case OPPONENT_ADDED:
                opponentImage.setEntity(entity);
                opponentImage.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                opponentImage.setSize(enemyWidth, enemyHeight);
                opponentImage.setPosition(600, 200);

                currentOpponentImagePosition.set(opponentImage.getX(), opponentImage.getY());
                LOGGER.debug("Opponent added on battle map");
                /*if( battleShakeCam == null ){
                    battleShakeCam = new ShakeCamera(currentImagePosition.x, currentImagePosition.y, 30.0f);
                }*/

                //Gdx.app.debug(TAG, "Image position: " + _image.getX() + "," + _image.getY() );

                //this.getTitleLabel().setText("Level " + battleState.getCurrentZoneLevel() + " " + entity.getEntityConfig().getEntityID());
                break;
            case PLAYER_HIT_DAMAGE:
                int damagePlayer = Integer.parseInt(player.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                dmgPlayerValLabel.setText(String.valueOf(damagePlayer));
                dmgPlayerValLabel.setY(origDmgPlayerValLabelY);
                //battleShakeCam.startShaking();
                dmgPlayerValLabel.setVisible(true);

                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                battleStatusUI.setHPValue(hpVal);

                if(hpVal <= 0){
                    GameScreen.setGameState(GameScreen.GameState.GAME_OVER);
                }
                break;
            case OPPONENT_HIT_DAMAGE:
                int damageEnemy = Integer.parseInt(entity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                dmgOpponentValLabel.setText(String.valueOf(damageEnemy));
                dmgOpponentValLabel.setY(origDmgOpponentValLabelY);
                //battleShakeCam.startShaking();
                dmgOpponentValLabel.setVisible(true);
                LOGGER.debug("Opponent deals " + damageEnemy + " damages");
                break;
            case OPPONENT_DEFEATED:
                dmgOpponentValLabel.setVisible(false);
                dmgOpponentValLabel.setY(origDmgOpponentValLabelY);
                //opponentImage.setVisible(false);
                LOGGER.debug("Opponent is defeated");
                break;
            case OPPONENT_TURN_DONE:
                if(GameScreen.getGameState() != GameScreen.GameState.GAME_OVER) {
                    battleUI.setVisible(true);
                    battleUI.setTouchable(Touchable.enabled);
                }
                LOGGER.debug("Opponent turn done");
                break;
            case PLAYER_TURN_DONE:
                battleState.opponentAttacks();
                LOGGER.debug("Player turn done");
                break;
            /*case PLAYER_USED_MAGIC:
                float x = currentImagePosition.x + (enemyWidth/2);
                float y = currentImagePosition.y + (enemyHeight/2);
                //effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
                break;*/
            default:
                break;
        }
    }

    public Stage getBattleHUDStage() {
        return battleHUDStage;
    }

    public BattleUI getBattleUI() {
        return battleUI;
    }

    public BattleStatusUI getBattleStatusUI() {
        return battleStatusUI;
    }

    public Label getDmgPlayerValLabel() {
        return dmgPlayerValLabel;
    }

    public Label getDmgOpponentValLabel() {
        return dmgOpponentValLabel;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        battleHUDStage.act(delta);
        battleHUDStage.draw();

        if(dmgPlayerValLabel.isVisible() && dmgPlayerValLabel.getY() < this.battleHUDStage.getHeight()) {
            dmgPlayerValLabel.setY(dmgPlayerValLabel.getY()+5);
        }
        if(dmgOpponentValLabel.isVisible() && dmgOpponentValLabel.getY() < this.battleHUDStage.getHeight()) {
            dmgOpponentValLabel.setY(dmgOpponentValLabel.getY()+5);
        }

        if (playerImage.getActions().size == 0 && !playerImage.getCurrentAnimationType().equals(Entity.AnimationType.LOOK_RIGHT)) {
            playerImage.setCurrentAnimation(Entity.AnimationType.LOOK_RIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        battleHUDStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        battleHUDStage.dispose();
        player.dispose();
        enemy.dispose();
        battleUI.remove();
        battleStatusUI.remove();
        playerImage.remove();
        opponentImage.remove();
        dmgPlayerLabelTable.remove();
        dmgOpponentLabelTable.remove();
        dmgOpponentValLabel.remove();
        dmgPlayerValLabel.remove();
    }
}