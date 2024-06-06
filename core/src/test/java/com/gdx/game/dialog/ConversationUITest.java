package com.gdx.game.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

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

        assertThat("TOWN_FOLK1").isEqualTo(conversationUI.getCurrentEntityID());
        assertThat("1").isEqualTo(conversationUI.getCurrentConversationGraph().getCurrentConversationID());
    }

    @Test
    public void testLoadCourtesyConversation_ShouldSucceed() {
        Json json = new Json();
        String value = "{animationConfig:[{texturePaths:[sprites/characters/Player0.png,sprites/characters/Player1.png],gridPoints:[{x:3},{x:3}]},{animationType:IMMOBILE,texturePaths:[sprites/characters/Player0.png,sprites/characters/Player1.png],gridPoints:[{x:3},{x:3}]}],state:IMMOBILE,entityID:TOWN_INNKEEPER,conversationConfigPath:\"\",questConfigPath:\"\",currentQuestID:\"\",itemTypeID:NONE}";
        EntityConfig config = json.fromJson(EntityConfig.class, value);
        ConversationUI conversationUI = new ConversationUI();
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal("conversations/conversation_courtesy.json"));

        conversationUI.loadConversation(config);
        String conversationID = conversationUI.getCurrentConversationGraph().getCurrentConversationID();
        String dialog = graph.getConversations().get(conversationID).getDialog();

        assertThat("TOWN_INNKEEPER").isEqualTo(conversationUI.getCurrentEntityID());
        assertThat(dialog).isEqualTo(conversationUI.getCurrentConversationGraph().displayCurrentConversation());
    }
}
