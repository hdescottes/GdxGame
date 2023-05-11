package com.gdx.game.battle;

import com.badlogic.gdx.math.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BattleUtils {

    static double escapeChance(double speedRatio) {
        double baseChance = 0.3d;
        return roundDown(Math.min(baseChance + speedRatio / 4d, 1d));
    }

    static double criticalChance(double atkStat) {
        return roundDown((94 - (15260) / (atkStat + 163)) / 100);
    }

    static boolean isSuccessful(double statChance) {
        double randomVal = MathUtils.random(100f) / 100;

        return statChance > randomVal;
    }

    private static double roundDown(double number) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
