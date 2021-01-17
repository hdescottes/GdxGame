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

    private Music currentMusic;

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

    public Music getCurrentMusic() {
        return currentMusic;
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    private void checkMusicEnabled(Music music) {
        if(!PreferenceManager.getInstance().isMusicEnabled()) {
            music.stop();
        } else {
            music.play();
        }
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

    private void playMusic(boolean isLooping, String fullFilePath) {
        Music music = queuedMusic.get(fullFilePath);
        if(music != null) {
            music.setLooping(isLooping);
            music.setVolume(PreferenceManager.getMusicVolume());
            checkMusicEnabled(music);
            setCurrentMusic(music);
        } else if(ResourceManager.isAssetLoaded(fullFilePath)) {
            music = ResourceManager.getMusicAsset(fullFilePath);
            music.setLooping(isLooping);
            music.setVolume(PreferenceManager.getMusicVolume());
            checkMusicEnabled(music);
            queuedMusic.put(fullFilePath, music);
            setCurrentMusic(music);
        } else {
            LOGGER.debug("Music not loaded");
        }
    }

    public void dispose() {
        for (Music music: queuedMusic.values()) {
            music.dispose();
        }
    }

}
