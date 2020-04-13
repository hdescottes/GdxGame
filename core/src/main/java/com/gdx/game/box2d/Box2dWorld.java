package com.gdx.game.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.entities.Entity;
import com.gdx.game.manager.ControlManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Box2dWorld {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private HashMap<Integer, Entity> entityMap;

    public World getWorld() {
        return world;
    }

    public Box2dWorld() {
        world = new World(new Vector2(.0f, .0f), true);
        debugRenderer = new Box2DDebugRenderer();
        entityMap = new HashMap<>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                processCollisions(fixtureA, fixtureB, true);
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                processCollisions(fixtureA, fixtureB, false);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }

    private void processCollisions(Fixture aFixture, Fixture bFixture, boolean begin) {
        Entity entityA = entityMap.get(aFixture.hashCode());
        Entity entityB = entityMap.get(bFixture.hashCode());

        if (entityA != null && entityB != null) {
            if (aFixture.isSensor() && !bFixture.isSensor()) {
                entityB.collision(entityA, begin);
            } else if (bFixture.isSensor() && !aFixture.isSensor()) {
                entityA.collision(entityB, begin);
            }
        }
    }

    public void populateEntityMap(ArrayList<Entity> entities) {
        entityMap.clear();
        entities.forEach(e -> entityMap.put(e.hashcode, e));
    }

    public void tick(OrthographicCamera orthographicCamera, ControlManager controlManager) {
        if (controlManager.isDebug()) {
            debugRenderer.render(world, orthographicCamera.combined);
        }
        world.step(Gdx.app.getGraphics().getDeltaTime(), 6, 2);
    }

    public void removeEntityToMap(Entity entity){
        entityMap.remove(entity.hashcode, entity);
    }
}
