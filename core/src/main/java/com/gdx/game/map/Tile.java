package com.gdx.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Entity;
import com.gdx.game.Enums.TILETYPE;

public class Tile extends Entity {
    private int size;
    private int row;
    private int col;
    private String code;
    private Texture secondaryTexture;
    private TILETYPE type;
    
    public Tile(int x, int y, int size, TILETYPE type, Texture texture){
        super(texture, 0,0);
        this.getPos().x = x*size;
        this.getPos().y = y*size;
        this.size = size;
        this.col = x;
        this.row = y;
        this.type = type;
        this.code = "";
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Texture getSecondaryTexture() {
        return secondaryTexture;
    }

    public void setSecondaryTexture(Texture secondaryTexture) {
        this.secondaryTexture = secondaryTexture;
    }

    public TILETYPE getType() {
        return type;
    }

    public void setType(TILETYPE type) {
        this.type = type;
    }

    public boolean isGrass() {
        return type.equals(TILETYPE.GRASS);
    }
    
    public boolean isWater() {
        return type.equals(TILETYPE.WATER);
    }
    
    public boolean isCliff() {
        return type.equals(TILETYPE.CLIFF);
    }
    
    public boolean isPassable() {
        return !isWater() && !isCliff();
    }
    
    public boolean isNotPassable() {
        return !isPassable();
    }

    public String details() {
        return "x: " + getPos().x + " y: " + getPos().y + " row: " + row + " col: " + col + " code: " + code + " type: " + type.toString();
    }
}
