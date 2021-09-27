package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxGame;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.MENU_THEME;

public class MenuLoadGameScreen extends BaseScreen {

    private Table loadTable;
    private Table topTable;
    private Table bottomTable;
    private Stage loadStage = new Stage();
    private BaseScreen previousScreen;
    private List listItems;
    private float stateTime;

    public MenuLoadGameScreen(GdxGame gdxGame, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        this.previousScreen = previousScreen;
        super.musicTheme = MENU_THEME;

        resourceManager.setMenuLoadGameScreen(true);

        loadTable = createTable();

        topTable = createTable();
        topTable.center();
        topTable.setFillParent(true);

        bottomTable = createTable();
        bottomTable.setWidth(Gdx.graphics.getWidth());
        bottomTable.setHeight(Gdx.graphics.getHeight()/2f);
        bottomTable.center();

        createProfileList();
        handleLoadButton();
        handleLoadBackButton();
    }

    private void createProfileList() {
        ProfileManager.getInstance().storeAllProfiles();
        listItems = new List(ResourceManager.skin);
        Array<String> list = ProfileManager.getInstance().getProfileList();
        listItems.setItems(list);
        ScrollPane scrollPane = new ScrollPane(listItems);

        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollbarsOnTop(true);

        topTable.add(scrollPane).center();
    }

    private void handleLoadButton() {
        createButton("Play",0, loadTable.getHeight()/9, loadTable);

        Actor loadButton = loadTable.getCells().get(0).getActor();
        topTable.padBottom(loadButton.getHeight());
        bottomTable.add(loadButton).padRight(50);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                previousScreen.dispose();

                if(listItems.getSelected() == null) {
                    return;
                }
                String fileName = listItems.getSelected().toString();
                if(fileName != null && !fileName.isEmpty()) {
                    FileHandle file = ProfileManager.getInstance().getProfileFile(fileName);
                    if(file != null) {
                        ProfileManager.getInstance().setCurrentProfile(fileName);
                        ProfileManager.getInstance().loadProfile();
                        gdxGame.setGameScreen(new GameScreen(gdxGame, resourceManager));

                        ArrayList<TransitionEffect> effects = new ArrayList<>();
                        effects.add(new FadeOutTransitionEffect(1f));
                        //effects.add(new FadeInTransitionEffect(1f)); TODO: Issue with fadein effect
                        setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), effects);
                    }
                }
            }
        });

    }

    private void handleLoadBackButton() {
        createButton("Back",0, loadTable.getHeight()/5, loadTable);

        Actor backButton = loadTable.getCells().get(1).getActor();
        bottomTable.add(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), previousScreen, new ArrayList<>());
            }
        });
    }

    @Override
    public void show() {
        loadStage.addActor(loadTable);
        loadStage.addActor(topTable);
        loadStage.addActor(bottomTable);
        Gdx.input.setInputProcessor(loadStage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();

        if(previousScreen != null) {
            previousScreen.render(stateTime);
        }

        show();
        loadStage.act(delta);
        loadStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        loadTable.remove();
        topTable.remove();
        bottomTable.remove();
    }

    @Override
    public void hide() {
        resourceManager.setMenuLoadGameScreen(false);
    }
}
