package com.gdx.game.component;

public interface ComponentObserver {
    enum ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION,
        QUEST_LOCATION_DISCOVERED,
        ENEMY_SPAWN_LOCATION_CHANGED,
    }

    void onNotify(final String value, ComponentEvent event);
}
