package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.manager.ResourceManager;

public class BattleScreen extends BaseScreen {

/*    private Box2dWorld box2d;
    private ControlManager controlManager;
    private Hero hero;
    private AnimationManager animationManager = new AnimationManager();*/
    private TextureRegion[]  textureRegions;
    private Stage battleStage;

    //To be able to come back to game screen
    //TODO: remove
    private float lifeTime;
    private Long delay = 3L;

    public BattleScreen(GdxGame gdxGame, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = AudioObserver.AudioTypeEvent.BATTLE_THEME;
        this.viewport = new StretchViewport(getBattleCam().viewportWidth, getBattleCam().viewportHeight, getBattleCam());
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
    public void show() {
        /*CameraManager cameraManager = new CameraManager();
        controlManager = cameraManager.insertControl(getBattleCam());*/

        Gdx.input.setInputProcessor(battleStage);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.BATTLE_THEME);
    }

    @Override
    public void render(float delta) {
        gdxGame.getBatch().setProjectionMatrix(getBattleCam().combined);

        gdxGame.getBatch().begin();
        gdxGame.getBatch().draw(resourceManager.battleBackgroundMeadow, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(textureRegions != null) {
            gdxGame.getBatch().draw(textureRegions[1], 150, 175, textureRegions[1].getRegionWidth()*3f, textureRegions[1].getRegionHeight()*3f);
        }

        gdxGame.getBatch().end();

        //To be able to come back to game screen
        //TODO: remove
       /* lifeTime += Gdx.graphics.getDeltaTime();
        if (lifeTime > delay) {
            hero.collision(hero.getEntityCollision(), false);
            gdxGame.setScreen(gdxGame.getGameScreen());
        }

        box2d.tick(getBattleCam(), controlManager);*/
    }
}
