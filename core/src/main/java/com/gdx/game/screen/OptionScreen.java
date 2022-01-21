package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioManager;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.manager.ResourceManager;

import java.util.ArrayList;

public class OptionScreen extends BaseScreen {

    private Table optionTable;
    private Table musicTable;
    private Table controlTable;
    private Stage optionStage = new Stage();
    private Stage musicStage = new Stage();
    private Stage controlStage = new Stage();
    private Stage backgroundStage = new Stage();
    private BaseScreen previousScreen;
    private Image previousScreenAsImg;
    private boolean musicClickListener;
    private boolean controlClickListener;
    private float stateTime;

    private VfxManager vfxManager;
    private GaussianBlurEffect vfxEffect;

    public OptionScreen(GdxGame gdxGame, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        this.previousScreen = previousScreen;

        resourceManager.setOptionScreen(true);
        loadContents();
    }

    public OptionScreen(GdxGame gdxGame, BaseScreen previousScreen, Image previousScreenAsImg, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        this.previousScreen = previousScreen;
        this.previousScreenAsImg = previousScreenAsImg;

        resourceManager.setOptionScreen(true);
        loadContents();
    }

    private void loadContents() {
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxEffect = new GaussianBlurEffect();
        if(previousScreen.getClass() != MenuScreen.class) {
            vfxManager.addEffect(vfxEffect);
        }

        optionTable = createTable();
        handleControlButton();
        handleMusicButton();
        handleBackButton();
    }

    private void handleControlButton() {
        createButton("Control",0, optionTable.getHeight()/10, optionTable);

        Actor controlButton = optionTable.getCells().get(0).getActor();
        controlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                controlClickListener = true;
                controlTable = createTable();
                handleControlBackButton();
            }
        });
    }

    private void handleControlBackButton() {
        createButton("Back",0, controlTable.getHeight()/5, controlTable);

        Actor backButton = controlTable.getCells().get(0).getActor();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                controlClickListener = false;
            }
        });
    }

    private void handleMusicButton() {
        createButton("Music",0, optionTable.getHeight()/15, optionTable);

        Actor musicButton = optionTable.getCells().get(1).getActor();
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                musicClickListener = true;
                musicTable = createTable();
                handleMusicSettings();
                handleMusicBackButton();
            }
        });
    }

    private void handleMusicSettings() {
        Label musicLabel = new Label("MUSIC", resourceManager.skin);
        musicLabel.setAlignment(Align.left);
        Slider musicSlider = new Slider(0, 1, 0.01f, false, resourceManager.skin);
        musicSlider.setValue(gdxGame.getPreferenceManager().getMusicVolume());
        musicSlider.addListener(event -> {
            gdxGame.getPreferenceManager().setMusicVolume(musicSlider.getValue());
            AudioManager.getInstance().getCurrentMusic().setVolume(gdxGame.getPreferenceManager().getMusicVolume());
            return false;
        });
        CheckBox musicCheckbox = new CheckBox("Enable Music", resourceManager.skin);
        musicCheckbox.setChecked(gdxGame.getPreferenceManager().isMusicEnabled());
        musicCheckbox.addListener(event -> {
            gdxGame.getPreferenceManager().setMusicEnabled(musicCheckbox.isChecked());
            notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, previousScreen.getMusicTheme());
            return false;
        });

        Label soundLabel = new Label("SOUND", resourceManager.skin);
        soundLabel.setAlignment(Align.left);
        Slider soundSlider = new Slider(0, 1, 0.01f, false, resourceManager.skin);
        soundSlider.setValue(gdxGame.getPreferenceManager().getSoundVolume());
        soundSlider.addListener(event -> {
            gdxGame.getPreferenceManager().setSoundVolume(soundSlider.getValue());
            return false;
        });
        CheckBox soundCheckbox = new CheckBox("Enable Sound", resourceManager.skin);
        soundCheckbox.setChecked(gdxGame.getPreferenceManager().isSoundEffectsEnabled());
        soundCheckbox.addListener(event -> {
            boolean enabled = soundCheckbox.isChecked();
            gdxGame.getPreferenceManager().setSoundEffectsEnabled(enabled);
            return false;
        });

        musicTable.row();
        musicTable.add(musicLabel).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();
        musicTable.add(musicSlider).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();
        musicTable.add(musicCheckbox).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();
        musicTable.add(soundLabel).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();
        musicTable.add(soundSlider).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();
        musicTable.add(soundCheckbox).padLeft(0).padTop(musicTable.getHeight()/20);
        musicTable.row();

    }

    private void handleMusicBackButton() {
        createButton("Back",0, musicTable.getHeight()/20, musicTable);

        Actor backButton = musicTable.getCells().get(6).getActor();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                musicClickListener = false;
            }
        });
    }

    private void handleBackButton() {
        createButton("Back",0, optionTable.getHeight()/5, optionTable);

        Actor backButton = optionTable.getCells().get(2).getActor();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), previousScreen, new ArrayList<>());
            }
        });
    }

    @Override
    public void show() {
        if(controlClickListener) {
            controlStage.addActor(controlTable);
            Gdx.input.setInputProcessor(controlStage);
        } else if(musicClickListener) {
            musicStage.addActor(musicTable);
            Gdx.input.setInputProcessor(musicStage);
        } else {
            controlStage.clear();
            musicStage.clear();
            optionStage.addActor(optionTable);
            Gdx.input.setInputProcessor(optionStage);
        }
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        if(previousScreen != null && previousScreenAsImg == null) {
            previousScreen.render(stateTime);
        }
        if(previousScreenAsImg != null) {
            backgroundStage.addActor(previousScreenAsImg);
            backgroundStage.draw();
        }

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        if(controlClickListener) {
            show();
            controlStage.act(delta);
            controlStage.draw();
        } else if(musicClickListener) {
            show();
            musicStage.act(delta);
            musicStage.draw();
        } else {
            show();
            optionStage.act(delta);
            optionStage.draw();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        optionTable.remove();
        musicTable.remove();
        controlTable.remove();
        vfxManager.dispose();
        vfxEffect.dispose();
    }

    @Override
    public void hide() {
        resourceManager.setOptionScreen(false);
    }
}
