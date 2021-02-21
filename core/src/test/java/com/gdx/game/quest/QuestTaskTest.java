package com.gdx.game.quest;

import com.badlogic.gdx.utils.ObjectMap;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(GdxRunner.class)
public class QuestTaskTest {

    @Test
    public void testIsTaskComplete_ShouldSucceed() {
        QuestTask questTask = new QuestTask();
        ObjectMap<String, Object> taskProperties = questTask.getTaskProperties();
        taskProperties.put(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), true);

        boolean isQuestTaskComplete = questTask.isTaskComplete();

        assertThat(isQuestTaskComplete).isTrue();
    }

    @Test
    public void testIsTaskComplete_ShouldSucceedReturnFalse() {
        QuestTask questTask = new QuestTask();

        boolean isQuestTaskComplete = questTask.isTaskComplete();

        assertThat(isQuestTaskComplete).isFalse();
    }

    @Test
    public void testGetPropertyValue_ShouldSucceed() {
        QuestTask questTask = new QuestTask();
        ObjectMap<String, Object> taskProperties = questTask.getTaskProperties();
        taskProperties.put(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), true);

        String propertyValue = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString());

        assertThat(propertyValue).isEqualTo("true");
    }

    @Test
    public void testGetPropertyValue_ShouldSucceedWhenNullValue() {
        QuestTask questTask = new QuestTask();
        ObjectMap<String, Object> taskProperties = questTask.getTaskProperties();
        taskProperties.put(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), null);

        String propertyValue = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.IS_TASK_COMPLETE.toString());

        assertThat(propertyValue).isEmpty();
    }

}
