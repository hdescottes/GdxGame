package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.GdxGame;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.MENU_THEME;

public class MenuNewGameScreen extends BaseScreen {

    private Table newTable;
    private Table topTable;
    private Table bottomTable;
    private Stage newStage = new Stage();
    private TextField profileText;
    private Dialog overwriteDialog;
    private BaseScreen previousScreen;
    private float stateTime;

    public MenuNewGameScreen(GdxGame gdxGame, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        this.previousScreen = previousScreen;
        super.musicTheme = MENU_THEME;

        resourceManager.setMenuNewGameScreen(true);

        Label profileName = new Label("Enter Profile Name: ", ResourceManager.skin);
        profileText = new TextField("default", ResourceManager.skin);
        profileText.setMaxLength(20);

        newTable = createTable();

        topTable = createTable();
        topTable.setFillParent(true);
        topTable.add(profileName).center();
        topTable.add(profileText).center();

        bottomTable = createTable();
        bottomTable.setWidth(Gdx.graphics.getWidth());
        bottomTable.setHeight(Gdx.graphics.getHeight()/2f);
        bottomTable.center();

        createOverwriteDialog();
        handlePlayButton();
        handleNewBackButton();
        handleOverwriteButton();
        handleCancelButton();
    }

    private void createOverwriteDialog() {
        overwriteDialog = new Dialog("Overwrite?", ResourceManager.skin);
        Label overwriteLabel = new Label("Overwrite existing profile name?", ResourceManager.skin);

        overwriteDialog.setKeepWithinStage(true);
        overwriteDialog.setModal(true);
        overwriteDialog.setMovable(false);
        overwriteDialog.text(overwriteLabel);
        overwriteDialog.row();
    }

    private void handlePlayButton() {
        createButton("Play", 0, newTable.getHeight()/9, newTable);

        Actor playButton = newTable.getCells().get(0).getActor();
        bottomTable.add(playButton).padRight(50);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                previousScreen.dispose();

                String messageText = profileText.getText();
                //check to see if the current profile matches one that already exists
                boolean exists = ProfileManager.getInstance().doesProfileExist(messageText);

                if(exists) {
                    //Pop up dialog for Overwrite
                    overwriteDialog.show(newStage);
                } else {
                    ProfileManager.getInstance().writeProfileToStorage(messageText,"",false);
                    ProfileManager.getInstance().setCurrentProfile(messageText);
                    ProfileManager.getInstance().setIsNewProfile(true);

                    ArrayList<TransitionEffect> effects = new ArrayList<>();
                    effects.add(new FadeOutTransitionEffect(1f));
                    //effects.add(new FadeInTransitionEffect(1f)); TODO: Issue with fadein effect
                    setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getCharacterSelectionScreen(), effects);
                }
            }
        });
    }

    private void handleNewBackButton() {
        createButton("Back",0, newTable.getHeight()/5, newTable);

        Actor backButton = newTable.getCells().get(1).getActor();
        bottomTable.add(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), previousScreen, new ArrayList<>());
            }
        });
    }

    private void handleOverwriteButton() {
        createButton("Overwrite",0, newTable.getHeight()/5, newTable);

        Actor overwriteButton = newTable.getCells().get(2).getActor();
        overwriteDialog.button((Button) overwriteButton).bottom().left();
        overwriteButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                String messageText = profileText.getText();
                ProfileManager.getInstance().writeProfileToStorage(messageText, "", true);
                ProfileManager.getInstance().setCurrentProfile(messageText);
                ProfileManager.getInstance().setIsNewProfile(true);
                overwriteDialog.hide();

                ArrayList<TransitionEffect> effects = new ArrayList<>();
                effects.add(new FadeOutTransitionEffect(1f));
                //effects.add(new FadeInTransitionEffect(1f)); TODO: Issue with fadein effect
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getCharacterSelectionScreen(), effects);
            }

        });
    }

    private void handleCancelButton() {
        createButton("Cancel",0, newTable.getHeight()/5, newTable);

        Actor cancelButton = newTable.getCells().get(3).getActor();
        overwriteDialog.button((Button) cancelButton).bottom().right();
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                overwriteDialog.hide();
            }
        });
    }

    @Override
    public void show() {
        newStage.addActor(newTable);
        newStage.addActor(topTable);
        newStage.addActor(bottomTable);
        Gdx.input.setInputProcessor(newStage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();

        if(previousScreen != null) {
            previousScreen.render(stateTime);
        }

        show();
        newStage.act(delta);
        newStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        newTable.remove();
    }

    @Override
    public void hide() {
        resourceManager.setMenuNewGameScreen(false);
    }
}
