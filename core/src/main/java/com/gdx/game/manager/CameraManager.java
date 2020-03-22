package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gdx.game.manager.ControlManager;

public class CameraManager {

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

    public ControlManager insertControl(OrthographicCamera camera) {
        ControlManager controlManager = new ControlManager((int) camera.viewportWidth, (int) camera.viewportHeight, camera);
        Gdx.input.setInputProcessor(controlManager);
        return controlManager;
    }
}
