package com.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.Control;
import com.gdx.game.Enums.ENTITYTYPE;
import com.gdx.game.Media;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;

public class Hero extends Entity {

    private int heroSpeed;

    public Hero(Vector3 pos3, Box2dWorld box2d) {
        super(Media.hero,null,8, 8);
        this.type = ENTITYTYPE.HERO;
        this.getPos3().x = pos3.x;
        this.getPos3().y = pos3.y;
        this.heroSpeed = 50;
        this.body = Box2dHelper.createBody(box2d.getWorld(), this.getWidth(), this.getHeight()/2, this.getWidth()/4, 0, this.getPos3(), BodyDef.BodyType.DynamicBody);
    }

    public void update(Control control) {
        float directionX = getDirectionX();
        float directionY = getDirectionY();

        if(control.isDown()) {
            directionY = -1 ;
        }
        if(control.isUp()) {
            directionY = 1 ;
        }
        if(control.isLeft()) {
            directionX = -1;
        }
        if(control.isRight()) {
            directionX = 1;
        }

        body.setLinearVelocity(directionX * heroSpeed, directionY * heroSpeed);

        updatePositions();
    }

    public float getCameraX() {
        return getPos3().x + getWidth()/2;
    }

    public float getCameraY() {
        return getPos3().y + getHeight()/2;
    }

}
