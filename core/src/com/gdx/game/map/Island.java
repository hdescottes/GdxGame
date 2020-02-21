package com.gdx.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.game.Entity;
import com.gdx.game.Enums.TILETYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.gdx.game.Media.*;

public class Island {
    Tile centreTile;
    Tile clickedTile;
    
    // CHUNKS TODO: Add multiple chunks
    // public Map<Integer, ArrayList<Chunk> chunks = new Map<Integer, ArrayList<Chunk>();
    
    // ONE CHUNK
    public Chunk chunk;
    ArrayList<Entity> entities = new ArrayList<>();
    
    // TRACK CLICK
    int currentTileNo;
    int currentCol;
    int currentRow;
    
    // Arrays for mapping code to texture
    String[] aGrassLeft = {"001001001","001001001", "001001000", "000001001"};
    String[] aGrassRight = {"100100100","100100000","000100100"};
    String[] aGrassREnd = {"100000000"};
    String[] aGrassLEnd = {"001000000"};
    String[] aGrassTop = {"000000111", "000000011","000000110"};
    String[] aGrassTopRight = {"000000100"};
    String[] aGrassTopLeft = {"000000001"};
    
    public Island(){
        setupTiles();
        codeTiles();
    }
    
    private void setupTiles(){
        chunk = new Chunk(33,33, 8);

        int currentRow = 0;
        int centreTileRow = chunk.getNumberRows() / 2;
        int centreTileCol = chunk.getNumberCols() /2;

        int rngW = MathUtils.random(5,8);
        int rngH = MathUtils.random(5,8);
        int maxRow = centreTileRow + rngH;
        int minRow = centreTileRow - rngH;
        int maxCol = centreTileCol + rngW;
        int minCol = centreTileCol - rngW;
        int firstTileRow = centreTileRow - (rngH);

        ArrayList<Tile> chunkRow = new ArrayList<>();
        HashMap<Integer, ArrayList<Tile>> map = new HashMap<>();

        // If number of tiles is needed.
        // int num_tiles = ((maxCol - minCol)-1) * ((maxRow - minRow)-1);

        for(int row = 0; row < chunk.getNumberRows(); row ++){
            for(int col = 0; col < chunk.getNumberCols(); col ++){
                // Create TILE
                Tile tile = new Tile(col, row, chunk.getTileSize(), TILETYPE.WATER, randomWater());

                // Make a small island
                createIsland(row, col, tile, minRow, maxRow, minCol, maxCol, firstTileRow);

                // ADD TILE TO CHUNK
                HashMap<Integer, ArrayList<Tile>> completeMap = addTilesToChunk(row, col, tile, currentRow, chunkRow, map);
                for ( Map.Entry<Integer, ArrayList<Tile>> entry : completeMap.entrySet()) {
                    currentRow = entry.getKey();
                    chunkRow = entry.getValue();
                }
            }
        }

        // Set centre tile for camera positioning
        centreTile = chunk.getTile(centreTileRow, centreTileCol);
    }

    private void createIsland(int row, int col, Tile tile, int minRow, int maxRow, int minCol, int maxCol, int firstTileRow) {
        if(row > minRow && row < maxRow && col > minCol && col < maxCol){
            tile.setTexture(randomGrass());
            tile.setType(TILETYPE.GRASS);

            if(row == firstTileRow + 1){
                tile.setTexture(cliff);
                tile.setType(TILETYPE.CLIFF);
            } else {
                // Chance to add trees etc
            }
        }
    }

    private HashMap<Integer, ArrayList<Tile>> addTilesToChunk(int row, int col, Tile tile, int currentRow, ArrayList<Tile> chunkRow, HashMap<Integer, ArrayList<Tile>> map) {
        // CHUNK ROW
        if(currentRow == row){
            // Add tile to current row
            chunkRow.add(tile);

            // Last row and column?
            if (row == chunk.getNumberRows() - 1 && col == chunk.getNumberCols() - 1){
                chunk.tiles.add(chunkRow);
            }
            map.put(currentRow, chunkRow);
        } else {
            // New row
            currentRow = row;

            // Add row to chunk
            chunk.tiles.add(chunkRow);

            // Clear chunk row
            chunkRow = new ArrayList<>();

            // Add first tile to the new row
            chunkRow.add(tile);

            map.put(currentRow, chunkRow);
        }
        return map;
    }
    
    private void updateImage(Tile tile) {
        // Secondary Texture is to add edges to tiles
        // TODO: Add array of textures per tile
        if(Arrays.asList(aGrassLeft).contains(tile.getCode())){
            tile.setSecondaryTexture(grassLeft);
        } else if(Arrays.asList(aGrassRight).contains(tile.getCode())){
            tile.setSecondaryTexture(grassRight);
        } else if(Arrays.asList(aGrassREnd).contains(tile.getCode())){
            tile.setSecondaryTexture(grassLeftUpperEdge);
        } else if(Arrays.asList(aGrassLEnd).contains(tile.getCode())){
            tile.setSecondaryTexture(grassRightUpperEdge);
        } else if(Arrays.asList(aGrassTop).contains(tile.getCode())){
            tile.setSecondaryTexture(grassTop);
        } else if(Arrays.asList(aGrassTopRight).contains(tile.getCode())){
            tile.setSecondaryTexture(grassTopRight);
        } else if(Arrays.asList(aGrassTopLeft).contains(tile.getCode())){
            tile.setSecondaryTexture(grassTopLeft);
        }        
    }
    
    private Texture randomGrass(){
        Texture grass;
        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  grass = grass01;
                     break;
            case 2:  grass = grass02;
                     break;
            case 3:  grass = grass03;
                     break;
            case 4:  grass = grass04;
                     break;
            default: grass = grass01;
                     break;        
        }
        return grass;
    }

    private Texture randomWater(){
        Texture water;
        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  water = water01;
                     break;
            case 2:  water = water02;
                     break;
            case 3:  water = water03;
                     break;
            case 4:  water = water04;
                     break;
            default: water = water01;
                     break;        
        }
        return water;
    }
    
    private void codeTiles() {
        // Loop all tiles and set the initial code
     
        // 1 CHUNK ONLY ATM
        for(ArrayList<Tile> row : chunk.tiles){
            for(Tile tile : row){ 
                // Check all surrounding tiles and set 1 for pass 0 for non pass
                // 0 0 0
                // 0 X 0
                // 0 0 0
                
                int[] rows = {1,0,-1}; //1 for row above, 0 for same, -1 for row below
                int[] cols = {-1,0,1};
                
                for(int r: rows){
                    for(int c: cols){
                        tile.setCode(tile.getCode() + chunk.getTileCode(tile.getRow() + r, tile.getCol() + c));
                        updateImage(tile);
                    }
                }    
            }
        }
    }
}
