package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.GdxGame;
import com.gdx.game.manager.AnimationManager;
import com.gdx.game.manager.RessourceManager;
import com.gdx.game.screen.transition.effects.FadeInTransitionEffect;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

public class MenuScreen extends BaseScreen {

    private Table table;
    private Stage menuStage = new Stage();
    private Animation<TextureRegion> flowAnimation;
    private float stateTime;

    public MenuScreen(GdxGame gdxGame, RessourceManager ressourceManager) {
        super(gdxGame, ressourceManager);

        createTable();
        handleBackground();
        handlePlayButton();
        handleOptionButton();
    }

    private void createTable() {
        table = new Table();
        table.setBounds(0,0, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    private void handleBackground() {
        int nbRow = 7;
        int nbCol = 7;
        AnimationManager animationManager = new AnimationManager();

        Texture backgroundSheet = ressourceManager.backgroundSheet;

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

    private void handleOptionButton() {
        createButton("Options", 0, table.getHeight()/10, table);

        Actor optionButton = table.getCells().get(1).getActor();
        optionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                ArrayList<TransitionEffect> effects = new ArrayList<>();
                effects.add(new FadeInTransitionEffect(1f));
                setScreenWithTransition(gdxGame.getScreen(), new OptionScreen(gdxGame, gdxGame.getScreen(), ressourceManager), effects);
            }
        });
    }

    private void handlePlayButton() {
        createButton("Play", 0, table.getHeight()/9, table);

        Actor playButton = table.getCells().get(0).getActor();
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                ArrayList<TransitionEffect> effects = new ArrayList<>();
                effects.add(new FadeOutTransitionEffect(1f));
                effects.add(new FadeInTransitionEffect(1f));
                setScreenWithTransition(gdxGame.getScreen(), gdxGame.getGameScreen(), effects);
            }
        });
    }

    @Override
    public void show() {
        menuStage.addActor(table);
        Gdx.input.setInputProcessor(menuStage);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(ressourceManager.cursor, 0, 0));
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
        super.dispose();
        table.remove();
    }
}
