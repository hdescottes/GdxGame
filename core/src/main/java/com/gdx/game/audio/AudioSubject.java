package com.gdx.game.audio;

public interface AudioSubject {
    void addObserver(AudioObserver audioObserver);
    void removeObserver(AudioObserver audioObserver);
    void removeAllObservers();
    void notify(final AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event);
}
