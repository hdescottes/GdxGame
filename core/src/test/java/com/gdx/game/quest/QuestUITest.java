package com.gdx.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class QuestUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testQuestUI_ShouldSucceed() {
        QuestUI questUI = new QuestUI();

        assertThat(questUI).isNotNull();
        assertThat(questUI.getQuests()).isNotNull();
        assertThat(questUI.getQuests()).isEmpty();
    }

    @Test
    public void testQuestTaskComplete_ShouldSucceed() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        questUI.loadQuest(questConfigPath);

        questUI.questTaskComplete("2", "2");

        assertThat(questUI.getQuests().get(0).getQuestTaskByID("2").isTaskComplete()).isTrue();
    }

    @Test
    public void testQuestTaskComplete_ShouldSucceedIsFalse() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        questUI.loadQuest(questConfigPath);

        questUI.questTaskComplete("2", "1");

        assertThat(questUI.getQuests().get(0).getQuestTaskByID("2").isTaskComplete()).isFalse();
    }

    @Test
    public void testLoadQuest_ShouldSucceed() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();

        QuestGraph questGraph = questUI.loadQuest(questConfigPath);

        assertThat(questUI.getQuests()).isNotNull();
        assertThat(questUI.getQuests().size).isEqualTo(1);
        assertThat(questGraph.getQuestTitle()).isEqualTo("Herbs for Mother");
    }

    @Test
    public void testLoadQuest_ShouldSucceedWithEmptyPath() {
        String questConfigPath = "";
        QuestUI questUI = new QuestUI();

        QuestGraph questGraph = questUI.loadQuest(questConfigPath);

        assertThat(questGraph).isNull();
    }

    @Test
    public void testIsQuestReadyForReturn_ShouldSucceed() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        questUI.loadQuest(questConfigPath);
        questUI.getQuests().get(0).setQuestTaskComplete("2");

        boolean isQuestReadyForReturn = questUI.isQuestReadyForReturn("2");

        assertThat(isQuestReadyForReturn).isTrue();
    }

    @Test
    public void testIsQuestReadyForReturn_ShouldSucceedWithEmptyQuestId() {
        QuestUI questUI = new QuestUI();

        boolean isQuestReadyForReturn = questUI.isQuestReadyForReturn("");

        assertThat(isQuestReadyForReturn).isFalse();
    }

    @Test
    public void testIsQuestReadyForReturn_ShouldSucceedWithQuestIdDoesNotExist() {
        QuestUI questUI = new QuestUI();

        boolean isQuestReadyForReturn = questUI.isQuestReadyForReturn("1");

        assertThat(isQuestReadyForReturn).isFalse();
    }

    @Test
    public void testIsQuestReadyForReturn_ShouldSucceedWithQuestNotReady() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        questUI.loadQuest(questConfigPath);

        boolean isQuestReadyForReturn = questUI.isQuestReadyForReturn("2");

        assertThat(isQuestReadyForReturn).isFalse();
    }

    @Test
    public void testGetQuestById_ShouldSucceed() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        QuestGraph questGraph = questUI.loadQuest(questConfigPath);

        QuestGraph questGraph1 = questUI.getQuestByID("2");

        assertThat(questGraph1).isEqualTo(questGraph);
    }

    @Test
    public void testGetQuestById_ShouldSucceedReturnNull() {
        QuestUI questUI = new QuestUI();

        QuestGraph questGraph1 = questUI.getQuestByID("1");

        assertThat(questGraph1).isNull();
    }

    @Test
    public void testDoesQuestExist_ShouldSucceed() {
        String questConfigPath = "quests/quest002.json";
        QuestUI questUI = new QuestUI();
        questUI.loadQuest(questConfigPath);

        boolean doesQuestExist = questUI.doesQuestExist("2");

        assertThat(doesQuestExist).isTrue();
    }

    @Test
    public void testDoesQuestExist_ShouldSucceedReturnFalse() {
        QuestUI questUI = new QuestUI();

        boolean doesQuestExist = questUI.doesQuestExist("1");

        assertThat(doesQuestExist).isFalse();
    }
}
