package com.gdx.game.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chunk {
    private int numberRows;
    private int numberCols;
    private int tileSize;
    // Tiles are split into arrays of rows
    /*Chunk
            Row
                Tile, Tile, Tile
            Row
                Tile, Tile, Tile
            Row
                Tile, Tile , Tile*/
    private ArrayList<ArrayList<Tile>> tiles;
    
    public Chunk(int numberRows, int numberCols, int tileSize){
        this.tiles = new ArrayList<>();
        this.numberRows = numberRows;
        this.numberCols = numberCols;
        this.tileSize = tileSize;
    }

    public int getNumberRows() {
        return numberRows;
    }

    public void setNumberRows(int numberRows) {
        this.numberRows = numberRows;
    }

    public int getNumberCols() {
        return numberCols;
    }

    public void setNumberCols(int numberCols) {
        this.numberCols = numberCols;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public List<ArrayList<Tile>> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<ArrayList<Tile>> tiles) {
        this.tiles = tiles;
    }

    public Tile getTile(int row, int col){
        return Optional.of(row)
                .filter(r -> tiles.size() > r && r >= 0)
                .map(r -> tiles.get(r))
                .filter(c -> c.size() > col && col >= 0)
                .map(c -> c.get(col))
                .orElse(null);
    }

    public String getTileCode(int row, int col){
        return Optional.of(row)
                .filter(r -> tiles.size() > r && r >= 0)
                .map(r -> tiles.get(r))
                .filter(c -> c.size() > col && col >= 0)
                .map(c -> c.get(col))
                .map(t -> t.isGrass()? "1" : "0")
                .orElse("0");
    }
}