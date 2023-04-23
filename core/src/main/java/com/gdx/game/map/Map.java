package com.gdx.game.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.audio.AudioManager;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.audio.AudioSubject;
import com.gdx.game.entities.Entity;
import com.gdx.game.manager.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public abstract class Map implements AudioSubject {

    private static final Logger LOGGER = LoggerFactory.getLogger(Map.class);

    private Array<AudioObserver> observers;

    public static final float UNIT_SCALE  = 1/16f;

    //Map layers
    protected static final String COLLISION_LAYER = "MAP_COLLISION_LAYER";
    protected static final String SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    protected static final String PORTAL_LAYER = "MAP_PORTAL_LAYER";
    protected static final String QUEST_ITEM_SPAWN_LAYER = "MAP_QUEST_ITEM_SPAWN_LAYER";
    protected static final String QUEST_DISCOVER_LAYER = "MAP_QUEST_DISCOVER_LAYER";
    protected static final String ENEMY_SPAWN_LAYER = "MAP_ENEMY_SPAWN_LAYER";
    protected static final String PARTICLE_EFFECT_SPAWN_LAYER = "PARTICLE_EFFECT_SPAWN_LAYER";

    public static final String BACKGROUND_LAYER = "Background_Layer";
    public static final String GROUND_LAYER = "Ground_Layer";
    public static final String DECORATION_LAYER = "Decoration_Layer";

    //Starting locations
    protected static final String PLAYER_START = "PLAYER_START";
    protected static final String NPC_START = "NPC_START";
    protected static final String ENEMY_SPAWN = "ENEMY_SPAWN";

    protected Json json;

    protected Vector2 playerStartPositionRect;
    protected Vector2 closestPlayerStartPosition;
    protected Vector2 convertedUnits;
    protected TiledMap currentMap = null;
    protected Vector2 playerStart;
    protected Array<Vector2> npcStartPositions;
    protected Hashtable<String, Vector2> enemyStartPositions;
    protected Hashtable<String, Vector2> specialNPCStartPositions;

    protected MapLayer collisionLayer = null;
    protected MapLayer portalLayer = null;
    protected MapLayer spawnsLayer = null;
    protected MapLayer questItemSpawnLayer = null;
    protected MapLayer questDiscoverLayer = null;
    protected MapLayer enemySpawnLayer = null;

    protected MapFactory.MapType currentMapType;
    protected Array<Entity> mapEntities;
    protected Array<Entity> mapQuestEntities;

    private AudioObserver.AudioTypeEvent musicTheme;

    public Map(MapFactory.MapType mapType, String fullMapPath) {
        json = new Json();
        mapEntities = new Array<>(10);
        observers = new Array<>();
        mapQuestEntities = new Array<>();
        currentMapType = mapType;
        playerStart = new Vector2(0,0);
        playerStartPositionRect = new Vector2(0,0);
        closestPlayerStartPosition = new Vector2(0,0);
        convertedUnits = new Vector2(0,0);

        if (fullMapPath == null || fullMapPath.isEmpty()) {
            LOGGER.debug("Map is invalid");
            return;
        }

        ResourceManager.loadMapAsset(fullMapPath);
        if (ResourceManager.isAssetLoaded(fullMapPath)) {
            currentMap = ResourceManager.getMapAsset(fullMapPath);
        } else {
            LOGGER.debug("Map not loaded");
            return;
        }

        collisionLayer = currentMap.getLayers().get(COLLISION_LAYER);
        if (collisionLayer == null) {
            LOGGER.debug("No collision layer!");
        }

        portalLayer = currentMap.getLayers().get(PORTAL_LAYER);
        if (portalLayer == null) {
            LOGGER.debug("No portal layer!");
        }

        spawnsLayer = currentMap.getLayers().get(SPAWNS_LAYER);
        if (spawnsLayer == null) {
            LOGGER.debug("No spawn layer!");
        } else {
            setClosestStartPosition(playerStart);
        }

        questItemSpawnLayer = currentMap.getLayers().get(QUEST_ITEM_SPAWN_LAYER);
        if (questItemSpawnLayer == null) {
            LOGGER.debug("No quest item spawn layer!");
        }

        questDiscoverLayer = currentMap.getLayers().get(QUEST_DISCOVER_LAYER);
        if (questDiscoverLayer == null) {
            LOGGER.debug("No quest discover layer!");
        }

        enemySpawnLayer = currentMap.getLayers().get(ENEMY_SPAWN_LAYER);
        if (enemySpawnLayer == null) {
            LOGGER.debug("No enemy layer found!");
        } else {
            enemyStartPositions = getEnemyStartPositions();
        }

        npcStartPositions = getNPCStartPositions();
        specialNPCStartPositions = getSpecialNPCStartPositions();

        //Observers
        this.addObserver(AudioManager.getInstance());
    }

    public AudioObserver.AudioTypeEvent getMusicTheme() {
        return musicTheme;
    }

    public Array<Vector2> getQuestItemSpawnPositions(String objectName, String objectTaskID) {
        Array<Vector2> positions = new Array<>();

        for(MapObject object: questItemSpawnLayer.getObjects()) {
            String name = object.getName();
            String taskID = (String)object.getProperties().get("taskID");

            if (name == null || taskID == null || name.isEmpty() || taskID.isEmpty() ||
                    !name.equalsIgnoreCase(objectName) || !taskID.equalsIgnoreCase(objectTaskID)) {
                continue;
            }
            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            positions.add(new Vector2(x,y));
        }
        return positions;
    }

    public Array<Entity> getMapEntities() {
        return mapEntities;
    }

    public Array<Entity> getMapQuestEntities() {
        return mapQuestEntities;
    }

    public void addMapQuestEntities(Array<Entity> entities) {
        mapQuestEntities.addAll(entities);
    }

    public MapFactory.MapType getCurrentMapType() {
        return currentMapType;
    }

    public Vector2 getPlayerStart() {
        return playerStart;
    }

    public void setPlayerStart(Vector2 playerStart) {
        this.playerStart = playerStart;
    }

    protected void updateMapEntities(MapManager mapMgr, Batch batch, float delta) {
        for(int i = 0; i < mapEntities.size; i++) {
            mapEntities.get(i).update(mapMgr, batch, delta);
        }
        for(int i = 0; i < mapQuestEntities.size; i++) {
            mapQuestEntities.get(i).update(mapMgr, batch, delta);
        }
    }

    protected void dispose() {
        for(int i = 0; i < mapEntities.size; i++) {
            mapEntities.get(i).dispose();
        }
        for(int i = 0; i < mapQuestEntities.size; i++) {
            mapQuestEntities.get(i).dispose();
        }
    }

    public MapLayer getCollisionLayer() {
        return collisionLayer;
    }

    public MapLayer getPortalLayer() {
        return portalLayer;
    }

    public MapLayer getQuestItemSpawnLayer() {
        return questItemSpawnLayer;
    }

    public MapLayer getQuestDiscoverLayer() {
        return questDiscoverLayer;
    }

    public MapLayer getEnemySpawnLayer() {
        return enemySpawnLayer;
    }

    public TiledMap getCurrentTiledMap() {
        return currentMap;
    }

    public Vector2 getPlayerStartUnitScaled() {
        Vector2 playerStart = this.playerStart.cpy();
        playerStart.set(this.playerStart.x * UNIT_SCALE, this.playerStart.y * UNIT_SCALE);
        return playerStart;
    }

    private Array<Vector2> getNPCStartPositions() {
        Array<Vector2> npcStartPositions = new Array<>();

        for(MapObject object: spawnsLayer.getObjects()) {
            String objectName = object.getName();

            if (objectName == null || objectName.isEmpty()) {
                continue;
            }

            if (objectName.equalsIgnoreCase(NPC_START)) {
                //Get center of rectangle
                float x = ((RectangleMapObject)object).getRectangle().getX();
                float y = ((RectangleMapObject)object).getRectangle().getY();

                //scale by the unit to convert from map coordinates
                x *= UNIT_SCALE;
                y *= UNIT_SCALE;

                npcStartPositions.add(new Vector2(x,y));
            }
        }
        return npcStartPositions;
    }

    private Hashtable<String, Vector2> getEnemyStartPositions() {
        Hashtable<String, Vector2> enemyStartPositions = new Hashtable<>();

        for(MapObject object: enemySpawnLayer.getObjects()) {
            String objectName = object.getName();

            if (objectName == null || objectName.isEmpty()) {
                continue;
            }

            //This is meant for all the special spawn locations, a catch all, so ignore known ones
            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            enemyStartPositions.put(objectName, new Vector2(x,y));
        }
        return enemyStartPositions;
    }

    private Hashtable<String, Vector2> getSpecialNPCStartPositions() {
        Hashtable<String, Vector2> specialNPCStartPositions = new Hashtable<>();

        for(MapObject object: spawnsLayer.getObjects()) {
            String objectName = object.getName();

            if (objectName == null || objectName.isEmpty()) {
                continue;
            }

            //This is meant for all the special spawn locations, a catch all, so ignore known ones
            if (objectName.equalsIgnoreCase(NPC_START) || objectName.equalsIgnoreCase(PLAYER_START)) {
                continue;
            }

            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            specialNPCStartPositions.put(objectName, new Vector2(x,y));
        }
        return specialNPCStartPositions;
    }

    private void setClosestStartPosition(final Vector2 position) {
         LOGGER.debug("setClosestStartPosition INPUT: ({},{}) {}", position.x, position.y, currentMapType.toString());

        //Get last known position on this map
        playerStartPositionRect.set(0,0);
        closestPlayerStartPosition.set(0,0);
        float shortestDistance = 0f;

        //Go through all player start positions and choose closest to last known position
        for(MapObject object: spawnsLayer.getObjects()) {
            String objectName = object.getName();

            if (objectName == null || objectName.isEmpty()) {
                continue;
            }

            if (objectName.equalsIgnoreCase(PLAYER_START)) {
                ((RectangleMapObject)object).getRectangle().getPosition(playerStartPositionRect);
                float distance = position.dst2(playerStartPositionRect);

                LOGGER.debug("DISTANCE: {} for {}", distance, currentMapType.toString());

                if (distance < shortestDistance || shortestDistance == 0) {
                    closestPlayerStartPosition.set(playerStartPositionRect);
                    shortestDistance = distance;
                    LOGGER.debug("closest START is: ({},{}) {}", closestPlayerStartPosition.x, closestPlayerStartPosition.y,  currentMapType.toString());
                }
            }
        }
        playerStart =  closestPlayerStartPosition.cpy();
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 position) {
        convertedUnits.set(position.x/UNIT_SCALE, position.y/UNIT_SCALE);
        setClosestStartPosition(convertedUnits);
    }

    public abstract void unloadMusic();
    public abstract void loadMusic();

    @Override
    public void addObserver(AudioObserver audioObserver) {
        observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: observers) {
            observer.onNotify(command, event);
        }
    }
}
