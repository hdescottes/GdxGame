package com.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.Media;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.EntityEnums.ENTITYTYPE;

public class Tree extends Entity {

    public Tree(Vector3 pos3, Box2dWorld box2d) {
        super(Media.tree,null,8, 8);
        this.type = ENTITYTYPE.TREE;
        this.getPos3().x = pos3.x;
        this.getPos3().y = pos3.y;
        this.body = Box2dHelper.createBody(box2d.getWorld(), getWidth()/2, getHeight()/2, getWidth()/4, 0, getPos3(), null, BodyDef.BodyType.StaticBody);
        this.sensor = Box2dHelper.createSensor(box2d.getWorld(), getWidth()*.6f, getHeight()*.6f, getWidth()/4 - 0.4f, -0.4f, getPos3(), null, BodyDef.BodyType.DynamicBody);
        this.hashcode = sensor.getFixtureList().get(0).hashCode();
    }

    @Override
    public void interact(){
        remove = true;
    }
}
