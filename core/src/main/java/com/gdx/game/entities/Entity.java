package com.gdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.game.component.Component;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.component.GraphicsComponent;
import com.gdx.game.component.InputComponent;
import com.gdx.game.component.PhysicsComponent;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;

import java.util.ArrayList;
import java.util.Hashtable;

public class Entity {

	public enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;

		static public Direction getRandomNext() {
			return Direction.values()[MathUtils.random(Direction.values().length - 1)];
		}

		public Direction getOpposite() {
			if(this == LEFT) {
				return RIGHT;
			} else if(this == RIGHT) {
				return LEFT;
			} else if(this == UP) {
				return DOWN;
			} else {
				return UP;
			}
		}
	}

	public enum State {
		IDLE,
		WALKING,
		IMMOBILE;//This should always be last

		static public State getRandomNext() {
			//Ignore IMMOBILE which should be last state
			return State.values()[MathUtils.random(State.values().length - 2)];
		}
	}

	public enum AnimationType {
		WALK_LEFT,
		WALK_RIGHT,
		WALK_UP,
		WALK_DOWN,
		IDLE,
		IMMOBILE,
		LOOK_RIGHT
	}

	public static final int FRAME_WIDTH = 16;
	public static final int FRAME_HEIGHT = 16;
	private static final int MAX_COMPONENTS = 5;

	private Json json;
	private EntityConfig entityConfig;
	private Array<Component> components;
	private InputComponent inputComponent;
	private GraphicsComponent graphicsComponent;
	private PhysicsComponent physicsComponent;

	public Entity(Entity entity) {
		set(entity);
	}

	private Entity set(Entity entity) {
		inputComponent = entity.inputComponent;
		graphicsComponent = entity.graphicsComponent;
		physicsComponent = entity.physicsComponent;

		if(components == null) {
			components = new Array<>(MAX_COMPONENTS);
		}
		components.clear();
		components.add(inputComponent);
		components.add(physicsComponent);
		components.add(graphicsComponent);

		json = entity.json;

		entityConfig = new EntityConfig(entity.entityConfig);
		return this;
	}

	public Entity(InputComponent inputCpnt, PhysicsComponent physicsCpnt, GraphicsComponent graphicsCpnt) {
		entityConfig = new EntityConfig();
		json = new Json();

		components = new Array<>(MAX_COMPONENTS);

		inputComponent = inputCpnt;
		physicsComponent = physicsCpnt;
		graphicsComponent = graphicsCpnt;

		components.add(inputComponent);
		components.add(physicsComponent);
		components.add(graphicsComponent);
	}

	public EntityConfig getEntityConfig() {
		return entityConfig;
	}

	public void sendMessage(Component.MESSAGE messageType, String ... args) {
		String fullMessage = messageType.toString();

		for(String string : args) {
			fullMessage += Component.MESSAGE_TOKEN + string;
		}

		for(Component component: components) {
			component.receiveMessage(fullMessage);
		}
	}

	public void registerObserver(ComponentObserver observer) {
		inputComponent.addObserver(observer);
		physicsComponent.addObserver(observer);
		graphicsComponent.addObserver(observer);
	}

	public void unregisterObservers() {
		inputComponent.removeAllObservers();
		physicsComponent.removeAllObservers();
		graphicsComponent.removeAllObservers();
	}

	public void update(MapManager mapMgr, Batch batch, float delta) {
		inputComponent.update(this, delta);
		physicsComponent.update(this, mapMgr, delta);
		graphicsComponent.update(this, mapMgr, batch, delta);
	}

	public void updateInput(float delta) {
		inputComponent.update(this, delta);
	}

	public void dispose() {
		for(Component component: components) {
			component.dispose();
		}
	}

	public Rectangle getCurrentBoundingBox() {
		return physicsComponent.boundingBox;
	}

	public EntityFactory.EntityName getEntityEncounteredType() {
		return physicsComponent.entityEncounteredType;
	}

	public void setEntityEncounteredType(EntityFactory.EntityName entityName) {
		this.physicsComponent.entityEncounteredType = entityName;
	}

	public Vector2 getCurrentPosition() {
		return graphicsComponent.getCurrentPosition();
	}

	public InputProcessor getInputProcessor() {
		return inputComponent;
	}

	public void setEntityConfig(EntityConfig entityConfig) {
		this.entityConfig = entityConfig;
	}

	public Animation<TextureRegion> getAnimation(AnimationType type) {
		return graphicsComponent.getAnimation(type);
	}

	public static EntityConfig getEntityConfig(String configFilePath) {
		Json json = new Json();
		return json.fromJson(EntityConfig.class, Gdx.files.internal(configFilePath));
	}

	public static Array<EntityConfig> getEntityConfigs(String configFilePath) {
		Json json = new Json();
		Array<EntityConfig> configs = new Array<>();

    	ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));

		for(JsonValue jsonVal : list) {
			configs.add(json.readValue(EntityConfig.class, jsonVal));
		}

		return configs;
	}

	public static EntityConfig loadEntityConfigByPath(String entityConfigPath) {
		EntityConfig entityConfig = Entity.getEntityConfig(entityConfigPath);
		EntityConfig serializedConfig = ProfileManager.getInstance().getProperty(entityConfig.getEntityID(), EntityConfig.class);

		if(serializedConfig == null) {
			return entityConfig;
		} else {
			return serializedConfig;
		}
	}

	public static EntityConfig loadEntityConfig(EntityConfig entityConfig) {
		EntityConfig serializedConfig = ProfileManager.getInstance().getProperty(entityConfig.getEntityID(), EntityConfig.class);

		if(serializedConfig == null) {
			return entityConfig;
		} else {
			return serializedConfig;
		}
	}

	public static Entity initEntity(EntityConfig entityConfig, Vector2 position) {
		Json json = new Json();
		Entity entity = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.NPC);
		entity.setEntityConfig(entityConfig);

		entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
		entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));
		entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.getEntityConfig().getState()));
		entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.getEntityConfig().getDirection()));

		return entity;
	}

	public static Hashtable<String, Entity> initEntities(Array<EntityConfig> configs) {
		Json json = new Json();
		Hashtable<String, Entity > entities = new Hashtable<>();
		for(EntityConfig config: configs ) {
			Entity entity = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.NPC);

			entity.setEntityConfig(config);
			entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
			entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(new Vector2(0,0)));
			entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.getEntityConfig().getState()));
			entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.getEntityConfig().getDirection()));

			entities.put(entity.getEntityConfig().getEntityID(), entity);
		}

		return entities;
	}

	public static Entity initEntity(EntityConfig entityConfig) {
		Json json = new Json();
		Entity entity = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.NPC);
		entity.setEntityConfig(entityConfig);

		entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
		entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(new Vector2(0,0)));
		entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.getEntityConfig().getState()));
		entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.getEntityConfig().getDirection()));

		return entity;
	}


}
