package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Media {

    // ENTITIES
    public static final Texture hero = new Texture("entities/hero/hero.png");
    public static final Texture tree = new Texture("entities/tree/tree.png");

    public static final Texture birdPeck = new Texture("entities/bird/bird_peck.png");
    public static final Texture birdWalk = new Texture("entities/bird/bird_walk.png");
    public static final Texture birdFly  = new Texture("entities/bird/bird_fly.png");
    public static final Texture birdShadow = new Texture("entities/bird/bird_shadow.png");

    // ANIMATIONS
    protected static final TextureRegion[] birdWalkFrames = TextureRegion.split(birdWalk, 10, 9)[0];
    protected static final TextureRegion[] birdPeckFrames = TextureRegion.split(birdPeck, 10, 9)[0];
    protected static final TextureRegion[] birdFlyFrames = TextureRegion.split(birdFly, 10, 9)[0];

    public static final Animation<TextureRegion> birdWalkAnim = new Animation<>(.1f, birdWalkFrames);
    public static final Animation<TextureRegion> birdPeckAnim = new Animation<>(.1f, birdPeckFrames);
    public static final Animation<TextureRegion> birdFlyAnim = new Animation<>(.1f, birdFlyFrames);

    // TILES
    public static final Texture grass01 = new Texture("8x8/grass/grass_01.png");
    public static final Texture grass02 = new Texture("8x8/grass/grass_02.png");
    public static final Texture grass03 = new Texture("8x8/grass/grass_03.png");
    public static final Texture grass04 = new Texture("8x8/grass/grass_04.png");
    public static final Texture grassLeft = new Texture("8x8/grass/right_grass_edge.png");
    public static final Texture grassRight = new Texture("8x8/grass/left_grass_edge.png");
    public static final Texture grassLeftUpperEdge = new Texture("8x8/grass/left_upper_edge.png");
    public static final Texture grassRightUpperEdge = new Texture("8x8/grass/right_upper_edge.png");
    public static final Texture grassTop = new Texture("8x8/grass/top.png");
    public static final Texture grassTopRight = new Texture("8x8/grass/top_right.png");
    public static final Texture grassTopLeft = new Texture("8x8/grass/top_left.png");
    public static final Texture water01 = new Texture("8x8/water/water_01.png");
    public static final Texture water02 = new Texture("8x8/water/water_02.png");
    public static final Texture water03 = new Texture("8x8/water/water_03.png");
    public static final Texture water04 = new Texture("8x8/water/water_04.png");
    public static final Texture cliff = new Texture(Gdx.files.internal("8x8/cliff.png"));

    private Media() {
    }

    public static void dispose() {
        hero.dispose();
        tree.dispose();
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
