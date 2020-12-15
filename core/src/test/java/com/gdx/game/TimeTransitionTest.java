package com.gdx.game;

import com.gdx.game.screen.transition.TimeTransition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(GdxRunnerTest.class)
public class TimeTransitionTest {

    @Test
    public void testGet_ShouldSucceedWithTimeNull() {
        TimeTransition timeTransition = new TimeTransition();

        float time = timeTransition.get();

        assertThat(time).isEqualTo(1f);
    }

    @Test
    public void testGet_ShouldSucceedWithTimeNotZero() {
        float duration = 1f;
        TimeTransition timeTransition = new TimeTransition();
        timeTransition.start(duration);

        float time = timeTransition.get();

        assertThat(time).isEqualTo(0);
    }
}
