package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.screen.GameScreen;

public class GdxGame extends Game {
	private SpriteBatch batch;
	private GameScreen gameScreen;

	public SpriteBatch getBatch() {
		return batch;
	}

	public void create() {
		batch = new SpriteBatch();

		gameScreen = new GameScreen(this);

		this.setScreen(gameScreen);
	}

	public void render() {
		super.render();
	}
	
	public void dispose() {
		super.dispose();
		batch.dispose();
		gameScreen.dispose();
		Media.dispose();
	}
}
