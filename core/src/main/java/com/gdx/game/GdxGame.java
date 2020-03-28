package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.screen.GameScreen;
import com.gdx.game.screen.MenuScreen;

public class GdxGame extends Game {
	private SpriteBatch batch;
	private MenuScreen menuScreen;
	private GameScreen gameScreen;

	public SpriteBatch getBatch() {
		return batch;
	}

	public MenuScreen getMenuScreen() {
		return menuScreen;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	public void create() {
		batch = new SpriteBatch();

		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);

		this.setScreen(menuScreen);
	}

	public void render() {
		super.render();
	}
	
	public void dispose() {
		super.dispose();
		batch.dispose();
		menuScreen.dispose();
		gameScreen.dispose();
		Media.dispose();
	}
}
