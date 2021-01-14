package com.gdx.game.profile;

import com.badlogic.gdx.utils.Array;

public class ProfileSubject {

    private Array<ProfileObserver> observers;

    public ProfileSubject() {
        observers = new Array<>();
    }

    public void addObserver(ProfileObserver profileObserver) {
        observers.add(profileObserver);
    }

    public void removeObserver(ProfileObserver profileObserver) {
        observers.removeValue(profileObserver, true);
    }

    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    protected void notify(final ProfileManager profileManager, ProfileObserver.ProfileEvent event) {
        for(ProfileObserver observer: observers) {
            observer.onNotify(profileManager, event);
        }
    }

}
