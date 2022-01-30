package com.gdx.game.component;

public interface ComponentObserver {
    enum ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION,
        LOAD_RESUME,
        SHOW_RESUME,
        QUEST_LOCATION_DISCOVERED,
        ENEMY_SPAWN_LOCATION_CHANGED,
        START_BATTLE,
        OPTION_INPUT
    }

    void onNotify(final String value, ComponentEvent event);
}
