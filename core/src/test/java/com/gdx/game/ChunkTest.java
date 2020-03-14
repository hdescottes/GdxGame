package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.map.Chunk;
import com.gdx.game.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
public class ChunkTest {

    @BeforeEach
    void init() {
        Gdx.graphics = mock(Graphics.class);
        Gdx.gl = mock(GL20.class);
    }

    @Test
    void testGetTile_ShouldSucceedWithRowCol() {
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        Tile tileTest = chunk.getTile(0,0);

        assertThat(tileTest).isEqualTo(tile);
    }

    @Test
    void testGetTile_ShouldFailedWithRowColCauzRowExpectedIsOff() {
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        Tile tileTest = chunk.getTile(1,0);

        assertThat(tileTest).isNull();
    }

    @Test
    void testGetTile_ShouldSucceedVector() {
        Vector2 vector2 = new Vector2(0,0);
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        Tile tileTest = chunk.getTile(vector2);

        assertThat(tileTest).isEqualTo(tile);
    }

    @Test
    void testGetTile_ShouldFailedVectorCauzRowExpectedIsOff() {
        Vector2 vector2 = new Vector2(2,0);
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        Tile tileTest = chunk.getTile(vector2);

        assertThat(tileTest).isNull();
    }

    @Test
    void testGetTileCode_ShouldSucceedWithGrass() {
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        String tileCode = chunk.getTileCode(0,0);

        assertThat(tileCode).isEqualTo("1");
    }

    @Test
    void testGetTileCode_ShouldSucceedWithWater() {
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.WATER, Media.water01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        String tileCode = chunk.getTileCode(0,0);

        assertThat(tileCode).isEqualTo("0");
    }

    @Test
    void testGetTileCode_ShouldFailedCauzRowExpectedIsOff() {
        Chunk chunk = new Chunk(1,1,1);
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        ArrayList<Tile> tilesBis = new ArrayList<>();
        Tile tile = new Tile(1,1,1, Enums.TILETYPE.GRASS, Media.grass01);
        tilesBis.add(tile);
        tiles.add(tilesBis);
        chunk.setTiles(tiles);

        String tileCode = chunk.getTileCode(1,0);

        assertThat(tileCode).isEqualTo("0");
    }
}
