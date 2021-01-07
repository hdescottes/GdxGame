package com.gdx.game.screen.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;

public class ImmediateModeRendererUtils {

    private static final ImmediateModeRenderer renderer = new ImmediateModeRenderer20(true, true, 0);
    private static Matrix4 projectionMatrix = null;

    private ImmediateModeRendererUtils() {
    }

    public static Matrix4 getProjectionMatrix() {
        if(projectionMatrix == null) {
            projectionMatrix = new Matrix4();
            projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        return projectionMatrix;
    }

    public static void fillRectangle(float x0, float y0, float x1, float y1, Color color) {
        renderer.begin(getProjectionMatrix(), GL20.GL_TRIANGLES);

        // first triangle
        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x0, y0, 0f);

        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x0, y1, 0f);

        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x1, y1, 0f);

        // second triangle
        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x1, y1, 0f);

        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x1, y0, 0f);

        renderer.color(color.r, color.g, color.b, color.a);
        renderer.vertex(x0, y0, 0f);

        renderer.end();
    }
}
