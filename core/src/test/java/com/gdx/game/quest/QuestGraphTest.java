package com.gdx.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.Hashtable;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class QuestGraphTest {

    private MockedConstruction<NPCGraphicsComponent> mockNPCGraphics;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockNPCGraphics = mockConstruction(NPCGraphicsComponent.class);
    }

    @AfterEach
    void end() {
        mockNPCGraphics.close();
    }

    @Test
    public void testIsValid_ShouldSucceed() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue("1", "test");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        boolean isValid = questGraph.isValid("1");

        assertThat(isValid).isTrue();
    }

    @Test
    public void testIsValid_ShouldSucceedWithNullValue() {
        QuestGraph questGraph = new QuestGraph();
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questGraph.setTasks(questTasks);

        boolean isValid = questGraph.isValid("1");

        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @MethodSource("questTask")
    public void testGetQuestTaskById(QuestGraph questGraph, String taskId, String expectedPropertyValue) {
        QuestTask questTask = questGraph.getQuestTaskByID(taskId);
        assertThat(questTask != null ? questTask.getPropertyValue(taskId) : null).isEqualTo(expectedPropertyValue);
    }

    static Stream<Arguments> questTask() {
        return Stream.of(
                Arguments.of(createQuestGraphWithTask("1", "test"), "1", "test"),
                Arguments.of(createQuestGraphWithTask("1", "test"), "2", null)
        );
    }

    private static QuestGraph createQuestGraphWithTask(String taskId, String propertyValue) {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId(taskId);
        questTask.setPropertyValue(taskId, propertyValue);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put(taskId, questTask);
        questGraph.setTasks(questTasks);
        return questGraph;
    }

    @Test
    public void testDoesCycleExist_ShouldSucceedReturnFalse() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        QuestTaskDependency questTaskDependency = new QuestTaskDependency();
        questTaskDependency.setSourceId("1");
        questTaskDependency.setDestinationId("1");

        boolean doesCycleExist = questGraph.doesCycleExist(questTaskDependency);

        assertThat(doesCycleExist).isFalse();
    }

    @Test
    public void testDoesQuestTaskHaveDependencies_ShouldSucceedWithWrongId() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        boolean doesQuestHaveDependencies = questGraph.doesQuestTaskHaveDependencies("2");

        assertThat(doesQuestHaveDependencies).isFalse();
    }

    @Test
    public void testDoesQuestTaskHaveDependencies_ShouldSucceedReturnFalse() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        QuestTaskDependency questTaskDependency = new QuestTaskDependency();
        questTaskDependency.setSourceId("1");
        questTaskDependency.setDestinationId("1");

        boolean doesQuestHaveDependencies = questGraph.doesQuestTaskHaveDependencies("1");

        assertThat(doesQuestHaveDependencies).isFalse();
    }

    @ParameterizedTest
    @MethodSource("updateQuestForReturn")
    public void testDoesUpdateQuestForReturn(QuestGraph questGraph, boolean expectedResult) {
        boolean isQuestReturnable = questGraph.updateQuestForReturn();
        assertThat(isQuestReturnable).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> updateQuestForReturn() {
        return Stream.of(
                // Test case 1: Task is incomplete and of type RETURN
                Arguments.of(createQuestGraphWithTask("1", false, QuestTask.QuestType.RETURN), true),
                // Test case 2: Task is already complete
                Arguments.of(createQuestGraphWithTask("1", true, QuestTask.QuestType.RETURN), false),
                // Test case 3: Task is complete but not of type RETURN
                Arguments.of(createQuestGraphWithTask("1", true, QuestTask.QuestType.DELIVERY), false),
                // Test case 4: Task is not available
                Arguments.of(createQuestGraphWithTask("", true, QuestTask.QuestType.DELIVERY), false)
        );
    }

    private static QuestGraph createQuestGraphWithTask(String taskId, boolean isTaskComplete, QuestTask.QuestType questType) {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId(taskId);
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), String.valueOf(isTaskComplete));
        questTask.setQuestType(questType);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put(taskId, questTask);
        questGraph.setTasks(questTasks);
        return questGraph;
    }

    @ParameterizedTest
    @MethodSource("isQuestReturnable")
    public void testIsQuestTaskAvailable(QuestGraph questGraph, String taskId, boolean expectedResult) {
        boolean isQuestReturnable = questGraph.isQuestTaskAvailable(taskId);
        assertThat(isQuestReturnable).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> isQuestReturnable() {
        String taskId = "1";
        String wrongTaskId = "2";
        return Stream.of(
                Arguments.of(createQuestGraphWithTask(taskId, true), taskId, true),
                Arguments.of(createQuestGraphWithTask(taskId, true), wrongTaskId, false)
        );
    }

    private static QuestGraph createQuestGraphWithTask(String taskId, boolean isTaskComplete) {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId(taskId);
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), String.valueOf(isTaskComplete));
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put(taskId, questTask);
        questGraph.setTasks(questTasks);
        return questGraph;
    }

    @ParameterizedTest
    @MethodSource("updateQuest")
    public void testUpdateQuest(QuestTask.QuestType questType, String isTaskComplete) {
        MapManager mapManager = new MapManager();
        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        ProfileManager.getInstance().setProperty("TOWN_INNKEEPER", new Array<Vector2>());

        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_LOCATION.toString(), "TOPPLE");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString(), EntityFactory.TOWN_INNKEEPER_CONFIG);
        questTask.setQuestType(questType);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        questGraph.update(mapManager);

        assertThat(questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString())).isEqualTo(isTaskComplete);
    }

    private static Stream<Arguments> updateQuest() {
        return Stream.of(
                Arguments.of(QuestTask.QuestType.FETCH, "true"),
                Arguments.of(QuestTask.QuestType.KILL, "false")
        );
    }

    @Test
    public void testInit_ShouldSucceedForQuestTypeFETCH() {
        MapManager mapManager = new MapManager();
        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        ProfileManager.getInstance().setProperty("TOWN_INNKEEPER", new Array<Vector2>());

        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_LOCATION.toString(), "TOPPLE");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString(), EntityFactory.TOWN_INNKEEPER_CONFIG);
        questTask.setQuestType(QuestTask.QuestType.FETCH);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        questGraph.setQuestID("1");
        QuestTaskDependency questTaskDependency = new QuestTaskDependency();
        questTaskDependency.setSourceId("1");
        questTaskDependency.setDestinationId("1");

        questGraph.init(mapManager);

        assertThat(ProfileManager.getInstance().getProperty("TOWN_INNKEEPER", Array.class)).isNotNull();
    }

    @Test
    public void testInit_ShouldSucceedForQuestTypeKILL() {
        MapManager mapManager = new MapManager();
        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        ProfileManager.getInstance().setProperty("TOWN_INNKEEPER", new Array<Vector2>());

        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_LOCATION.toString(), "TOPPLE");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString(), EntityFactory.TOWN_INNKEEPER_CONFIG);
        questTask.setQuestType(QuestTask.QuestType.KILL);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        questGraph.setQuestID("1");

        questGraph.init(mapManager);

        assertThat(ProfileManager.getInstance().getProperty("TOWN_INNKEEPER", Array.class)).isEmpty();
    }
}
