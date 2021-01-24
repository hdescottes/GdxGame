package com.gdx.game.status;


public interface StatusObserver {
    enum StatusEvent {
        UPDATED_GP,
        UPDATED_LEVEL,
        UPDATED_HP,
        UPDATED_MP,
        UPDATED_XP,
        LEVELED_UP
    }

    void onNotify(final int value, StatusEvent event);
}
