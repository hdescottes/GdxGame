package com.gdx.game.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraStyles {

    private CameraStyles() {
    }

    public static void boundaries(OrthographicCamera camera, float startX, float startY, float width, float height) {
        Vector3 position = camera.position;

        if(position.x < startX) {
            position.x = startX;
        }
        if(position.y < startY) {
            position.y = startY;
        }

        if(position.x > startX + width) {
            position.x = startX + width;
        }
        if(position.y > startY + height) {
            position.y = startY + height;
        }

        camera.position.set(position);
        camera.update();
    }
}
