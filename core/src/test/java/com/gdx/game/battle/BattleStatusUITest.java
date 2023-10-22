package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class BattleStatusUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);

        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.setProperty("currentPlayerXPMax", 200);
        profileManager.setProperty("currentPlayerXP", 0);
        profileManager.setProperty("currentPlayerHPMax", 50);
        profileManager.setProperty("currentPlayerHP", 40);
        profileManager.setProperty("currentPlayerMPMax", 50);
        profileManager.setProperty("currentPlayerMP", 45);
        profileManager.setProperty("currentPlayerLevel", 1);
    }

    @Test
    public void testBattleStatusUI_ShouldSucceed() {
        BattleStatusUI battleStatusUI = new BattleStatusUI();

        assertThat(battleStatusUI).isNotNull();
        assertThat(battleStatusUI.getChildren().size).isEqualTo(16);
        assertThat(Arrays.stream(battleStatusUI.getChildren().items).count()).isEqualTo(24);
    }

    @ParameterizedTest
    @MethodSource("xpValue")
    void testSetXpValue(int xp, int xpRemainder, int level) {
        BattleStatusUI battleStatusUI = new BattleStatusUI();
        battleStatusUI.setXPValue(xp);

        assertThat(battleStatusUI).isNotNull();
        assertThat(battleStatusUI.getXPValue()).isEqualTo(xpRemainder);
        assertThat(battleStatusUI.getLevelValue()).isEqualTo(level);
    }

    private static Stream<Arguments> xpValue() {
        return Stream.of(
                Arguments.of(210, 10, 2),
                Arguments.of(650, 50, 3)
        );
    }
}
