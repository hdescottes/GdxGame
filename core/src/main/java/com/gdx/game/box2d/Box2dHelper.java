package com.gdx.game.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2dHelper {

    private Box2dHelper() {
    }

    public static Body createBody(World world, float width, float height, float xOffset, float yOffset, Vector3 pos3, Vector2 vector2, BodyDef.BodyType type) {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos3.x + width/2 + xOffset, pos3.y + height/2 + yOffset);
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;
        bodyDef.type = type;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape boxShape = new PolygonShape();
        if(vector2 == null) {
            vector2 = new Vector2(0,0);
        }
        boxShape.setAsBox(width / 2, height / 2, vector2, 0);

        fixtureDef.shape = boxShape;
        fixtureDef.restitution = 0.4f;

        body.createFixture(fixtureDef);
        boxShape.dispose();

        return body;
    }
}
