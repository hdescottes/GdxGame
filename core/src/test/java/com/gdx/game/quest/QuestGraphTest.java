package com.gdx.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class QuestGraphTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
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

    @Test
    public void testGetQuestTaskById_ShouldSucceed() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue("1", "test");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        QuestTask questTask1 = questGraph.getQuestTaskByID("1");

        assertThat(questTask1).isEqualTo(questTask);
    }

    @Test
    public void testGetQuestTaskById_ShouldSucceedWithNonValidId() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue("1", "test");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        QuestTask questTask1 = questGraph.getQuestTaskByID("2");

        assertThat(questTask1).isNull();
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

    @Test
    public void testDoesUpdateQuestForReturn_ShouldSucceed() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "false");
        questTask.setQuestType(QuestTask.QuestType.RETURN);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        QuestTaskDependency questTaskDependency = new QuestTaskDependency();
        questTaskDependency.setSourceId("1");
        questTaskDependency.setDestinationId("1");

        boolean isQuestReturnable = questGraph.updateQuestForReturn();

        assertThat(isQuestReturnable).isTrue();
    }

    @Test
    public void testDoesUpdateQuestForReturn_ShouldSucceedForAlreadyCompleteTask() {
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

        boolean isQuestReturnable = questGraph.updateQuestForReturn();

        assertThat(isQuestReturnable).isFalse();
    }

    @Test
    public void testDoesUpdateQuestForReturn_ShouldSucceedForNonReturnTaskType() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        questTask.setQuestType(QuestTask.QuestType.DELIVERY);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);
        QuestTaskDependency questTaskDependency = new QuestTaskDependency();
        questTaskDependency.setSourceId("1");
        questTaskDependency.setDestinationId("1");

        boolean isQuestReturnable = questGraph.updateQuestForReturn();

        assertThat(isQuestReturnable).isFalse();
    }

    @Test
    public void testDoesUpdateQuestForReturn_ShouldSucceedForNonAvailableTask() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        questTask.setQuestType(QuestTask.QuestType.DELIVERY);
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        boolean isQuestReturnable = questGraph.updateQuestForReturn();

        assertThat(isQuestReturnable).isFalse();
    }

    @Test
    public void testIsQuestTypeAvailable_ShouldSucceed() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        boolean isQuestReturnable = questGraph.isQuestTaskAvailable("1");

        assertThat(isQuestReturnable).isTrue();
    }

    @Test
    public void testIsQuestTypeAvailable_ShouldSucceedWithWrongTaskId() {
        QuestGraph questGraph = new QuestGraph();
        QuestTask questTask = new QuestTask();
        questTask.setId("1");
        questTask.setPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "true");
        Hashtable<String, QuestTask> questTasks = new Hashtable<>();
        questTasks.put("1", questTask);
        questGraph.setTasks(questTasks);

        boolean isQuestReturnable = questGraph.isQuestTaskAvailable("2");

        assertThat(isQuestReturnable).isFalse();
    }

    @Disabled
    @Test
    public void testUpdate_ShouldSucceedForQuestTypeFETCH() {
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

        questGraph.update(mapManager);

        assertThat(questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString())).isEqualTo("true");
    }

    @Disabled
    @Test
    public void testUpdate_ShouldSucceedForQuestTypeKILL() {
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

        questGraph.update(mapManager);

        assertThat(questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString())).isEqualTo("false");
    }

    @Disabled("Need to set up item spawn layer")
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

    @Disabled
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
