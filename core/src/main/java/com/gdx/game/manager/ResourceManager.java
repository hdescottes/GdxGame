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
    private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();

    // ATLAS
    public TextureAtlas atlas;

    // IMAGES
    public Texture backgroundSheet;
    public Texture battleBackgroundMeadow;
    public Pixmap cursor;

    // BUTTON
    public TextureRegion[][] button;

    // FONT
    public BitmapFont pixel10;

    // SETTINGS
    public Skin skin;

    // ENTITIES
    public Texture rabite;
    public Texture heroWalkUp;
    public Texture rabiteWalkDown;

    public static AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();

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
            LOGGER.debug("Map loaded!: " + mapFilenamePath);
        } else {
            LOGGER.debug("Map doesn't exist!: " + mapFilenamePath );
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
            LOGGER.debug("Map is not loaded: " + mapFilenamePath);
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
            LOGGER.debug("Texture doesn't exist!: " + textureFilenamePath);
        }
    }

    public static Texture getTextureAsset(String textureFilenamePath) {
        Texture texture = null;

        // once the asset manager is done loading
        if(assetManager.isLoaded(textureFilenamePath)) {
            texture = assetManager.get(textureFilenamePath,Texture.class);
        } else {
            LOGGER.debug("Texture is not loaded: " + textureFilenamePath);
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
            LOGGER.debug("Music loaded!: " + musicFilenamePath);
        } else {
            LOGGER.debug("Music doesn't exist!: " + musicFilenamePath);
        }
    }

    public static Music getMusicAsset(String musicFilenamePath) {
        Music music = null;

        // once the asset manager is done loading
        if(assetManager.isLoaded(musicFilenamePath)) {
            music = assetManager.get(musicFilenamePath, Music.class);
        } else {
            LOGGER.debug("Music is not loaded: " + musicFilenamePath);
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
