package com.gdx.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.Input.Keys;

public class ControlManager extends InputAdapter implements InputProcessor {
    // CAMERA
    private OrthographicCamera camera;

    // DIRECTIONS
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    // ACTIONS
    private boolean interact;

    // MOUSE
    private boolean leftMouseButton;
    private boolean rightMouseButton;
    private boolean processedClick;
    private Vector2 mouseClickPos = new Vector2();
    private Vector2 mapClickPos = new Vector2();

    // DEBUG
    private boolean debug;

    // SCREEN
    private int screenWidth;
    private int screenHeight;

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isInteract() {
        return interact;
    }

    public void setInteract(boolean interact) {
        this.interact = interact;
    }

    public boolean isLeftMouseButton() {
        return leftMouseButton;
    }

    public void setLeftMouseButton(boolean leftMouseButton) {
        this.leftMouseButton = leftMouseButton;
    }

    public boolean isRightMouseButton() {
        return rightMouseButton;
    }

    public void setRightMouseButton(boolean rightMouseButton) {
        this.rightMouseButton = rightMouseButton;
    }

    public boolean isProcessedClick() {
        return processedClick;
    }

    public void setProcessedClick(boolean processedClick) {
        this.processedClick = processedClick;
    }

    public Vector2 getMouseClickPos() {
        return mouseClickPos;
    }

    public void setMouseClickPos(Vector2 mouseClickPos) {
        this.mouseClickPos = mouseClickPos;
    }

    public Vector2 getMapClickPos() {
        return mapClickPos;
    }

    public void setMapClickPos(Vector2 mapClickPos) {
        this.mapClickPos = mapClickPos;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public ControlManager(int screenWidth, int screenHeight, OrthographicCamera camera) {
        this.camera = camera;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    private void setMouseClickedPos(int screenX, int screenY) {
        // Set mouse position (flip screen Y)
        mouseClickPos.set(screenX, screenHeight - screenY);
        mapClickPos.set(getMapCoords(mouseClickPos));
    }

    public Vector2 getMapCoords(Vector2 mouseCoords) {
        Vector3 v3 = new Vector3(mouseCoords.x, screenHeight - mouseCoords.y, 0);
        this.camera.unproject(v3);
        return new Vector2(v3.x, v3.y);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.DOWN:
            case Keys.S:
                down = true;
                break;
            case Keys.UP:
            case Keys.W:
                up = true;
                break;
            case Keys.LEFT:
            case Keys.A:
                left = true;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = true;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.DOWN:
            case Keys.S:
                down = false;
                break;
            case Keys.UP:
            case Keys.W:
                up = false;
                break;
            case Keys.LEFT:
            case Keys.A:
                left = false;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = false;
                break;
            case Keys.E:
                interact = true;
                break;
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.BACKSPACE:
                debug = !debug;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0 && button == 0) {
            leftMouseButton = true;
        } else if (pointer == 0 && button == 0) {
            rightMouseButton = true;
        }

        setMouseClickedPos(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0 && button == 0) {
            leftMouseButton = false;
            processedClick = false;
        } else if (pointer == 0 && button == 0) {
            rightMouseButton = false;
        }

        setMouseClickedPos(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        setMouseClickedPos(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
