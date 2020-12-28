package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.gdx.game.GdxGame;
import com.gdx.game.manager.RessourceManager;

import java.util.ArrayList;

public class OptionScreen extends BaseScreen {

    private Table table;
    private Stage optionStage = new Stage();
    private Stage backgroundStage = new Stage();
    private Screen previousScreen;
    private Image previousScreenAsImg;
    private float stateTime;

    private VfxManager vfxManager;
    private GaussianBlurEffect vfxEffect;

    public OptionScreen(GdxGame gdxGame, Screen previousScreen, RessourceManager ressourceManager) {
        super(gdxGame, ressourceManager);
        this.previousScreen = previousScreen;

        loadContents();
    }

    public OptionScreen(GdxGame gdxGame, Screen previousScreen, Image previousScreenAsImg, RessourceManager ressourceManager) {
        super(gdxGame, ressourceManager);
        this.previousScreen = previousScreen;
        this.previousScreenAsImg = previousScreenAsImg;

        loadContents();
    }

    private void loadContents() {
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxEffect = new GaussianBlurEffect();
        vfxManager.addEffect(vfxEffect);

        createTable();
        handleBackground();
        handleControlButton();
        handleMusicButton();
        handleBackButton();
    }

    private void createTable() {
        table = new Table();
        table.setBounds(0,0, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    private void handleBackground() {
    }

    private void handleControlButton() {
        createButton("Control",0,table.getHeight()/10, table);

        Actor controlButton = table.getCells().get(0).getActor();
        controlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                //TODO: To be completed
            }
        });

    }

    private void handleMusicButton() {
        createButton("Music",0,table.getHeight()/15, table);

        Actor musicButton = table.getCells().get(1).getActor();
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                //TODO: To be completed
            }
        });
    }

    private void handleBackButton() {
        createButton("Back",0,table.getHeight()/5, table);

        Actor musicButton = table.getCells().get(2).getActor();
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                setScreenWithTransition(gdxGame.getScreen(), previousScreen, new ArrayList<>());
            }
        });
    }

    @Override
    public void show() {
        optionStage.addActor(table);
        Gdx.input.setInputProcessor(optionStage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        if (previousScreen != null && previousScreenAsImg == null) {
            previousScreen.render(stateTime);
        }
        if (previousScreenAsImg != null) {
            backgroundStage.addActor(previousScreenAsImg);
            backgroundStage.draw();
        }

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        optionStage.act(delta);
        optionStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        table.remove();
        vfxManager.dispose();
        vfxEffect.dispose();
    }
}
