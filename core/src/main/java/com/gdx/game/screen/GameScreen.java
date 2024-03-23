package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.SerializationException;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioManager;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.camera.CameraStyles;
import com.gdx.game.component.Component;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.entities.player.PlayerInputComponent;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gdx.game.common.Constats.FULL_CONTROLS_SETTINGS_PATH;
import static com.gdx.game.common.Constats.PARTIAL_CONTROLS_SETTINGS_PATH;
import static com.gdx.game.common.DefaultControlsMap.DEFAULT_CONTROLS;
import static com.gdx.game.component.InputComponent.playerControls;
import static com.gdx.game.component.InputComponent.setPlayerControlMapFromJsonControlsMap;

public class GameScreen extends BaseScreen implements ComponentObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameScreen.class);

    public static class VIEWPORT {
        private static float viewportWidth;
        private static float viewportHeight;
        private static float virtualWidth;
        private static float virtualHeight;
        private static float physicalWidth;
        private static float physicalHeight;
        private static float aspectRatio;
    }

    public enum GameState {
        SAVING,
        LOADING,
        RUNNING,
        PAUSED,
        GAME_OVER
    }
    private static GameState gameState;

    protected OrthogonalTiledMapRenderer mapRenderer = null;
    protected MapManager mapManager;
    protected OrthographicCamera camera;
    protected OrthographicCamera hudCamera;

    private final Json json;
    private final GdxGame game;
    private final InputMultiplexer multiplexer;

    private final Entity player;
    private final PlayerHUD playerHUD;

    private float startX;
    private float startY;
    private float levelWidth;
    private float levelHeight;
    private float endX;
    private float endY;

    private AudioObserver.AudioTypeEvent musicTheme;

    public GameScreen(GdxGame gdxGame, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        game = gdxGame;
        mapManager = new MapManager();
        json = new Json();

        setGameState(GameState.RUNNING);

        //_camera setup
        setupViewport(15, 15);

        //get the current size
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        player = EntityFactory.getInstance().getEntity(ProfileManager.getInstance().getProperty("playerCharacter", EntityFactory.EntityType.class));
        player.registerObserver(this);

        //initialize controls
        HashMap<String, String> controlMap;
        
        if(playerControls.isEmpty()){
            try {
                controlMap = json.fromJson(HashMap.class, Gdx.files.local(PARTIAL_CONTROLS_SETTINGS_PATH));

                if (DEFAULT_CONTROLS.size() != controlMap.size()){
                    throw new SerializationException("Not valid control map");
                }
            } catch (SerializationException se) {LOGGER.error(se.getMessage());
                // if I can not read the file , so use the default controls binding and save it
                controlMap = DEFAULT_CONTROLS;

                FileHandle commandsFile = Gdx.files.local(FULL_CONTROLS_SETTINGS_PATH);
                commandsFile.writeString(json.prettyPrint(controlMap), false);
            }

            setPlayerControlMapFromJsonControlsMap(controlMap);
        }

        mapManager.setPlayer(player);
        mapManager.setCamera(camera);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);

        playerHUD = new PlayerHUD(hudCamera, player, mapManager);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(playerHUD.getStage());
        multiplexer.addProcessor(player.getInputProcessor());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public AudioObserver.AudioTypeEvent getMusicTheme() {
        return musicTheme;
    }

    @Override
    public void show() {
        ProfileManager.getInstance().addObserver(mapManager);
        ProfileManager.getInstance().addObserver(playerHUD);

        setGameState(GameState.LOADING);
        Gdx.input.setInputProcessor(multiplexer);

        if (mapRenderer == null) {
            mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentTiledMap(), Map.UNIT_SCALE);
        }
    }

    @Override
    public void hide() {
        if (gameState != GameState.GAME_OVER) {
            setGameState(GameState.SAVING);
        }

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (gameState == GameState.PAUSED) {
            player.updateInput(delta);
            playerHUD.render(delta);
            return;
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        mapRenderer.getBatch().enableBlending();
        mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (mapManager.hasMapChanged()) {
            mapRenderer.setMap(mapManager.getCurrentTiledMap());
            player.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(mapManager.getPlayerStartUnitScaled()));

            camera.position.set(mapManager.getPlayerStartUnitScaled().x, mapManager.getPlayerStartUnitScaled().y, 0f);
            camera.update();

            playerHUD.updateEntityObservers();

            mapManager.setMapChanged(false);
        }

        mapRenderer.render();
        mapManager.updateCurrentMapEntities(mapManager, mapRenderer.getBatch(), delta);
        player.update(mapManager, mapRenderer.getBatch(), delta);

        startX = camera.viewportWidth / 2;
        startY = camera.viewportHeight / 2;
        levelWidth = mapManager.getCurrentTiledMap().getProperties().get("width", Integer.class);
        levelHeight = mapManager.getCurrentTiledMap().getProperties().get("height", Integer.class);
        endX = levelWidth * ResourceManager.SQUARE_TILE_SIZE * Map.UNIT_SCALE - startX * 2;
        endY = levelHeight * ResourceManager.SQUARE_TILE_SIZE * Map.UNIT_SCALE - startY * 2;
        CameraStyles.boundaries(camera, startX, startY, endX, endY);

        playerHUD.render(delta);

        musicTheme = MapFactory.getMapTable().get(mapManager.getCurrentMapType()).getMusicTheme();
        AudioManager.getInstance().setCurrentMusic(ResourceManager.getMusicAsset(musicTheme.getValue()));
    }

    @Override
    public void onNotify(String value, ComponentEvent event) {
        switch (event) {
            case START_BATTLE -> {
                setGameState(GameState.SAVING);
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), new BattleScreen(game, playerHUD, mapManager, resourceManager), new ArrayList<>());
                PlayerInputComponent.clear();
            }
            case OPTION_INPUT -> {
                Image screenShot = new Image(ScreenUtils.getFrameBufferTexture());
                game.setScreen(new OptionScreen(game, (BaseScreen) game.getScreen(), screenShot, resourceManager));
            }
            default -> {
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        setupViewport(15, 15);
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
        playerHUD.resize((int) VIEWPORT.physicalWidth, (int) VIEWPORT.physicalHeight);
    }

    @Override
    public void pause() {
        setGameState(GameState.SAVING);
        playerHUD.pause();
    }

    @Override
    public void resume() {
        setGameState(GameState.LOADING);
        playerHUD.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (player != null) {
            player.unregisterObservers();
            player.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        AudioManager.getInstance().dispose();
        MapFactory.clearCache();
    }

    public static GameState getGameState() {
        return gameState;
    }

    public static void setGameState(GameState state) {
        switch (state) {
            case LOADING -> {
                ProfileManager.getInstance().loadProfile();
                gameState = GameState.RUNNING;
            }
            case SAVING -> {
                ProfileManager.getInstance().saveProfile();
                gameState = GameState.PAUSED;
            }
            case PAUSED -> {
                if (gameState == GameState.PAUSED) {
                    gameState = GameState.RUNNING;
                } else if (gameState == GameState.RUNNING) {
                    gameState = GameState.PAUSED;
                }
            }
            case GAME_OVER -> gameState = GameState.GAME_OVER;
            default -> gameState = GameState.RUNNING;
        }

    }

    private static void setupViewport(int width, int height) {
        //Make the viewport a percentage of the total display area
        VIEWPORT.virtualWidth = width;
        VIEWPORT.virtualHeight = height;

        //Current viewport dimensions
        VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
        VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

        //pixel dimensions of display
        VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
        VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

        //aspect ratio for current viewport
        VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

        //update viewport if there could be skewing
        if (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
            //Letterbox left and right
            VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
            VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
        } else {
            //letterbox above and below
            VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
            VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
        }

        LOGGER.debug("WorldRenderer: virtual: ({},{})", VIEWPORT.virtualWidth, VIEWPORT.virtualHeight);
        LOGGER.debug("WorldRenderer: viewport: ({},{})", VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
        LOGGER.debug("WorldRenderer: physical: ({},{})", VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
    }
}
