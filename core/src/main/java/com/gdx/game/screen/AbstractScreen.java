package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.game.GdxGame;
import com.gdx.game.manager.CameraManager;

public class AbstractScreen implements Screen {
    protected final GdxGame gdxGame;
    protected OrthographicCamera gameCam;
    protected OrthographicCamera battleCam;
    // viewport that keeps aspect ratios of the game when resizing
    protected Viewport viewport;
    // main stage of each screen
    protected Stage stage;

    public AbstractScreen(GdxGame gdxGame) {
        this.gdxGame = gdxGame;

        CameraManager cameraManager = new CameraManager();
        gameCam = cameraManager.createCamera(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3, .4f);
        battleCam = cameraManager.createCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
        // the game will retain it's scaled dimensions regardless of resizing
        viewport = new StretchViewport(gameCam.viewportWidth, gameCam.viewportHeight, gameCam);
        stage = new Stage(viewport, gdxGame.getBatch());
    }

    @Override
    public void show() {
        // Nothing
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Nothing
    }

    @Override
    public void pause() {
        // Nothing
    }

    @Override
    public void resume() {
        // Nothing
    }

    @Override
    public void hide() {
        // Nothing
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public OrthographicCamera getGameCam() {
        return gameCam;
    }

    public OrthographicCamera getBattleCam() {
        return battleCam;
    }

    public Stage getStage() {
        return stage;
    }
}
