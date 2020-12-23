package com.gdx.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

public class TransitionScreen implements Screen {

    private Game game;
    private Screen current;
    private Screen next;

    int currentTransitionEffect;
    ArrayList<TransitionEffect> transitionEffects;

    TransitionScreen(Game game, Screen current, Screen next, ArrayList<TransitionEffect> transitionEffects) {
        this.current = current;
        this.next = next;
        this.transitionEffects = transitionEffects;
        this.currentTransitionEffect = 0;
        this.game = game;
    }

    @Override
    public void render(float delta) {
        if (currentTransitionEffect >= transitionEffects.size()) {
            game.setScreen(next);
            return;
        }

        transitionEffects.get(currentTransitionEffect).update(delta);
        transitionEffects.get(currentTransitionEffect).render(current, next);

        if (transitionEffects.get(currentTransitionEffect).isFinished()) {
            currentTransitionEffect++;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        current.dispose();
        next.dispose();
    }
}
