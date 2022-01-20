package com.gdx.game.battle;

import com.badlogic.gdx.utils.Json;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.component.ComponentSubject;
import com.gdx.game.entities.Entity;

public class BattleConversation extends ComponentSubject {

    private Json json;

    public BattleConversation() {
        json = new Json();
    }

    public void notifBattleResume(Entity entity) {
        notify(json.toJson(entity.getEntityConfig()), ComponentObserver.ComponentEvent.LOAD_RESUME);
        notify(json.toJson(entity.getEntityConfig()), ComponentObserver.ComponentEvent.SHOW_RESUME);
    }
}
