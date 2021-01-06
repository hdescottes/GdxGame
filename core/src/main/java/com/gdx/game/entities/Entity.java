package com.gdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.EntityEnums.ENTITYSTATE;
import com.gdx.game.entities.EntityEnums.ENTITYTYPE;
import com.gdx.game.map.demo.Chunk;
import com.gdx.game.map.demo.Tile;

public class Entity implements Comparable<Entity> {
    private Vector3 pos3;
    private Texture texture;
    private Texture shadow;
    private float width;
    private float height;
    public ENTITYTYPE type;
    public ENTITYSTATE state;
    public Body body;
    public int hashcode;
    public Body sensor; // A trigger hitbox

    public Boolean ticks; // .tick will only be called if true
    public boolean remove;
    public float time; // Store the time up for the Entity
    public Vector3 destVec; // Destination vector for movement
    public Tile currentTile; // Tile the Entity occupies
    public float coolDown; // For logic
    
    private float directionX = 0;
    private float directionY = 0;
    
    public Entity(Texture texture, Texture shadow, float width, float height) {
        this.pos3 = new Vector3();
        this.texture = texture;
        this.shadow = shadow;
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

    public Texture getShadow() {
        return shadow;
    }

    public void setShadow(Texture shadow) {
        this.shadow = shadow;
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

    public ENTITYTYPE getType() {
        return type;
    }

    public void setType(ENTITYTYPE type) {
        this.type = type;
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

    public void draw(SpriteBatch batch) {
        if(shadow != null) {
            batch.draw(shadow, pos3.x, pos3.y, width, height);
        }
        if(texture != null) {
            batch.draw(texture, pos3.x, pos3.y, width, height);
        }
    }

    public void tick(float delta) {
        time += delta;
    }

    public void tick(float delta, Chunk chunk) {
    }

    public void getVector(Vector3 dest) {
        float dx = dest.x - getPos3().x;
        float dy = dest.y - getPos3().y;
        double h = Math.sqrt(dx * dx + dy * dy);
        float dn = (float)(h / 1.4142135623730951);

        destVec = new Vector3(dx / dn, dy / dn, 0);
    }

    public void updatePositions() {
        getPos3().x = body.getPosition().x - getWidth()/2;
        getPos3().y = body.getPosition().y - getHeight()/4;
    }

    public void collision(Entity entity, boolean begin) {}

    public void interact() {}

    public void removeBodies(Box2dWorld box2D) {
        if(sensor != null) {
            box2D.getWorld().destroyBody(sensor);
        }
        if(body != null) {
            box2D.getWorld().destroyBody(body);
        }
    }

    @Override
    public int compareTo(Entity entity) {
        float tempY =  entity.getPos3().y;
        float compareY = getPos3().y;

        return Float.compare(tempY, compareY);
    }
}
