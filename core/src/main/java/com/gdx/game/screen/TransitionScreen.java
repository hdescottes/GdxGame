package com.gdx.game.screen;

import com.badlogic.gdx.Game;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;
import java.util.Arrays;

public class TransitionScreen extends BaseScreen {

    private Game game;
    private BaseScreen current;
    private BaseScreen next;

    int currentTransitionEffect;
    ArrayList<TransitionEffect> transitionEffects;

    TransitionScreen(Game game, BaseScreen current, BaseScreen next, ArrayList<TransitionEffect> transitionEffects) {
        super((GdxGame) game, null);
        this.current = current;
        this.next = next;
        this.transitionEffects = transitionEffects;
        this.currentTransitionEffect = 0;
        this.game = game;
    }

    @Override
    public void render(float delta) {
        if(next.getClass() != OptionScreen.class) {
            Arrays.stream(AudioObserver.AudioTypeEvent.values())
                    .filter(e -> e.equals(current.getMusicTheme()))
                    .findFirst()
                    .filter(a -> !a.equals(next.getMusicTheme()))
                    .ifPresent(a -> notify(AudioObserver.AudioCommand.MUSIC_STOP, a));
        }

        if(currentTransitionEffect >= transitionEffects.size()) {
            game.setScreen(next);
            return;
        }

        transitionEffects.get(currentTransitionEffect).update(delta);
        transitionEffects.get(currentTransitionEffect).render(current, next);

        if(transitionEffects.get(currentTransitionEffect).isFinished()) {
            currentTransitionEffect++;
        }

    }

    @Override
    public void show() {
        // Nothing
    }

    @Override
    public void resize(int width, int height) {
        // Nothing
    }

    @Override
    public void pause() {
        // Nothing
    }

    @Override
    public void resume() {
        // Nothing
    }

    @Override
    public void hide() {
        // Nothing
    }

    @Override
    public void dispose() {
        current.dispose();
        next.dispose();
    }
}
