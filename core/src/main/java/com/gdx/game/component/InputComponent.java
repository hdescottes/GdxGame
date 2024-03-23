package com.gdx.game.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.entities.Entity;

import java.util.HashMap;
import java.util.Map;

import static com.gdx.game.common.UtilityClass.mapInverter;

public abstract class InputComponent extends ComponentSubject implements Component, InputProcessor {

    protected Entity.Direction currentDirection = null;
    protected Entity.State currentState = null;
    protected Json json;

    public enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT, INTERACT, OPTION
    }

    protected enum Mouse {
        SELECT, DOACTION
    }

    protected static Map<Keys, Boolean> keys = new HashMap<>();
    protected static Map<Mouse, Boolean> mouseButtons = new HashMap<>();

    //initialize the hashmap for inputs
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
        keys.put(Keys.INTERACT, false);
        keys.put(Keys.OPTION, false);
    }

    static {
        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DOACTION, false);
    }

    public InputComponent() {
        json = new Json();
    }

    public abstract void update(Entity entity, float delta);

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public static HashMap<Integer, Keys> playerControls = new HashMap<>();

    public static void setPlayerControlMapFromJsonControlsMap(HashMap<String, String> jsonMap){
        HashMap<Integer, Keys> newPlayerControls = new HashMap<>();

        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            newPlayerControls.put(Integer.valueOf(entry.getKey()), InputComponent.Keys.valueOf(entry.getValue()));
        }

        playerControls = newPlayerControls;
    }

    public static HashMap<String, String> mapJsonControlsToPlayerControl(HashMap<Integer, Keys> playerControls){
        HashMap<String, String> result = new HashMap<>();

        for (Map.Entry<Integer, InputComponent.Keys> entry : playerControls.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return result;
    }

    public static HashMap<String, String> changeValueFromJsonControlsMap(HashMap<String, String> jsonMap, Keys keyValue, Integer keyCode){
        jsonMap = mapInverter(jsonMap);
        jsonMap.put(keyValue.name(), String.valueOf(keyCode));
        jsonMap = mapInverter(jsonMap);

        return jsonMap;
    }

}
