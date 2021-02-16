package com.gdx.game.screen.transition.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.screen.transition.ImmediateModeRendererUtils;

public class RotationTransitionEffect extends TransitionEffect {

    private Color color = new Color();

    public RotationTransitionEffect(float duration) {
        super(duration);
    }

    @Override
    public void render(Screen current, Screen next) {
        current.render(Gdx.graphics.getDeltaTime());
        color.set(0f, 0f, 0f, getAlpha());

        Gdx.gl20.glEnable(GL20.GL_BLEND);
        ImmediateModeRendererUtils.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ImmediateModeRendererUtils.drawFillArcCircle((float) Gdx.graphics.getWidth()/2, (float) Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), color);
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }
}
