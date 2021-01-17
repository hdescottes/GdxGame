package com.gdx.game.screen;

import com.badlogic.gdx.Game;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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
        if(currentTransitionEffect >= transitionEffects.size()) {
            game.setScreen(next);
            return;
        }

        transitionEffects.get(currentTransitionEffect).update(delta);
        transitionEffects.get(currentTransitionEffect).render(current, next);

        if(transitionEffects.get(currentTransitionEffect).isFinished()) {
            currentTransitionEffect++;
        }

        if(next.getClass() != OptionScreen.class) {
            Optional<AudioObserver.AudioTypeEvent> audioEvent = Arrays.stream(AudioObserver.AudioTypeEvent.values())
                    .filter(e -> e.equals(current.getMusicTheme()))
                    .findFirst();
            audioEvent.ifPresent(audioTypeEvent -> notify(AudioObserver.AudioCommand.MUSIC_STOP, audioTypeEvent));
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
