package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioManager;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.component.InputComponent;
import com.gdx.game.manager.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.gdx.game.common.Constats.FULL_CONTROLS_SETTINGS_PATH;
import static com.gdx.game.common.Constats.PARTIAL_CONTROLS_SETTINGS_PATH;
import static com.gdx.game.common.DefaultControlsMap.DEFAULT_CONTROLS;
import static com.gdx.game.common.UtilityClass.getFirstKeyByValue;
import static com.gdx.game.common.UtilityClass.mapInverter;
import static com.gdx.game.manager.ResourceManager.skin;

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

    private Map<String, String> playerControlsNew = new HashMap<>();

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
        if (previousScreen.getClass() != MenuScreen.class) {
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
                handleControlSettings();
                handleControlBackButton();
            }
        });
    }

    private void handleControlSettings() {

        Json jsonObject = new Json();

        try {
            playerControlsNew = jsonObject.fromJson(HashMap.class, Gdx.files.internal(PARTIAL_CONTROLS_SETTINGS_PATH));
        }catch (SerializationException se){
            playerControlsNew = DEFAULT_CONTROLS;
        }


        playerControlsNew = mapInverter(playerControlsNew);

        Label upLabel = new Label(InputComponent.Keys.UP.name(), skin);
        TextField upText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.UP.name()))), skin);
        Label downLabel = new Label(InputComponent.Keys.DOWN.name(), skin);
        TextField downText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.DOWN.name()))), skin);
        Label leftLabel = new Label(InputComponent.Keys.LEFT.name(), skin);
        TextField leftText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.LEFT.name()))), skin);
        Label rightLabel = new Label(InputComponent.Keys.RIGHT.name(), skin);
        TextField rightText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.RIGHT.name()))), skin);
        Label interactLabel = new Label(InputComponent.Keys.INTERACT.name(), skin);
        TextField interactText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.INTERACT.name()))), skin);
        Label optionLabel = new Label(InputComponent.Keys.OPTION.name(), skin);
        TextField optionText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.OPTION.name()))), skin);
        Label quitLabel = new Label(InputComponent.Keys.QUIT.name(), skin);
        TextField quitText = new TextField(
                Input.Keys.toString(Integer.parseInt(playerControlsNew.get(InputComponent.Keys.QUIT.name()))), skin);

        playerControlsNew = mapInverter(playerControlsNew);

        downText.setMaxLength(1);
        upText.setMaxLength(1);
        leftText.setMaxLength(1);
        rightText.setMaxLength(1);
        interactText.setMaxLength(1);
        optionText.setMaxLength(1);
        quitText.setMaxLength(1);
        
        downText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.DOWN.name());

                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.DOWN.name());
                downText.setMaxLength(Input.Keys.toString(keycode).length());
                downText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        upText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.UP.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.UP.name());
                upText.setMaxLength(Input.Keys.toString(keycode).length());
                upText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        leftText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.LEFT.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.LEFT.name());
                leftText.setMaxLength(Input.Keys.toString(keycode).length());
                leftText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        rightText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.RIGHT.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.RIGHT.name());
                rightText.setMaxLength(Input.Keys.toString(keycode).length());
                rightText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        interactText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.INTERACT.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.INTERACT.name());
                interactText.setMaxLength(Input.Keys.toString(keycode).length());
                interactText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        optionText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.OPTION.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.OPTION.name());
                optionText.setMaxLength(Input.Keys.toString(keycode).length());
                optionText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        quitText.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                Optional<String> keyToRemove = getFirstKeyByValue(playerControlsNew, InputComponent.Keys.QUIT.name());
                keyToRemove.ifPresent(s -> playerControlsNew.remove(s));

                playerControlsNew.put(String.valueOf(keycode), InputComponent.Keys.QUIT.name());
                quitText.setMaxLength(Input.Keys.toString(keycode).length());
                quitText.setText(Input.Keys.toString(keycode));

                return super.keyDown(event, keycode);
            }
        });

        controlTable.add(upLabel);
        controlTable.add(upText);
        controlTable.row();
        controlTable.add(downLabel);
        controlTable.add(downText);
        controlTable.row();
        controlTable.add(leftLabel);
        controlTable.add(leftText);
        controlTable.row();
        controlTable.add(rightLabel);
        controlTable.add(rightText);
        controlTable.row();
        controlTable.add(interactLabel);
        controlTable.add(interactText);
        controlTable.row();
        controlTable.add(optionLabel);
        controlTable.add(optionText);
        controlTable.row();
        controlTable.add(quitLabel);
        controlTable.add(quitText);
        controlTable.row();

    }

    private void handleControlBackButton() {
        createButton("Back",0, controlTable.getHeight()/5, controlTable);

        Actor backButton = controlTable.getCells().get(controlTable.getCells().size - 1).getActor();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {

                Json json = new Json();

                FileHandle commandsFile = Gdx.files.local(FULL_CONTROLS_SETTINGS_PATH);
                commandsFile.writeString(json.prettyPrint(playerControlsNew), false);

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
        Label musicLabel = new Label("MUSIC", skin);
        musicLabel.setAlignment(Align.left);
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(gdxGame.getPreferenceManager().getMusicVolume());
        musicSlider.addListener(event -> {
            gdxGame.getPreferenceManager().setMusicVolume(musicSlider.getValue());
            AudioManager.getInstance().getCurrentMusic().setVolume(gdxGame.getPreferenceManager().getMusicVolume());
            return false;
        });
        CheckBox musicCheckbox = new CheckBox("Enable Music", skin);
        musicCheckbox.setChecked(gdxGame.getPreferenceManager().isMusicEnabled());
        musicCheckbox.addListener(event -> {
            gdxGame.getPreferenceManager().setMusicEnabled(musicCheckbox.isChecked());
            notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, previousScreen.getMusicTheme());
            return false;
        });

        Label soundLabel = new Label("SOUND", skin);
        soundLabel.setAlignment(Align.left);
        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
        soundSlider.setValue(gdxGame.getPreferenceManager().getSoundVolume());
        soundSlider.addListener(event -> {
            gdxGame.getPreferenceManager().setSoundVolume(soundSlider.getValue());
            return false;
        });
        CheckBox soundCheckbox = new CheckBox("Enable Sound", skin);
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
        if (controlClickListener) {
            controlStage.addActor(controlTable);
            Gdx.input.setInputProcessor(controlStage);
        } else if (musicClickListener) {
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

        if (controlClickListener) {
            show();
            controlStage.act(delta);
            controlStage.draw();
        } else if (musicClickListener) {
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
