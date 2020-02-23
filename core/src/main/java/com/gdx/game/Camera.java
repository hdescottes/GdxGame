package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Camera {

    public OrthographicCamera createCamera() {
        // Display Size
        int displayW = Gdx.graphics.getWidth();
        int displayH = Gdx.graphics.getHeight();

        // For 800x600 we will get 266*200
        int h = displayH /(displayH /160);
        int w = displayW /(displayH / h);

        OrthographicCamera camera = new OrthographicCamera(w, h);
        camera.zoom = .4f;
        return camera;
    }

    public Control insertControl(OrthographicCamera camera) {
        Control control = new Control((int) camera.viewportWidth, (int) camera.viewportHeight, camera);
        Gdx.input.setInputProcessor(control);
        return control;
    }
}
