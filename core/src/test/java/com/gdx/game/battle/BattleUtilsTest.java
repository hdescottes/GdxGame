package com.gdx.game.battle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BattleUtilsTest {

    @Test
    public void testEscapeChance_ShouldSucceedWithInfOne() {
        float result = BattleUtils.escapeChance(0.4f);

        assertEquals(0.4f, result);
    }

    @Test
    public void testEscapeChance_ShouldSucceedWithSupOne() {
        float result = BattleUtils.escapeChance(3f);

        assertEquals(1f, result);
    }
}
