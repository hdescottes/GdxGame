package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gdx.game.manager.ControlManager;

public class CameraManager {

    public OrthographicCamera createCamera(int width, int height, float zoom) {
        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.zoom = zoom;
        return camera;
    }

    public ControlManager insertControl(OrthographicCamera camera) {
        ControlManager controlManager = new ControlManager((int) camera.viewportWidth, (int) camera.viewportHeight, camera);
        Gdx.input.setInputProcessor(controlManager);
        return controlManager;
    }
}
