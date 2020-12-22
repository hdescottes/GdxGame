package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gdx.game.GdxGame;

public class OptionScreen extends BaseScreen {

    private AssetManager assetManager = new AssetManager();
    private Table table;
    private Stage optionStage = new Stage();
    private float stateTime;

    public OptionScreen(GdxGame gdxGame) {
        super(gdxGame);

        loadAssets();
        createTable();
        handleBackground();
        handleControlButton();
        handleMusicButton();
    }

    private void loadAssets() {
        assetManager.load("asset/textures.atlas", TextureAtlas.class);
        assetManager.finishLoading();
    }

    private void createTable() {
        table = new Table();
        table.setBounds(0,0, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    private void handleBackground() {

    }

    private void handleControlButton() {
        createButton("Control",0,table.getHeight()/10);

        Actor controlButton = table.getCells().get(0).getActor();
        controlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                gdxGame.setScreen(gdxGame.getMenuScreen());
            }
        });

    }

    private void handleMusicButton() {
        createButton("Music",0,table.getHeight()/15);

        Actor musicButton = table.getCells().get(1).getActor();
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                gdxGame.setScreen(gdxGame.getMenuScreen());
            }
        });
    }

    private void createButton(String buttonName, float posX, float posY) {
        TextureAtlas atlas = assetManager.get("asset/textures.atlas", TextureAtlas.class);
        TextureRegion[][] playButtons = atlas.findRegion("play_button").split(80, 40);

        BitmapFont pixel10 = new BitmapFont(Gdx.files.internal("fonts/pixel.fnt"), atlas.findRegion("pixel"), false);

        TextureRegionDrawable imageUp = new TextureRegionDrawable(playButtons[0][0]);
        TextureRegionDrawable imageDown = new TextureRegionDrawable(playButtons[1][0]);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(imageUp, imageDown, null, pixel10);
        TextButton button = new TextButton(buttonName, buttonStyle);
        button.getLabel().setColor(new Color(79 / 255.f, 79 / 255.f, 117 / 255.f, 1));

        table.add(button).padLeft(posX).padTop(posY);
        table.row();
    }

    @Override
    public void show() {
        optionStage.addActor(table);
        Gdx.input.setInputProcessor(optionStage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();

        optionStage.act(delta);
        optionStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        table.remove();
    }
}
