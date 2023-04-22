package com.gdx.game.battle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BattleUtilsTest {

    @Test
    public void testEscapeChance_ShouldSucceedWithInfOne() {
        double result = BattleUtils.escapeChance(0.4d);

        assertEquals(0.4d, result);
    }

    @Test
    public void testEscapeChance_ShouldSucceedWithSupOne() {
        double result = BattleUtils.escapeChance(3d);

        assertEquals(1d, result);
    }

    @Test
    public void testCriticalChance_ShouldSucceed() {
        double result = BattleUtils.criticalChance(18);

        assertEquals(0.0969, result);
    }
}
