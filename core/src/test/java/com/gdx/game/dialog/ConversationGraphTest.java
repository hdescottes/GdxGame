package com.gdx.game.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GdxRunner.class)
public class ConversationGraphTest {

    @Test
    public void testSetConversations_ShouldSucceed() {
        Conversation conversation1 = new Conversation();
        conversation1.setId("1");
        conversation1.setDialog("Ok");
        Conversation conversation2 = new Conversation();
        conversation2.setId("2");
        conversation2.setDialog("Can you please help me mister? My mother is sick but I can't find the herbs she needs around town!");
        Hashtable<String, Conversation> conversations = new Hashtable<>();
        conversations.put("1", conversation1);
        conversations.put("2", conversation2);
        ConversationGraph graph = new ConversationGraph();

        graph.setConversations(conversations);
        graph.setCurrentConversation("1");

        assertThat(graph.getCurrentChoices()).isEmpty();
    }

    @Test
    public void testSetCurrentConversation_ShouldSucceed() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        graph.setCurrentConversation("1");

        assertEquals(graph.getCurrentConversationID(), "1");
    }

    @Test
    public void testIsValid_ShouldSucceedIsTrue() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        boolean isValid = graph.isValid("1");

        assertThat(isValid).isTrue();
    }

    @Test
    public void testIsValid_ShouldSucceedIsFalse() {
        Hashtable<String, Conversation> conversations = new Hashtable<>();
        ConversationGraph graph = new ConversationGraph();
        graph.setConversations(conversations);

        boolean isValid = graph.isValid("1");

        assertThat(isValid).isFalse();
    }

    @Test
    public void testIsReachable_ShouldSucceedIsNotValid() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        boolean isReachable = graph.isReachable("5", "1");

        assertThat(isReachable).isFalse();
    }

    @Test
    public void testIsReachable_ShouldSucceedReturnTrue() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        boolean isReachable = graph.isReachable("1", "2");

        assertThat(isReachable).isTrue();
    }

    @Test
    public void testIsReachable_ShouldSucceedReturnFalse() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        boolean isReachable = graph.isReachable("1", "1");

        assertThat(isReachable).isFalse();
    }

    @Test
    public void testGetConversationById_ShouldSucceedReturnNull() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        Conversation conversation = graph.getConversationByID("5");

        assertThat(conversation).isNull();
    }

    @Test
    public void testGetConversationById_ShouldSucceed() {
        Json json = new Json();
        String fullFilenamePath = "conversations/conversation004.json";
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));

        Conversation conversation = graph.getConversationByID("1");

        assertThat(conversation).isNotNull();
    }
}
