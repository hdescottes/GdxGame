package com.gdx.game.audio;

import com.badlogic.gdx.audio.Music;
import com.gdx.game.manager.PreferenceManager;
import com.gdx.game.manager.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class AudioManager implements AudioObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioManager.class);

    private static AudioManager instance = null;

    private Hashtable<String, Music> queuedMusic;

    private AudioManager() {
        queuedMusic = new Hashtable<>();
    }

    public static AudioManager getInstance() {
        if(instance == null) {
            instance = new AudioManager();
        }

        return instance;
    }


    @Override
    public void onNotify(AudioCommand command, AudioTypeEvent event) {
        switch(command){
            case MUSIC_LOAD:
                ResourceManager.loadMusicAsset(event.getValue());
                break;
            case MUSIC_PLAY_ONCE:
                playMusic(false, event.getValue());
                break;
            case MUSIC_PLAY_LOOP:
                playMusic(true, event.getValue());
                break;
            case MUSIC_STOP:
                Music music = queuedMusic.get(event.getValue());
                if(music != null) {
                    music.stop();
                }
                break;
            case MUSIC_STOP_ALL:
                for(Music musicStop: queuedMusic.values()) {
                    musicStop.stop();
                }
                break;
            default:
                break;
        }
    }

    private Music playMusic(boolean isLooping, String fullFilePath) {
        Music music = queuedMusic.get(fullFilePath);
        if(music != null) {
            music.setLooping(isLooping);
            music.play();
            music.setVolume(PreferenceManager.getMusicVolume());
        } else if(ResourceManager.isAssetLoaded(fullFilePath)) {
            music = ResourceManager.getMusicAsset(fullFilePath);
            music.setLooping(isLooping);
            music.play();
            music.setVolume(PreferenceManager.getMusicVolume());
            queuedMusic.put(fullFilePath, music);
        } else {
            LOGGER.debug("Music not loaded");
            return null;
        }
        return music;
    }

    public void dispose() {
        for (Music music: queuedMusic.values()) {
            music.dispose();
        }
    }

}
