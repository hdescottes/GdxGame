package com.gdx.game.entities.player.characterClass;

public interface ClassObserver {

    enum ClassEvent {
        CHECK_UPGRADE_TREE_CLASS
    }

    void onNotify(String value, ClassEvent event);
}
