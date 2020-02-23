package com.gdx.game;

import com.badlogic.gdx.math.Vector2;
import com.gdx.game.Enums.ENTITYTYPE;

public class Hero extends Entity {

    private int directionX;
    private int directionY;
    private int speed;

    public Hero(Vector2 pos) {
        super(Media.hero, 8, 8);
        this.type = ENTITYTYPE.HERO;
        this.getPos().x = pos.x;
        this.getPos().y = pos.y;
        this.getPos3().x = pos.x;
        this.getPos3().y = pos.y;
        this.speed = 1;
    }

    public void update(Control control) {
        directionX = 0;
        directionY = 0;

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

        getPos().x += directionX * speed;
        getPos().y += directionY * speed;

        getPos3().x = getPos().x;
        getPos3().y = getPos().y;
    }

    public float getCameraX() {
        return getPos().x + getWidth()/2;
    }

    public float getCameraY() {
        return getPos().y + getHeight()/2;
    }

}
