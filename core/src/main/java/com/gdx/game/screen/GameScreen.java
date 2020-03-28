package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.Enums;
import com.gdx.game.GdxGame;
import com.gdx.game.Media;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.Bird;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.Hero;
import com.gdx.game.manager.CameraManager;
import com.gdx.game.manager.ControlManager;
import com.gdx.game.map.Island;
import com.gdx.game.map.Tile;

import java.util.ArrayList;
import java.util.Collections;

public class GameScreen extends AbstractScreen {
    private Box2dWorld box2d;
    private ControlManager controlManager;
    private Island island;
    private Hero hero;

    public GameScreen(GdxGame gdxGame) {
        super(gdxGame);

        box2d = new Box2dWorld();
        island = new Island(box2d);
        Vector3 islandCentrePos3 = island.getCentreTile().getPos3();
        hero = new Hero(islandCentrePos3, box2d, Enums.ENTITYSTATE.WALKING_DOWN);
        Bird bird = new Bird(new Vector3(islandCentrePos3.x - 20, islandCentrePos3.y - 20, 0), box2d, Enums.ENTITYSTATE.FLYING);

        island.getEntities().add(hero);
        island.getEntities().add(bird);
    }

    @Override
    public void show() {
        CameraManager cameraManager = new CameraManager();
        controlManager = cameraManager.insertControl(getCam());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // GAME LOGIC
        hero.update(controlManager);

        for(Entity entity: island.getEntities()) {
            entity.tick(Gdx.graphics.getDeltaTime());
            entity.currentTile = island.getChunk().getTile(entity.body.getPosition());
            entity.tick(Gdx.graphics.getDeltaTime(), island.getChunk());
        }

        getCam().position.lerp(hero.getPos3(), .1f);
        getCam().update();

        Collections.sort(island.getEntities());

        drawGame();
        box2d.tick(getCam(), controlManager);

        delta += Gdx.graphics.getDeltaTime();
    }

    private void drawGame() {
        gdxGame.getBatch().setProjectionMatrix(getCam().combined);
        gdxGame.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        gdxGame.getBatch().begin();
        // Draw all tiles in the chunk / chunk rows
        for(ArrayList<Tile> rows : island.getChunk().getTiles()) {
            for(Tile tile : rows) {
                gdxGame.getBatch().draw(tile.getTexture(), tile.getPos3().x, tile.getPos3().y, tile.getSize(), tile.getSize());
                if (tile.getSecondaryTexture() != null) {
                    gdxGame.getBatch().draw(tile.getSecondaryTexture(), tile.getPos3().x, tile.getPos3().y, tile.getSize(), tile.getSize());
                }
            }
        }

        drawEntities();

        gdxGame.getBatch().end();
    }

    private void drawEntities() {
        island.getEntities().forEach(e -> e.draw(gdxGame.getBatch()));
    }

    @Override
    public void dispose() {
        super.dispose();
        Media.dispose();
    }
}
