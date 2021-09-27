package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.component.Component;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;

import java.util.ArrayList;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.GAME_OVER_THEME;

public class GameOverScreen extends BaseScreen {

    private Table gameOverTable;
    private Stage gameOverStage = new Stage();

    public GameOverScreen(GdxGame gdxGame, MapManager mapManager, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = GAME_OVER_THEME;

        gameOverTable = createTable();
        gameOverTable.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", ResourceManager.skin);
        gameOverLabel.setAlignment(Align.center);
        TextButton continueButton = new TextButton("Continue", ResourceManager.skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent input, float x, float y) {
                mapManager.getPlayer().setEntityEncounteredType(null);
                mapManager.getPlayer().sendMessage(Component.MESSAGE.RESET_POSITION);
                int hpMax = ProfileManager.getInstance().getProperty("currentPlayerHPMax", Integer.class);

                ProfileManager.getInstance().setProperty("currentPlayerHP", (int) Math.round(hpMax * 0.1));
                ProfileManager.getInstance().saveProfile();
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getMenuScreen(), new ArrayList<>());
            }
        });

        gameOverTable.add(gameOverLabel);
        gameOverTable.row();
        gameOverTable.add(continueButton).padTop(5);
        gameOverTable.pack();
    }

    @Override
    public void show() {
        gameOverStage.addActor(gameOverTable);
        Gdx.input.setInputProcessor(gameOverStage);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, GAME_OVER_THEME);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, GAME_OVER_THEME);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        gdxGame.getBatch().begin();
        //gdxGame.getBatch().draw();
        gdxGame.getBatch().end();

        gameOverStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameOverTable.remove();
    }
}
