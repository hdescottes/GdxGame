package com.gdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.Enums.ENTITYTYPE;

public class Entity {
    private Vector2 pos;
    private Vector3 pos3;
    private Texture texture;
    private float width;
    private float height;
    public ENTITYTYPE type;
    public float speed;
    
    float dirX = 0;
    float dirY = 0;
    
    public Entity(Texture texture, float width, float height){
        this.pos = new Vector2();
        this.pos3 = new Vector3();
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
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

    public void draw(SpriteBatch batch){
        batch.draw(texture, pos.x, pos.y, width, height);
    }
}
