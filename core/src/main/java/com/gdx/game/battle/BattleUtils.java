package com.gdx.game.battle;

public class BattleUtils {

    public static float escapeChance(float speedRatio) {
        float baseChance = 0.3f;
        return Math.min(baseChance + speedRatio / 4f, 1f);
    }
}
