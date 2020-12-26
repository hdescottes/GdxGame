package com.gdx.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.manager.RessourceManager;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.Tree;
import com.gdx.game.map.MapEnums.TILETYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class Island {
    private Tile centreTile;
    private Tile clickedTile;
    private RessourceManager ressourceManager;

    // CHUNKS TODO: Add multiple chunks
    // public Map<Integer, ArrayList<Chunk>> chunks = new HashMap<>();
    
    // ONE CHUNK
    private Chunk chunk;
    private ArrayList<Entity> entities = new ArrayList<>();
    
    // TRACK CLICK
    int currentTileNo;
    int currentCol;
    int currentRow;
    
    // Arrays for mapping code to texture
    String[] aGrassLeft = {"001001001", "001001000", "000001001"};
    String[] aGrassRight = {"100100100","100100000","000100100"};
    String[] aGrassREnd = {"100000000"};
    String[] aGrassLEnd = {"001000000"};
    String[] aGrassTop = {"000000111", "000000011","000000110"};
    String[] aGrassTopRight = {"000000100"};
    String[] aGrassTopLeft = {"000000001"};

    public Island(Box2dWorld box2d, RessourceManager ressourceManager) {
        this.ressourceManager = ressourceManager;

        setupTiles();
        codeTiles();
        generateHitboxes(box2d);
        generateTreeEntities(box2d);
    }

    public Tile getCentreTile() {
        return centreTile;
    }

    public void setCentreTile(Tile centreTile) {
        this.centreTile = centreTile;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }

    private void setupTiles() {
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

        for(int row = 0; row < chunk.getNumberRows(); row ++) {
            for(int col = 0; col < chunk.getNumberCols(); col ++) {
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
        if(row > minRow && row < maxRow && col > minCol && col < maxCol) {
            tile.setTexture(randomGrass());
            tile.setTiletype(TILETYPE.GRASS);

            if(row == firstTileRow + 1) {
                tile.setTexture(ressourceManager.cliff);
                tile.setTiletype(TILETYPE.CLIFF);
            } else {
                // Chance to add trees etc
            }
        }
    }

    private HashMap<Integer, ArrayList<Tile>> addTilesToChunk(int row, int col, Tile tile, int currentRow, ArrayList<Tile> chunkRow, HashMap<Integer, ArrayList<Tile>> map) {
        // CHUNK ROW
        if(currentRow == row) {
            // Add tile to current row
            chunkRow.add(tile);

            // Last row and column?
            if (row == chunk.getNumberRows() - 1 && col == chunk.getNumberCols() - 1) {
                chunk.getTiles().add(chunkRow);
            }
            map.put(currentRow, chunkRow);
        } else {
            // New row
            currentRow = row;

            // Add row to chunk
            chunk.getTiles().add(chunkRow);

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
        if(Arrays.asList(aGrassLeft).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassLeft);
        } else if(Arrays.asList(aGrassRight).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassRight);
        } else if(Arrays.asList(aGrassREnd).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassLeftUpperEdge);
        } else if(Arrays.asList(aGrassLEnd).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassRightUpperEdge);
        } else if(Arrays.asList(aGrassTop).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassTop);
        } else if(Arrays.asList(aGrassTopRight).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassTopRight);
        } else if(Arrays.asList(aGrassTopLeft).contains(tile.getCode())) {
            tile.setSecondaryTexture(ressourceManager.grassTopLeft);
        }        
    }
    
    private Texture randomGrass() {
        Texture grass;
        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  grass = ressourceManager.grass01;
                     break;
            case 2:  grass = ressourceManager.grass02;
                     break;
            case 3:  grass = ressourceManager.grass03;
                     break;
            case 4:  grass = ressourceManager.grass04;
                     break;
            default: grass = ressourceManager.grass01;
                     break;        
        }
        return grass;
    }

    private Texture randomWater() {
        Texture water;
        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  water = ressourceManager.water01;
                     break;
            case 2:  water = ressourceManager.water02;
                     break;
            case 3:  water = ressourceManager.water03;
                     break;
            case 4:  water = ressourceManager.water04;
                     break;
            default: water = ressourceManager.water01;
                     break;        
        }
        return water;
    }
    
    private void codeTiles() {
        // Loop all tiles and set the initial code
     
        // 1 CHUNK ONLY ATM
        for(ArrayList<Tile> row : chunk.getTiles()) {
            for(Tile tile : row){ 
                // Check all surrounding tiles and set 1 for pass 0 for non pass
                // 0 0 0
                // 0 X 0
                // 0 0 0
                
                int[] rows = {1,0,-1}; //1 for row above, 0 for same, -1 for row below
                int[] cols = {-1,0,1};
                
                for(int r: rows) {
                    for(int c: cols) {
                        tile.setCode(tile.getCode() + chunk.getTileCode(tile.getRow() + r, tile.getCol() + c));
                        updateImage(tile);
                    }
                }    
            }
        }
    }

    private void generateTreeEntities(Box2dWorld box2d) {
        // Loop all tiles and add random trees
        chunk.getTiles().forEach(r -> r.forEach(t -> addRandomTrees(box2d, t)));
    }

    private void addRandomTrees(Box2dWorld box2D, Tile tile) {
        Stream.of(tile)
                .filter(Tile::isGrass)
                .filter(t -> MathUtils.random(100) > 90)
                .forEach(t -> entities.add(new Tree(tile.getPos3(), box2D, ressourceManager)));
    }

    private void generateHitboxes(Box2dWorld box2d) {
        chunk.getTiles().forEach(r -> r.forEach(t -> createTileBody(box2d, t)));
    }

    private void createTileBody(Box2dWorld box2d, Tile tile) {
        Stream.of(tile)
                .filter(t -> t.isNotPassable() && t.notIsAllWater())
                .forEach(t -> Box2dHelper.createBody(box2d.getWorld(), chunk.getTileSize(), chunk.getTileSize(), 0, 0, t.getPos3(), null, BodyDef.BodyType.StaticBody));
    }

    public void clearRemovedEntities(Box2dWorld box2D) {
        Iterator<Entity> it = entities.iterator();
        while(it.hasNext()) {
            Entity e = it.next();
            removeEntity(box2D, it, e);
        }
    }

    private void removeEntity(Box2dWorld box2d, Iterator<Entity> it, Entity entity) {
        Stream.of(entity)
                .filter(e -> e.remove)
                .forEach(e -> {
                    e.removeBodies(box2d);
                    box2d.removeEntityToMap(e);
                    it.remove();
                });
    }
}
