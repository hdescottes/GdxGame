package com.gdx.game.map.worldMap;

import com.badlogic.gdx.math.Vector2;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.component.Component;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapFactory;
import com.gdx.game.profile.ProfileManager;

public class ToppleRoad1 extends Map {

    private static String mapPath = "asset/map/Topple_Road_1.tmx";
    private static final String TEST_THEME = AudioObserver.AudioTypeEvent.TEST_THEME.getValue();

    public ToppleRoad1() {
        super(MapFactory.MapType.TOPPLE_ROAD_1, mapPath);

        //Special cases
        Entity rabite = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.RABITE);
        initSpecialEntityPosition(rabite);
        mapEntities.add(rabite);

    }

    public String getMusicTheme() {
        return TEST_THEME;
    }

    @Override
    public void unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.TEST_THEME);
    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.TEST_THEME);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.TEST_THEME);
    }

    private void initSpecialEntityPosition(Entity entity) {
        Vector2 position = new Vector2(0,0);

        if(enemyStartPositions.containsKey(entity.getEntityConfig().getEntityID())) {
            position = enemyStartPositions.get(entity.getEntityConfig().getEntityID());
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));

        //Overwrite default if special config is found
        EntityConfig entityConfig = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), EntityConfig.class);
        if(entityConfig != null ) {
            entity.setEntityConfig(entityConfig);
        }
    }
}
