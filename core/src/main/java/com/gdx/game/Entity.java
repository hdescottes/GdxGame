package com.gdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.gdx.game.Enums.ENTITYTYPE;

public class Entity {
    private Vector3 pos3;
    private Texture texture;
    private float width;
    private float height;
    public ENTITYTYPE type;
    public float speed;
    public Body body;
    
    private float directionX = 0;
    private float directionY = 0;
    
    public Entity(Texture texture, float width, float height){
        this.pos3 = new Vector3();
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public Vector3 getPos3() {
        return pos3;
    }

    public void setPos3(Vector3 pos3) {
        this.pos3 = pos3;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDirectionX() {
        return directionX;
    }

    public void setDirectionX(float directionX) {
        this.directionX = directionX;
    }

    public float getDirectionY() {
        return directionY;
    }

    public void setDirectionY(float directionY) {
        this.directionY = directionY;
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, pos3.x, pos3.y, width, height);
    }
}
