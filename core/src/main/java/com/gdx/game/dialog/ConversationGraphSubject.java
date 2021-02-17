package com.gdx.game.dialog;

import com.badlogic.gdx.utils.Array;

public class ConversationGraphSubject {
    private Array<ConversationGraphObserver> observers;

    public ConversationGraphSubject() {
        observers = new Array<>();
    }

    public void addObserver(ConversationGraphObserver graphObserver) {
        observers.add(graphObserver);
    }

    public void removeObserver(ConversationGraphObserver graphObserver) {
        observers.removeValue(graphObserver, true);
    }

    public void removeAllObservers() {
        for(ConversationGraphObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    public void notify(final ConversationGraph graph, ConversationGraphObserver.ConversationCommandEvent event) {
        for(ConversationGraphObserver observer: observers) {
            observer.onNotify(graph, event);
        }
    }}
