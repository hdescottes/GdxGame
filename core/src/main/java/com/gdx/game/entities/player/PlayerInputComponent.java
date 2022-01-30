package com.gdx.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.component.InputComponent;
import com.gdx.game.entities.Entity;

public class PlayerInputComponent extends InputComponent {

	private Vector3 lastMouseCoordinates;

	// ACTIONS
	private boolean interact;

	// DEBUG
	private boolean debug;

	public PlayerInputComponent() {
		this.lastMouseCoordinates = new Vector3();
	}

	public boolean isInteract() {
		return interact;
	}

	public void setInteract(boolean interact) {
		this.interact = interact;
	}

	@Override
	public void receiveMessage(String message) {
		String[] string = message.split(MESSAGE_TOKEN);

		if(string.length == 0) {
			return;
		}

		if(string.length == 1 && string[0].equalsIgnoreCase(MESSAGE.OPTION_INPUT.toString())) {
			notify("", ComponentObserver.ComponentEvent.OPTION_INPUT);
		}

		//Specifically for messages with 1 object payload
		if(string.length == 2 && string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
			currentDirection = json.fromJson(Entity.Direction.class, string[1]);
		}
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void update(Entity entity, float delta) {
		//Keyboard input
		if(keys.get(Keys.LEFT)) {
			entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT));
		} else if(keys.get(Keys.RIGHT)) {
			entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT));
		} else if(keys.get(Keys.UP)) {
			entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP));
		} else if(keys.get(Keys.DOWN)) {
			entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN));
		} else if(keys.get(Keys.QUIT)) {
			quitReleased();
			Gdx.app.exit();
		} else if(keys.get(Keys.INTERACT)) {
			interactReleased();
			interact = true;
		} else if(keys.get(Keys.OPTION)) {
			entity.sendMessage(MESSAGE.OPTION_INPUT, "");
			optionReleased();
		} else {
			entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE));
			if(currentDirection == null) {
				entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN));
			}
		}

		//Mouse input
		if(mouseButtons.get(Mouse.SELECT)) {
			entity.sendMessage(MESSAGE.INIT_SELECT_ENTITY, json.toJson(lastMouseCoordinates));
			mouseButtons.put(Mouse.SELECT, false);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.DOWN:
			case Input.Keys.S:
				this.downPressed();
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				this.upPressed();
				break;
			case Input.Keys.LEFT:
			case Input.Keys.A:
				this.leftPressed();
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				this.rightPressed();
				break;
			case Input.Keys.E:
				this.interactPressed();
				break;
			case Input.Keys.O:
				this.optionPressed();
				break;
			case Input.Keys.ESCAPE:
				this.quitPressed();
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.DOWN:
			case Input.Keys.S:
				this.downReleased();
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				this.upReleased();
				break;
			case Input.Keys.LEFT:
			case Input.Keys.A:
				this.leftReleased();
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				this.rightReleased();
				break;
			case Input.Keys.E:
				this.interactReleased();
				break;
			case Input.Keys.O:
				this.optionReleased();
				break;
			case Input.Keys.ESCAPE:
				this.quitReleased();
				break;
			case Input.Keys.BACKSPACE:
				debug = !debug;
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if(button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
			this.setClickedMouseCoordinates(screenX, screenY);
		}

		//left is selection, right is context menu
		if(button == Input.Buttons.LEFT) {
			this.selectMouseButtonPressed(screenX, screenY);
		}
		if(button == Input.Buttons.RIGHT) {
			this.doActionMouseButtonPressed(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//left is selection, right is context menu
		if(button == Input.Buttons.LEFT) {
			this.selectMouseButtonReleased(screenX, screenY);
		}
		if(button == Input.Buttons.RIGHT) {
			this.doActionMouseButtonReleased(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	//Key presses
	public void leftPressed() {
		keys.put(Keys.LEFT, true);
	}
	
	public void rightPressed() {
		keys.put(Keys.RIGHT, true);
	}
	
	public void upPressed() {
		keys.put(Keys.UP, true);
	}
	
	public void downPressed() {
		keys.put(Keys.DOWN, true);
	}

	public void quitPressed() {
		keys.put(Keys.QUIT, true);
	}

	public void interactPressed() {
		keys.put(Keys.INTERACT, true);
	}

	public void optionPressed() {
		keys.put(Keys.OPTION, true);
	}

	public void setClickedMouseCoordinates(int x,int y) {
		lastMouseCoordinates.set(x, y, 0);
	}
	
	public void selectMouseButtonPressed(int x, int y) {
		mouseButtons.put(Mouse.SELECT, true);
	}
	
	public void doActionMouseButtonPressed(int x, int y) {
		mouseButtons.put(Mouse.DOACTION, true);
	}
	
	//Releases
	public void leftReleased() {
		keys.put(Keys.LEFT, false);
	}
	
	public void rightReleased() {
		keys.put(Keys.RIGHT, false);
	}
	
	public void upReleased() {
		keys.put(Keys.UP, false);
	}
	
	public void downReleased() {
		keys.put(Keys.DOWN, false);
	}
	
	public void quitReleased() {
		keys.put(Keys.QUIT, false);
	}

	public void interactReleased() {
		keys.put(Keys.INTERACT, false);
	}

	public void optionReleased() {
		keys.put(Keys.OPTION, false);
	}

	public void selectMouseButtonReleased(int x, int y) {
		mouseButtons.put(Mouse.SELECT, false);
	}
	
	public void doActionMouseButtonReleased(int x, int y) {
		mouseButtons.put(Mouse.DOACTION, false);
	}

	public static void clear() {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
		keys.put(Keys.OPTION, false);
	}
}
