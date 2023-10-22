package com.gdx.game.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Hashtable;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("loadConversation")
    public void testIsReachable(String fullFilenamePath, String sourceNode, String destinationNode, boolean expectedResult) {
        Json json = new Json();
        ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));
        boolean isReachable = graph.isReachable(sourceNode, destinationNode);
        assertThat(isReachable).isEqualTo(expectedResult);
    }

    static Stream<Arguments> loadConversation() {
        return Stream.of(
                // Test case 1: Source node is not valid
                Arguments.of("conversations/conversation004.json", "5", "1", false),
                // Test case 2: Valid path exists
                Arguments.of("conversations/conversation004.json", "1", "2", true),
                // Test case 3: No path exists between the same node
                Arguments.of("conversations/conversation004.json", "1", "1", false)
        );
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
