package com.gdx.game.common;

import com.gdx.game.component.InputComponent;

import java.util.HashMap;

public class DefaultControlsMap {

    public static final HashMap<String, String> defaultControls = new HashMap<>();

    static {
        defaultControls.put("33", InputComponent.Keys.INTERACT.name());
        defaultControls.put("44", InputComponent.Keys.OPTION.name());
        defaultControls.put("22", InputComponent.Keys.RIGHT.name());
        defaultControls.put("111", InputComponent.Keys.QUIT.name());
        defaultControls.put("19", InputComponent.Keys.UP.name());
        defaultControls.put("20", InputComponent.Keys.DOWN.name());
        defaultControls.put("21", InputComponent.Keys.LEFT.name());
    }

}
