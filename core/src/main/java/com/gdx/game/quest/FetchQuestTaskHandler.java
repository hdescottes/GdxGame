package com.gdx.game.quest;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchQuestTaskHandler implements QuestTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestGraph.class);

    @Override
    public void handleUpdate(QuestTask questTask, String questID) {
        String taskConfig = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString());
        if (taskConfig == null || taskConfig.isEmpty())
            return;
        EntityConfig config = Entity.getEntityConfig(taskConfig);

        Array<Vector2> questItemPositions = ProfileManager.getInstance().getProperty(config.getEntityID(), Array.class);
        //Case where all the items have been picked up
        if (questItemPositions != null && questItemPositions.size == 0) {
            questTask.setTaskComplete();
            LOGGER.debug("TASK : {} is complete of Quest: {}", questTask.getId(), questID);
            LOGGER.debug("INFO : {}", QuestTask.QuestTaskPropertyType.TARGET_TYPE);
        }
    }

    @Override
    public void handleInit(MapManager mapManager, QuestTask questTask, String questID) {

        String taskConfig = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString());
        if (taskConfig == null || taskConfig.isEmpty())
            return;

        Array<Entity> questEntities = new Array<>();
        Array<Vector2> positions = mapManager.getQuestItemSpawnPositions(questID, questTask.getId());
        EntityConfig config = Entity.getEntityConfig(taskConfig);
        Array<Vector2> questItemPositions = ProfileManager.getInstance().getProperty(config.getEntityID(), Array.class);

        if (questItemPositions == null) {
            questItemPositions = new Array<>(positions);
            addQuestEntities(config, positions, questID, questEntities);
        } else {
            addQuestEntities(config, positions, questID, questEntities);
        }

        mapManager.addMapQuestEntities(questEntities);
        ProfileManager.getInstance().setProperty(config.getEntityID(), questItemPositions);
    }

    private void addQuestEntities(EntityConfig config, Array<Vector2> positions, String questID, Array<Entity> questEntities) {
        for (Vector2 position : positions) {
            Entity entity = Entity.initEntity(config, position);
            entity.getEntityConfig().setCurrentQuestID(questID);
            questEntities.add(entity);
        }
    }

}