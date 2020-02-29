package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.Enums.ENTITYSTATE;
import com.gdx.game.box2d.Box2dWorld;
import com.gdx.game.entities.Bird;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.Hero;
import com.gdx.game.map.Island;
import com.gdx.game.map.Tile;

import java.util.ArrayList;
import java.util.Collections;

public class GdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera orthographicCamera;
	private Box2dWorld box2d;
	private Control control;
	private Island island;
	private Hero hero;
	private float time;

	@Override
	public void create() {
		batch = new SpriteBatch();
		box2d = new Box2dWorld();
		island = new Island(box2d);
		Vector3 islandCentrePos3 = island.getCentreTile().getPos3();
		hero = new Hero(islandCentrePos3, box2d);
		Bird bird = new Bird(new Vector3(islandCentrePos3.x - 20, islandCentrePos3.y - 20, 0), box2d, ENTITYSTATE.FLYING);

		island.getEntities().add(hero);
		island.getEntities().add(bird);

		Camera camera = new Camera();
		orthographicCamera = camera.createCamera();
		control = camera.insertControl(orthographicCamera);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// GAME LOGIC
		hero.update(control);

		for(Entity entity: island.getEntities()){
			entity.tick(Gdx.graphics.getDeltaTime());
			entity.currentTile = island.getChunk().getTile(entity.body.getPosition());
			entity.tick(Gdx.graphics.getDeltaTime(), island.getChunk());
		}

		orthographicCamera.position.lerp(hero.getPos3(), .1f);
		orthographicCamera.update();

		Collections.sort(island.getEntities());

		drawGame();
		box2d.tick(orthographicCamera, control);

		time += Gdx.graphics.getDeltaTime();
	}

	private void drawGame() {
		batch.setProjectionMatrix(orthographicCamera.combined);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.begin();
		// Draw all tiles in the chunk / chunk rows
		for(ArrayList<Tile> rows : island.getChunk().getTiles()){
			for(Tile tile : rows){
				batch.draw(tile.getTexture(), tile.getPos3().x, tile.getPos3().y, tile.getSize(), tile.getSize());
				if (tile.getSecondaryTexture() != null) {
					batch.draw(tile.getSecondaryTexture(), tile.getPos3().x, tile.getPos3().y, tile.getSize(), tile.getSize());
				}
			}
		}

		drawEntities();

		batch.end();
	}

	private void drawEntities() {
		island.getEntities().forEach(e -> e.draw(batch));
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		Media.dispose();
	}
}
