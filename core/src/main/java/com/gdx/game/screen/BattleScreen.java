package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.battle.BattleState;
import com.gdx.game.entities.Entity;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;

public class BattleScreen extends BaseScreen implements BattleObserver {

/*    private Box2dWorld box2d;
    private ControlManager controlManager;
    private Hero hero;
    private AnimationManager animationManager = new AnimationManager();*/
    private TextureRegion[]  textureRegions;
    private OrthographicCamera camera;
    private Stage battleStage;
    private MapManager mapManager;

    //private AnimatedImage image;

    private final int enemyWidth = 96;
    private final int enemyHeight = 96;

    private BattleState battleState = null;
    private TextButton attackButton = null;
    private TextButton runButton = null;
    private Label damageValLabel = null;

    private float battleTimer = 0;
    private final float checkTimer = 1;

    /*private ShakeCamera battleShakeCam = null;
    private Array<ParticleEffect> effects;*/

    private float origDamageValLabelY = 0;
    private Vector2 currentImagePosition;

    //To be able to come back to game screen
    //TODO: remove
    private float lifeTime;
    private Long delay = 3L;

    public BattleScreen(GdxGame gdxGame, MapManager mapManager, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = AudioObserver.AudioTypeEvent.BATTLE_THEME;
        this.mapManager = mapManager;
        //this.viewport = new StretchViewport(getBattleCam().viewportWidth, getBattleCam().viewportHeight, getBattleCam());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
        battleStage = new Stage(viewport, gdxGame.getBatch());
/*
        box2d = new Box2dWorld();
        handleEntities();*/
    }
/*
    private void handleEntities() {
        if(gdxGame.getEntityMap() != null) {
            hero = (Hero) gdxGame.getEntityMap().get("hero");
            hero.setTexture(resourceManager.heroWalkRight);
            textureRegions = animationManager.setTextureRegions(hero.getTexture(), 32, 37);
        }
    }*/

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        /*switch(event){
            case PLAYER_TURN_START:
                runButton.setDisabled(true);
                runButton.setTouchable(Touchable.disabled);
                attackButton.setDisabled(true);
                attackButton.setTouchable(Touchable.disabled);
                break;
            case OPPONENT_ADDED:
                image.setEntity(entity);
                image.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                image.setSize(enemyWidth, enemyHeight);
                image.setPosition(this.getCell(image).getActorX(), this.getCell(image).getActorY());

                currentImagePosition.set(image.getX(), image.getY());
                if( battleShakeCam == null ){
                    battleShakeCam = new ShakeCamera(currentImagePosition.x, currentImagePosition.y, 30.0f);
                }

                //Gdx.app.debug(TAG, "Image position: " + _image.getX() + "," + _image.getY() );

                //this.getTitleLabel().setText("Level " + battleState.getCurrentZoneLevel() + " " + entity.getEntityConfig().getEntityID());
                break;
            case OPPONENT_HIT_DAMAGE:
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
                break;
            default:
                break;
        }*/
    }

    @Override
    public void show() {
        /*CameraManager cameraManager = new CameraManager();
        controlManager = cameraManager.insertControl(getBattleCam());*/

        Gdx.input.setInputProcessor(battleStage);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.BATTLE_THEME);
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

        //To be able to come back to game screen
        //TODO: remove
        lifeTime += Gdx.graphics.getDeltaTime();
        if(lifeTime > delay) {
            /*Array<Entity> entities = mapManager.getCurrentMapEntities();
            for(Entity entity: entities) {
                if(entity.getEntityConfig().getEntityID().equals(mapManager.getPlayer().getEntityEncounteredType().toString())) {
                    mapManager.removeMapEntity(entity);
                }
            }*/
            mapManager.getPlayer().setEntityEncounteredType(null);
            Gdx.app.exit();
            //gdxGame.setScreen(gdxGame.getGameScreen());
        }

        //box2d.tick(getBattleCam(), controlManager);
    }
}
