package com.gdx.game.quest;

import com.gdx.game.map.MapManager;

public interface QuestTaskHandler {

    void handleUpdate(QuestTask questTask, String questID);

    void handleInit(MapManager mapManager, QuestTask questTask, String questID);

}
