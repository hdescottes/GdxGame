package com.gdx.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.MENU_THEME;
import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.TOPPLE_THEME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
class AudioManagerTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
    }

    @Test
    void testGetInstance_ShouldSucceed() {
        AudioManager audioManager = AudioManager.getInstance();

        assertThat(audioManager).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("loadMusic")
    void testOnNotify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        new ResourceManager();
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setCurrentMusic(null);

        audioManager.onNotify(AudioObserver.AudioCommand.MUSIC_LOAD, event);
        audioManager.onNotify(command, event);

        assertThat(audioManager.getCurrentMusic()).isEqualTo(ResourceManager.getMusicAsset(event.getValue()));
    }

    private static Stream<Arguments> loadMusic() {
        return Stream.of(
                Arguments.of(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, MENU_THEME),
                Arguments.of(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, TOPPLE_THEME)
        );
    }

    @Test
    void testOnNotify_ShouldSucceedWithMusicStop() {
        new ResourceManager();
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setCurrentMusic(null);

        audioManager.onNotify(AudioObserver.AudioCommand.MUSIC_LOAD, MENU_THEME);
        audioManager.onNotify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, MENU_THEME);
        audioManager.onNotify(AudioObserver.AudioCommand.MUSIC_STOP, MENU_THEME);

        assertThat(audioManager.getCurrentMusic().isPlaying()).isFalse();
    }
}
