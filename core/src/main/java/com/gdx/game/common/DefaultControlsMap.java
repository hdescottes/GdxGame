package com.gdx.game.common;

import com.gdx.game.component.InputComponent;

import java.util.HashMap;

public class DefaultControlsMap {

    public static final HashMap<String, String> DEFAULT_CONTROLS = new HashMap<>();

    static {
        DEFAULT_CONTROLS.put("33", InputComponent.Keys.INTERACT.name());
        DEFAULT_CONTROLS.put("43", InputComponent.Keys.OPTION.name());
        DEFAULT_CONTROLS.put("32", InputComponent.Keys.RIGHT.name());
        DEFAULT_CONTROLS.put("111", InputComponent.Keys.QUIT.name());
        DEFAULT_CONTROLS.put("51", InputComponent.Keys.UP.name());
        DEFAULT_CONTROLS.put("47", InputComponent.Keys.DOWN.name());
        DEFAULT_CONTROLS.put("29", InputComponent.Keys.LEFT.name());
    }

}
