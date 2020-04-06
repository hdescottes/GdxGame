package com.gdx.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.entities.Entity;
import com.gdx.game.map.MapEnums.TILETYPE;

public class Tile extends Entity {
    private int size;
    private int row;
    private int col;
    private String code;
    private Texture secondaryTexture;
    private TILETYPE tiletype;
    
    public Tile(int x, int y, int size, TILETYPE tiletype, Texture texture) {
        super(texture,null,0,0);
        this.getPos3().x = x*size;
        this.getPos3().y = y*size;
        this.size = size;
        this.col = x;
        this.row = y;
        this.tiletype = tiletype;
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

    public TILETYPE getTiletype() {
        return tiletype;
    }

    public void setTiletype(TILETYPE tiletype) {
        this.tiletype = tiletype;
    }

    public boolean isGrass() {
        return tiletype.equals(TILETYPE.GRASS);
    }
    
    public boolean isWater() {
        return tiletype.equals(TILETYPE.WATER);
    }
    
    public boolean isCliff() {
        return tiletype.equals(TILETYPE.CLIFF);
    }
    
    public boolean isPassable() {
        return !isWater() && !isCliff();
    }
    
    public boolean isNotPassable() {
        return !isPassable();
    }

    public boolean isAllWater() {
        return code.equals("000000000");
    }

    public boolean notIsAllWater() {
        return !isAllWater();
    }

    public String details() {
        return "x: " + getPos3().x + " y: " + getPos3().y + " row: " + row + " col: " + col + " code: " + code + " type: " + tiletype.toString();
    }
}
