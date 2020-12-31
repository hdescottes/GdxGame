package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourceManager {

    protected boolean isOptionScreen;

    // ATLAS
    public TextureAtlas atlas;

    // IMAGES
    public Texture backgroundSheet;
    public Texture battleBackgroundMeadow;
    public Pixmap cursor;

    // MUSICS
    private Music music;
    public Music menuMusic;
    public Music gameMusic;
    public Music battleMusic;

    // BUTTON
    public TextureRegion[][] button;

    // FONT
    public BitmapFont pixel10;

    // SETTINGS
    public Skin skin;

    // ENTITIES
    public Texture hero;
    public Texture tree;
    public Texture rabite;

    public Texture birdPeck;
    public Texture birdWalk;
    public Texture birdFly;
    public Texture birdShadow;

    public Texture heroWalkUp;
    public Texture heroWalkDown;
    public Texture heroWalkRight;
    public Texture heroWalkLeft;
    public Texture heroShadow;

    public Texture rabiteWalkDown;

    // TILES
    public Texture grass01;
    public Texture grass02;
    public Texture grass03;
    public Texture grass04;
    public Texture grassLeft;
    public Texture grassRight;
    public Texture grassLeftUpperEdge;
    public Texture grassRightUpperEdge;
    public Texture grassTop;
    public Texture grassTopRight;
    public Texture grassTopLeft;
    public Texture water01;
    public Texture water02;
    public Texture water03;
    public Texture water04;
    public Texture cliff;

    public AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();

        // ATLAS
        assetManager.load("asset/textures.atlas", TextureAtlas.class);

        // IMAGES
        assetManager.load("asset/background/natureBackground_frames_sheet.png", Texture.class);
        assetManager.load("asset/background/battleBackground_meadow.png", Texture.class);

        // MUSICS
        assetManager.load("music/Rising_Sun.mp3", Music.class);
        assetManager.load("music/Dwarves'_Theme.mp3", Music.class);
        assetManager.load("music/Challenge.mp3", Music.class);

        // ENTITIES
        assetManager.load("entities/hero/hero.png", Texture.class);
        assetManager.load("entities/tree/tree.png", Texture.class);
        assetManager.load("entities/enemies/rabite.png", Texture.class);

        assetManager.load("entities/bird/bird_peck.png", Texture.class);
        assetManager.load("entities/bird/bird_walk.png", Texture.class);
        assetManager.load("entities/bird/bird_fly.png", Texture.class);
        assetManager.load("entities/bird/bird_shadow.png", Texture.class);

        assetManager.load("entities/hero/hero_1_walking_up.png", Texture.class);
        assetManager.load("entities/hero/hero_1_walking_down.png", Texture.class);
        assetManager.load("entities/hero/hero_1_walking_right.png", Texture.class);
        assetManager.load("entities/hero/hero_1_walking_left.png", Texture.class);
        assetManager.load("entities/hero/hero_shadow.png", Texture.class);

        assetManager.load("entities/enemies/rabite_walking_down.png", Texture.class);

        // TILES
        assetManager.load("8x8/grass/grass_01.png", Texture.class);
        assetManager.load("8x8/grass/grass_02.png", Texture.class);
        assetManager.load("8x8/grass/grass_03.png", Texture.class);
        assetManager.load("8x8/grass/grass_04.png", Texture.class);
        assetManager.load("8x8/grass/right_grass_edge.png", Texture.class);
        assetManager.load("8x8/grass/left_grass_edge.png", Texture.class);
        assetManager.load("8x8/grass/left_upper_edge.png", Texture.class);
        assetManager.load("8x8/grass/right_upper_edge.png", Texture.class);
        assetManager.load("8x8/grass/top.png", Texture.class);
        assetManager.load("8x8/grass/top_right.png", Texture.class);
        assetManager.load("8x8/grass/top_left.png", Texture.class);
        assetManager.load("8x8/water/water_01.png", Texture.class);
        assetManager.load("8x8/water/water_02.png", Texture.class);
        assetManager.load("8x8/water/water_03.png", Texture.class);
        assetManager.load("8x8/water/water_04.png", Texture.class);
        assetManager.load("8x8/cliff.png", Texture.class);

        assetManager.finishLoading();

        atlas = assetManager.get("asset/textures.atlas", TextureAtlas.class);

        // IMAGES
        backgroundSheet = assetManager.get("asset/background/natureBackground_frames_sheet.png");
        battleBackgroundMeadow = assetManager.get("asset/background/battleBackground_meadow.png");
        cursor = new Pixmap(Gdx.files.internal("asset/tool/cursor.png"));

        // MUSICS
        menuMusic = assetManager.get("music/Rising_Sun.mp3");
        gameMusic = assetManager.get("music/Dwarves'_Theme.mp3");
        battleMusic = assetManager.get("music/Challenge.mp3");

        // BUTTON
        button = atlas.findRegion("play_button").split(80, 40);

        // FONT
        pixel10 = new BitmapFont(Gdx.files.internal("fonts/pixel.fnt"), atlas.findRegion("pixel"), false);

        // SETTINGS
        skin = new Skin(Gdx.files.internal("asset/data/uiskin.json"));

        // ENTITIES
        hero = assetManager.get("entities/hero/hero.png");
        tree = assetManager.get("entities/tree/tree.png");
        rabite = assetManager.get("entities/enemies/rabite.png");

        birdPeck = assetManager.get("entities/bird/bird_peck.png");
        birdWalk = assetManager.get("entities/bird/bird_walk.png");
        birdFly  = assetManager.get("entities/bird/bird_fly.png");
        birdShadow = assetManager.get("entities/bird/bird_shadow.png");

        heroWalkUp = assetManager.get("entities/hero/hero_1_walking_up.png");
        heroWalkDown = assetManager.get("entities/hero/hero_1_walking_down.png");
        heroWalkRight  = assetManager.get("entities/hero/hero_1_walking_right.png");
        heroWalkLeft = assetManager.get("entities/hero/hero_1_walking_left.png");
        heroShadow = assetManager.get("entities/hero/hero_shadow.png");

        rabiteWalkDown = assetManager.get("entities/enemies/rabite_walking_down.png");

        // TILES
        grass01 = assetManager.get("8x8/grass/grass_01.png");
        grass02 = assetManager.get("8x8/grass/grass_02.png");
        grass03 = assetManager.get("8x8/grass/grass_03.png");
        grass04 = assetManager.get("8x8/grass/grass_04.png");
        grassLeft = assetManager.get("8x8/grass/right_grass_edge.png");
        grassRight = assetManager.get("8x8/grass/left_grass_edge.png");
        grassLeftUpperEdge = assetManager.get("8x8/grass/left_upper_edge.png");
        grassRightUpperEdge = assetManager.get("8x8/grass/right_upper_edge.png");
        grassTop = assetManager.get("8x8/grass/top.png");
        grassTopRight = assetManager.get("8x8/grass/top_right.png");
        grassTopLeft = assetManager.get("8x8/grass/top_left.png");
        water01 = assetManager.get("8x8/water/water_01.png");
        water02 = assetManager.get("8x8/water/water_02.png");
        water03 = assetManager.get("8x8/water/water_03.png");
        water04 = assetManager.get("8x8/water/water_04.png");
        cliff = assetManager.get("8x8/cliff.png");
    }

    public boolean isOptionScreen() {
        return isOptionScreen;
    }

    public void setOptionScreen(boolean optionScreen) {
        isOptionScreen = optionScreen;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public void playMusic(String musicPath) {
        if (music == null) {
            addMusic(musicPath);
        }

        if (music != assetManager.get(musicPath, Music.class)) {
            music.stop();
            music.dispose();
            addMusic(musicPath);
        }
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
            music.dispose();
            setMusic(null);
        }
    }

    private void addMusic(String musicPath) {
        music = assetManager.get(musicPath, Music.class);
        music.setLooping(true);
        music.play();
        setMusic(music);
    }

    public void dispose() {
        assetManager.dispose();

        atlas.dispose();

        backgroundSheet.dispose();
        battleBackgroundMeadow.dispose();

        menuMusic.dispose();
        gameMusic.dispose();

        pixel10.dispose();

        hero.dispose();
        tree.dispose();
        rabite.dispose();

        birdPeck.dispose();
        birdWalk.dispose();
        birdFly.dispose();
        birdShadow.dispose();

        heroWalkUp.dispose();
        heroWalkDown.dispose();
        heroWalkRight.dispose();
        heroWalkLeft.dispose();
        heroShadow.dispose();

        rabiteWalkDown.dispose();

        grass01.dispose();
        grass02.dispose();
        grass03.dispose();
        grass04.dispose();
        grassLeft.dispose();
        grassRight.dispose();
        grassLeftUpperEdge.dispose();
        grassRightUpperEdge.dispose();
        grassTop.dispose();
        grassTopRight.dispose();
        grassTopLeft.dispose();
        water01.dispose();
        water02.dispose();
        water03.dispose();
        water04.dispose();
        cliff.dispose();
    }
}
