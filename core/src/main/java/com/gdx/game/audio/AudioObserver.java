package com.gdx.game.audio;

public interface AudioObserver {

    enum AudioTypeEvent {
        MENU_THEME("music/Rising_Sun.mp3"),
        TOPPLE_THEME("music/Village.mp3"),
        TEST_THEME("music/Dwarves'_Theme.mp3"),
        BATTLE_THEME("music/Challenge.mp3"),
        GAME_OVER_THEME("music/Requiem.mp3"),
        NONE("");

        private String audioFullFilePath;

        AudioTypeEvent(String audioFullFilePath) {
            this.audioFullFilePath = audioFullFilePath;
        }

        public String getValue() {
            return audioFullFilePath;
        }
    }

    enum AudioCommand {
        MUSIC_LOAD,
        MUSIC_PLAY_ONCE,
        MUSIC_PLAY_LOOP,
        MUSIC_STOP,
        MUSIC_STOP_ALL
    }

    void onNotify(AudioCommand command, AudioTypeEvent event);
}
