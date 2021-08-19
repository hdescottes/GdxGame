package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    protected boolean isOptionScreen;
    protected boolean isMenuNewGameScreen;
    protected boolean isMenuLoadGameScreen;
    private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();
    private final static String ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas";

    // MAP
    public final static int SQUARE_TILE_SIZE = 32;

    // ATLAS
    public TextureAtlas atlas;

    // ITEMS
    public static TextureAtlas ITEMS_TEXTURE_ATLAS = new TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH);

    // STATUS
    private final static String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
    private final static String STATUS_UI_SKIN_PATH = "skins/statusui.json";
    public static TextureAtlas STATUS_UI_TEXTURE_ATLAS = new TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH);
    public static Skin STATUS_UI_SKIN = new Skin(Gdx.files.internal(STATUS_UI_SKIN_PATH), STATUS_UI_TEXTURE_ATLAS);

    // IMAGES
    public Texture backgroundSheet;
    public Texture battleBackgroundMeadow;
    public Pixmap cursor;

    // BUTTON
    public TextureRegion[][] button;

    // FONT
    public BitmapFont pixel10;

    // SETTINGS
    public static Skin skin;

    // ENTITIES
    public Texture rabite;
    public Texture heroWalkUp;
    public Texture rabiteWalkDown;

    private static AssetManager assetManager = new AssetManager();

    public ResourceManager() {
        // ATLAS
        assetManager.load("asset/textures.atlas", TextureAtlas.class);

        // IMAGES
        assetManager.load("asset/background/natureBackground_frames_sheet.png", Texture.class);
        assetManager.load("asset/background/battleBackground_meadow.png", Texture.class);

        // ENTITIES
        assetManager.load("entities/enemies/rabite.png", Texture.class);

        assetManager.load("entities/hero/hero_1_walking_up.png", Texture.class);


        assetManager.load("entities/enemies/rabite_walking_down.png", Texture.class);

        assetManager.finishLoading();

        atlas = assetManager.get("asset/textures.atlas", TextureAtlas.class);

        // IMAGES
        backgroundSheet = assetManager.get("asset/background/natureBackground_frames_sheet.png");
        battleBackgroundMeadow = assetManager.get("asset/background/battleBackground_meadow.png");
        cursor = new Pixmap(Gdx.files.internal("asset/tool/cursor.png"));

        // BUTTON
        button = atlas.findRegion("play_button").split(80, 40);

        // FONT
        pixel10 = new BitmapFont(Gdx.files.internal("fonts/pixel.fnt"), atlas.findRegion("pixel"), false);

        // SETTINGS
        skin = new Skin(Gdx.files.internal("asset/data/uiskin.json"));

        // ENTITIES
        rabite = assetManager.get("entities/enemies/rabite.png");
        heroWalkUp = assetManager.get("entities/hero/hero_1_walking_up.png");
        rabiteWalkDown = assetManager.get("entities/enemies/rabite_walking_down.png");
    }

    public boolean isOptionScreen() {
        return isOptionScreen;
    }

    public void setOptionScreen(boolean optionScreen) {
        isOptionScreen = optionScreen;
    }

    public boolean isMenuNewGameScreen() {
        return isMenuNewGameScreen;
    }

    public void setMenuNewGameScreen(boolean menuNewGameScreen) {
        isMenuNewGameScreen = menuNewGameScreen;
    }

    public boolean isMenuLoadGameScreen() {
        return isMenuLoadGameScreen;
    }

    public void setMenuLoadGameScreen(boolean menuLoadGameScreen) {
        isMenuLoadGameScreen = menuLoadGameScreen;
    }

    public static void loadMapAsset(String mapFilenamePath) {
        if(mapFilenamePath == null || mapFilenamePath.isEmpty()) {
            return;
        }

        if(assetManager.isLoaded(mapFilenamePath)) {
            return;
        }

        //load asset
        if(filePathResolver.resolve(mapFilenamePath).exists() ) {
            assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));
            assetManager.load(mapFilenamePath, TiledMap.class);
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(mapFilenamePath);
            LOGGER.debug("Map loaded!: {}", mapFilenamePath);
        } else {
            LOGGER.debug("Map doesn't exist!: {}", mapFilenamePath );
        }
    }

    public static boolean isAssetLoaded(String fileName) {
        return assetManager.isLoaded(fileName);
    }

    public static TiledMap getMapAsset(String mapFilenamePath) {
        TiledMap map = null;

        // once the asset manager is done loading
        if(assetManager.isLoaded(mapFilenamePath)) {
            map = assetManager.get(mapFilenamePath, TiledMap.class);
        } else {
            LOGGER.debug("Map is not loaded: {}", mapFilenamePath);
        }

        return map;
    }

    public static void loadTextureAsset(String textureFilenamePath) {
        if(textureFilenamePath == null || textureFilenamePath.isEmpty()) {
            return;
        }

        if(assetManager.isLoaded(textureFilenamePath)) {
            return;
        }

        //load asset
        if(filePathResolver.resolve(textureFilenamePath).exists()) {
            assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
            assetManager.load(textureFilenamePath, Texture.class);
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(textureFilenamePath);
        } else {
            LOGGER.debug("Texture doesn't exist!: {}", textureFilenamePath);
        }
    }

    public static Texture getTextureAsset(String textureFilenamePath) {
        Texture texture = null;

        // once the asset manager is done loading
        if(assetManager.isLoaded(textureFilenamePath)) {
            texture = assetManager.get(textureFilenamePath,Texture.class);
        } else {
            LOGGER.debug("Texture is not loaded: {}", textureFilenamePath);
        }

        return texture;
    }

    public static void loadMusicAsset(String musicFilenamePath) {
        if(musicFilenamePath == null || musicFilenamePath.isEmpty()) {
            return;
        }

        if(assetManager.isLoaded(musicFilenamePath)) {
            return;
        }

        //load asset
        if(filePathResolver.resolve(musicFilenamePath).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(musicFilenamePath, Music.class);
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(musicFilenamePath);
            LOGGER.debug("Music loaded!: {}", musicFilenamePath);
        } else {
            LOGGER.debug("Music doesn't exist!: {}", musicFilenamePath);
        }
    }

    public static Music getMusicAsset(String musicFilenamePath) {
        Music music = null;

        // once the asset manager is done loading
        if(assetManager.isLoaded(musicFilenamePath)) {
            music = assetManager.get(musicFilenamePath, Music.class);
        } else {
            LOGGER.debug("Music is not loaded: {}", musicFilenamePath);
        }

        return music;
    }

    public void dispose() {
        assetManager.dispose();

        atlas.dispose();

        backgroundSheet.dispose();
        battleBackgroundMeadow.dispose();

        pixel10.dispose();

        rabite.dispose();
        rabiteWalkDown.dispose();
    }
}
