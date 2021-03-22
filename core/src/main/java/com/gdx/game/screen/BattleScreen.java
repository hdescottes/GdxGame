package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.battle.BattleState;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;

import java.util.ArrayList;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.BATTLE_THEME;

public class BattleScreen extends BaseScreen implements BattleObserver {

    private TextureRegion[]  textureRegions;
    private OrthographicCamera camera;
    private Stage battleStage;
    private MapManager mapManager;
    private AnimatedImage playerImage;
    private Entity player;
    private AnimatedImage opponentImage;
    private Entity enemy;

    //private AnimatedImage image;

    private final int enemyWidth = 50;
    private final int enemyHeight = 50;
    private final int playerWidth = 50;
    private final int playerHeight = 50;

    private BattleState battleState;
    private TextButton attackButton = null;
    private TextButton runButton = null;
    private Label damageValLabel = null;

    private float battleTimer = 0;
    private final float checkTimer = 1;

    /*private ShakeCamera battleShakeCam = null;
    private Array<ParticleEffect> effects;*/

    private float origDamageValLabelY = 0;
    private Vector2 currentOpponentImagePosition = new Vector2(0,0);
    private Vector2 currentPlayerImagePosition = new Vector2(0,0);
    private Vector2 playerPosition;

    //To be able to come back to game screen
    //TODO: remove
    private float lifeTime;
    private Long delay = 3L;

    public BattleScreen(GdxGame gdxGame, MapManager mapManager, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = BATTLE_THEME;
        this.mapManager = mapManager;

        battleState = new BattleState();
        battleState.addObserver(this);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
        battleStage = new Stage(viewport, gdxGame.getBatch());

        player = mapManager.getPlayer();
        playerImage = new AnimatedImage();
        playerImage.setTouchable(Touchable.disabled);
        enemy = EntityFactory.getInstance().getEntityByName(mapManager.getPlayer().getEntityEncounteredType());
        opponentImage = new AnimatedImage();
        opponentImage.setTouchable(Touchable.disabled);
        battleState.setPlayer(player);
        battleState.setCurrentOpponent(enemy);
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

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch(event) {
            /*case PLAYER_TURN_START:
                runButton.setDisabled(true);
                runButton.setTouchable(Touchable.disabled);
                attackButton.setDisabled(true);
                attackButton.setTouchable(Touchable.disabled);
                break;*/
            case PLAYER_ADDED:
                playerImage.setEntity(entity);
                playerImage.setCurrentAnimation(Entity.AnimationType.WALK_RIGHT);
                playerImage.setSize(playerWidth, playerHeight);
                playerImage.setPosition(200, 200);

                currentPlayerImagePosition.set(playerImage.getX(), playerImage.getY());
                break;
            case OPPONENT_ADDED:
                opponentImage.setEntity(entity);
                opponentImage.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                opponentImage.setSize(enemyWidth, enemyHeight);
                opponentImage.setPosition(600, 200);

                currentOpponentImagePosition.set(opponentImage.getX(), opponentImage.getY());
                /*if( battleShakeCam == null ){
                    battleShakeCam = new ShakeCamera(currentImagePosition.x, currentImagePosition.y, 30.0f);
                }*/

                //Gdx.app.debug(TAG, "Image position: " + _image.getX() + "," + _image.getY() );

                //this.getTitleLabel().setText("Level " + battleState.getCurrentZoneLevel() + " " + entity.getEntityConfig().getEntityID());
                break;
            /*case OPPONENT_HIT_DAMAGE:
                int damage = Integer.parseInt(entity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                damageValLabel.setText(String.valueOf(damage));
                damageValLabel.setY(origDamageValLabelY);
                battleShakeCam.startShaking();
                damageValLabel.setVisible(true);
                break;
            case OPPONENT_DEFEATED:
                damageValLabel.setVisible(false);
                damageValLabel.setY(origDamageValLabelY);
                break;
            case OPPONENT_TURN_DONE:
                attackButton.setDisabled(false);
                attackButton.setTouchable(Touchable.enabled);
                runButton.setDisabled(false);
                runButton.setTouchable(Touchable.enabled);
                break;
            case PLAYER_TURN_DONE:
                battleState.opponentAttacks();
                break;
            case PLAYER_USED_MAGIC:
                float x = currentImagePosition.x + (enemyWidth/2);
                float y = currentImagePosition.y + (enemyHeight/2);
                //effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
                break;*/
            default:
                break;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(battleStage);
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

        battleStage.addActor(opponentImage);
        battleStage.addActor(playerImage);
        battleStage.draw();

        //To be able to come back to game screen
        //TODO: remove
        lifeTime += Gdx.graphics.getDeltaTime();
        if(lifeTime > delay) {
            setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), new ArrayList<>());
            removeEntities();
        } //TODO: little bug on input when battle is done

        //box2d.tick(getBattleCam(), controlManager);
    }
}
