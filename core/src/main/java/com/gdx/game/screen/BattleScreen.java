package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.battle.BattleHUD;
import com.gdx.game.battle.BattleInventoryUI;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.battle.BattleState;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.inventory.InventoryItemLocation;
import com.gdx.game.inventory.InventoryUI;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.BATTLE_THEME;

public class BattleScreen extends BaseScreen implements BattleObserver {

    private TextureRegion[]  textureRegions;
    private InputMultiplexer multiplexer;
    private OrthographicCamera camera;
    private Stage battleStage;
    private MapManager mapManager;
    private PlayerHUD playerHUD;
    private BattleHUD battleHUD;

    private BattleState battleState;

    public BattleScreen(GdxGame gdxGame, PlayerHUD playerHUD_, MapManager mapManager_, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = BATTLE_THEME;
        this.mapManager = mapManager_;
        this.playerHUD = playerHUD_;

        battleState = new BattleState();
        battleState.addObserver(this);
        playerHUD.setBattleState(battleState);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
        battleStage = new Stage(viewport, gdxGame.getBatch());
        battleHUD = new BattleHUD(mapManager, battleStage, battleState);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(battleHUD.getBattleHUDStage());
    }

    private void removeEntities() {
        Array<Entity> entities = mapManager.getCurrentMapEntities();
        for(Entity entity: entities) {
            if(entity.getEntityConfig().getEntityID().equals(mapManager.getPlayer().getEntityEncounteredType().toString())) {
                mapManager.removeMapEntity(entity);
            }
        }
        mapManager.getPlayer().setEntityEncounteredType(null);
    }

    private void setupGameOver() {
        battleHUD.getDmgOpponentValLabel().setVisible(false);
        battleHUD.getDmgPlayerValLabel().setVisible(false);
        battleHUD.getBattleUI().setVisible(false);
        battleHUD.getBattleStatusUI().setVisible(false);
        battleStage.draw();

        ArrayList<TransitionEffect> effects = new ArrayList<>();
        effects.add(new FadeOutTransitionEffect(1f));
        setScreenWithTransition((BaseScreen) gdxGame.getScreen(), new GameOverScreen(gdxGame, mapManager, resourceManager), effects);
    }

    private void refreshStatus() {
        playerHUD.getStatusUI().setHPValue(battleHUD.getBattleStatusUI().getHPValue());
        playerHUD.getStatusUI().setMPValue(battleHUD.getBattleStatusUI().getMPValue());
    }

    private void refreshInventory() {
        Array<InventoryItemLocation> inventory = BattleInventoryUI.getInventory(battleHUD.getBattleInventoryUI().getInventorySlotTable());
        InventoryUI.populateInventory(playerHUD.getInventoryUI().getInventorySlotTable(), inventory, playerHUD.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
    }

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch(event) {
            case RESUME_OVER:
                refreshStatus();
                refreshInventory();
                ProfileManager.getInstance().saveProfile();
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), new ArrayList<>());
                removeEntities();
                break;
            case OPPONENT_TURN_DONE:
                if(GameScreen.getGameState() == GameScreen.GameState.GAME_OVER) {
                    setupGameOver();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, BATTLE_THEME);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, BATTLE_THEME);
    }

    @Override
    public void render(float delta) {
        gdxGame.getBatch().setProjectionMatrix(camera.combined);

        gdxGame.getBatch().begin();
        gdxGame.getBatch().draw(resourceManager.battleBackgroundMeadow, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(textureRegions != null) {
            gdxGame.getBatch().draw(textureRegions[1], 150, 175, textureRegions[1].getRegionWidth()*3f, textureRegions[1].getRegionHeight()*3f);
        }

        gdxGame.getBatch().end();
        battleStage.act(Gdx.graphics.getDeltaTime());
        battleStage.draw();

        battleHUD.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        battleStage.dispose();
        battleHUD.dispose();
        playerHUD.dispose();
    }
}
