package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gdx.game.GdxGame;
import com.gdx.game.Media;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.Bird;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityEnums;
import com.gdx.game.entities.Hero;
import com.gdx.game.entities.Rabite;
import com.gdx.game.manager.CameraManager;
import com.gdx.game.manager.ControlManager;
import com.gdx.game.map.Island;
import com.gdx.game.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameScreen extends BaseScreen {

    private Box2dWorld box2d;
    private ControlManager controlManager;
    private Island island;
    private Hero hero;

    public GameScreen(GdxGame gdxGame) {
        super(gdxGame);

        box2d = new Box2dWorld();
        island = new Island(box2d);
        Vector3 islandCentrePos3 = island.getCentreTile().getPos3();
        hero = new Hero(islandCentrePos3, box2d, EntityEnums.ENTITYSTATE.WALKING_DOWN);
        Bird bird = new Bird(new Vector3(islandCentrePos3.x - 20, islandCentrePos3.y - 20, 0), box2d, EntityEnums.ENTITYSTATE.FLYING);
        Rabite rabite = new Rabite(new Vector3(islandCentrePos3.x - 10, islandCentrePos3.y - 10, 0), box2d, EntityEnums.ENTITYSTATE.IDLE);

        island.getEntities().add(hero);
        island.getEntities().add(bird);
        island.getEntities().add(rabite);

        // HashMap of Entities for collisions
        box2d.populateEntityMap(island.getEntities());
    }

    @Override
    public void show() {
        CameraManager cameraManager = new CameraManager();
        controlManager = cameraManager.insertControl(getGameCam());
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

        getGameCam().position.lerp(hero.getPos3(), .1f);
        getGameCam().update();

        Collections.sort(island.getEntities());

        drawGame();
        box2d.tick(getGameCam(), controlManager);

        delta += Gdx.graphics.getDeltaTime();

        if(hero.isCollision()) {
            if(hero.getEntityCollision().getType() == EntityEnums.ENTITYTYPE.ENEMY) {
                HashMap<String, Entity> entityMap = new HashMap<>();
                entityMap.put("hero", hero);
                entityMap.put("enemy", hero.getEntityCollision());
                gdxGame.setEntityMap(entityMap);
                gdxGame.setScreen(new BattleScreen(gdxGame));
            }
        }

        island.clearRemovedEntities(box2d);

        if(controlManager.isOption()) {
            Image screenShot = new Image(ScreenUtils.getFrameBufferTexture());
            gdxGame.setScreen(new OptionScreen(gdxGame, gdxGame.getScreen(), screenShot));
        }
    }

    private void drawGame() {
        gdxGame.getBatch().setProjectionMatrix(getGameCam().combined);
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
