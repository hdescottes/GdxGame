package com.gdx.game.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class ConversationUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testConversationUI_ShouldSucceed() {
        ConversationUI conversationUI = new ConversationUI();

        assertThat(conversationUI).isNotNull();
        assertThat(conversationUI.getCloseButton()).isNotNull();
    }

    @Test
    public void testLoadConversation_ShouldSucceed() {
        Json json = new Json();
        String value = "{animationConfig:[{texturePaths:[sprites/characters/Player0.png,sprites/characters/Player1.png],gridPoints:[{x:3},{x:3}]},{animationType:IMMOBILE,texturePaths:[sprites/characters/Player0.png,sprites/characters/Player1.png],gridPoints:[{x:3},{x:3}]}],state:IMMOBILE,entityID:TOWN_FOLK1,conversationConfigPath:conversations/conversation004.json,questConfigPath:quests/quest002.json,currentQuestID:\"\",itemTypeID:NONE}";
        EntityConfig config = json.fromJson(EntityConfig.class, value);
        ConversationUI conversationUI = new ConversationUI();

        conversationUI.loadConversation(config);

        assertThat(conversationUI.getCurrentEntityID()).isEqualTo("TOWN_FOLK1");
        assertThat(conversationUI.getCurrentConversationGraph().getCurrentConversationID()).isEqualTo("1");
    }
}
