package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.map.Island;
import com.gdx.game.map.Tile;

import java.util.ArrayList;

public class GdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera orthographicCamera;
	private Control control;
	private Island island;

	// Temp x and y co-ords
	int x;
	int y;

	// For Movement
	int directionX;
	int directionY;
	int speed = 1;
	
	@Override
	public void create() {
		//Media.setupImages();
		batch = new SpriteBatch();
		island = new Island();

		Camera camera = new Camera();
		orthographicCamera = camera.createCamera();
		control = camera.insertControl(orthographicCamera);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// GAME LOGIC
		// Reset the direction values
		directionX = 0;
		directionY = 0;

		if(control.isDown()) {
			directionY = -1 ;
		}
		if(control.isUp()) {
			directionY = 1 ;
		}
		if(control.isLeft()) {
			directionX = -1;
		}
		if(control.isRight()) {
			directionX = 1;
		}

		orthographicCamera.position.x += directionX * speed;
		orthographicCamera.position.y += directionY * speed;
		orthographicCamera.update();

		drawGame();
	}

	private void drawGame() {
		batch.setProjectionMatrix(orthographicCamera.combined);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.begin();
		// Draw all tiles in the chunk / chunk rows
		for(ArrayList<Tile> rows : island.getChunk().getTiles()){
			for(Tile tile : rows){
				batch.draw(tile.getTexture(), tile.getPos().x, tile.getPos().y, tile.getSize(), tile.getSize());
				if (tile.getSecondaryTexture() != null) {
					batch.draw(tile.getSecondaryTexture(), tile.getPos().x, tile.getPos().y, tile.getSize(), tile.getSize());
				}
			}
		}
		batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		Media.dispose();
	}
}
