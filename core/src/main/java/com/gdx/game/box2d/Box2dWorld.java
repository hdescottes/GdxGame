package com.gdx.game.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.Control;

public class Box2dWorld {

    private World world;
    private Box2DDebugRenderer debugRenderer;

    public World getWorld() {
        return world;
    }

    public Box2dWorld() {
        world = new World(new Vector2(.0f, .0f), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    public void tick(OrthographicCamera orthographicCamera, Control control) {
        if (control.isDebug()) {
            debugRenderer.render(world, orthographicCamera.combined);
        }
        world.step(Gdx.app.getGraphics().getDeltaTime(), 6, 2);
    }
}
