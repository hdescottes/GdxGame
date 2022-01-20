package com.gdx.game.battle;

import com.gdx.game.entities.Entity;

public interface BattleObserver {
    enum BattleEvent {
        OPPONENT_ADDED,
        OPPONENT_HIT_DAMAGE,
        OPPONENT_DEFEATED,
        OPPONENT_TURN_DONE,
        PLAYER_ADDED,
        PLAYER_HIT_DAMAGE,
        PLAYER_RUNNING,
        PLAYER_TURN_DONE,
        PLAYER_TURN_START,
        PLAYER_USED_MAGIC,
        RESUME_OVER,
        NONE
    }

    void onNotify(final Entity enemyEntity, BattleEvent event);
}
