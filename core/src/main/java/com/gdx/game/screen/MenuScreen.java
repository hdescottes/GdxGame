package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gdx.game.GdxGame;
import com.gdx.game.Media;
import com.gdx.game.manager.AnimationManager;

public class MenuScreen extends AbstractScreen {

    private AssetManager assetManager = new AssetManager();
    private Table table;
    private Stage menuStage = new Stage();
    private Animation<TextureRegion> flowAnimation;
    private float stateTime;

    public MenuScreen(GdxGame gdxGame) {
        super(gdxGame);

        loadAssets();
        handleBackground();
        handlePlayButton();
    }

    private void loadAssets() {
        assetManager.load("asset/textures.atlas", TextureAtlas.class);
        assetManager.finishLoading();
    }

    private void handleBackground() {
        int nbRow = 7;
        int nbCol = 7;
        AnimationManager animationManager = new AnimationManager();

        Texture backgroundSheet = Media.backgroundSheet;

        TextureRegion[][] tmp = animationManager.setTextureRegionsDouble(backgroundSheet,
                backgroundSheet.getWidth() / nbCol,
                backgroundSheet.getHeight() / nbRow);

        TextureRegion[] flowFrames = new TextureRegion[nbCol * nbRow];
        int index = 0;
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbCol; j++) {
                flowFrames[index++] = tmp[i][j];
            }
        }

        flowAnimation = animationManager.setAnimation(flowFrames);
    }

    private void handlePlayButton() {
        table = new Table();

        TextureAtlas atlas = assetManager.get("asset/textures.atlas", TextureAtlas.class);
        TextureRegion[][] playButtons = atlas.findRegion("play_button").split(80, 40);

        BitmapFont pixel10 = new BitmapFont(Gdx.files.internal("fonts/pixel.fnt"), atlas.findRegion("pixel"), false);

        TextureRegionDrawable imageUp = new TextureRegionDrawable(playButtons[0][0]);
        TextureRegionDrawable imageDown = new TextureRegionDrawable(playButtons[1][0]);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(imageUp, imageDown, null, pixel10);
        TextButton playButton = new TextButton("Play", buttonStyle);
        playButton.getLabel().setColor(new Color(79 / 255.f, 79 / 255.f, 117 / 255.f, 1));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                gdxGame.setScreen(gdxGame.getGameScreen());
            }
        });

        table.add(playButton);
        table.setPosition((float) Gdx.graphics.getWidth()/2, (float) Gdx.graphics.getHeight()/2);

        menuStage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(menuStage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = flowAnimation.getKeyFrame(stateTime, true);

        gdxGame.getBatch().begin();
        gdxGame.getBatch().draw(currentFrame, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gdxGame.getBatch().end();

        menuStage.act(delta);
        menuStage.draw();
    }

    @Override
    public void dispose() {
        table.remove();
    }
}
