package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Media {

    // TILES
    public static Texture grass01, grass02, grass03, grass04;
    public static Texture grassLeft, grassRight;
    public static Texture grassLeftUpperEdge, grassRightUpperEdge;
    public static Texture grassTop, grassTopRight, grassTopLeft;
    public static Texture water01, water02, water03, water04;
    public static Texture cliff, water;

    public static void setupImages(){
        // Source https://opengameart.org/content/micro-tileset-overworld-and-dungeon
        // Example
        // http://opengameart.org/sites/default/files/styles/watermarked/public/Render_0.png
        grass01 = new Texture("8x8/grass/grass_01.png");
        grass02 = new Texture("8x8/grass/grass_02.png");
        grass03 = new Texture("8x8/grass/grass_03.png");
        grass04 = new Texture("8x8/grass/grass_04.png");

        grassLeft = new Texture("8x8/grass/right_grass_edge.png");
        grassRight = new Texture("8x8/grass/left_grass_edge.png");

        grassLeftUpperEdge = new Texture("8x8/grass/left_upper_edge.png");
        grassRightUpperEdge = new Texture("8x8/grass/right_upper_edge.png");

        grassTop = new Texture("8x8/grass/top.png");
        grassTopRight = new Texture("8x8/grass/top_right.png");
        grassTopLeft = new Texture("8x8/grass/top_left.png");

        water01 = new Texture("8x8/water/water_01.png");
        water02 = new Texture("8x8/water/water_02.png");
        water03 = new Texture("8x8/water/water_03.png");
        water04 = new Texture("8x8/water/water_04.png");
        cliff = new Texture(Gdx.files.internal("8x8/cliff.png"));
    }

    public static void dispose() {
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
