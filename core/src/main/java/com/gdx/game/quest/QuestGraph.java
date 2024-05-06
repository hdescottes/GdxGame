package com.gdx.game.quest;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QuestGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestGraph.class);

    private Hashtable<String, QuestTask> questTasks;
    private Hashtable<String, ArrayList<QuestTaskDependency>> questTaskDependencies;
    private String questTitle;
    private String questID;
    private boolean isQuestComplete;
    private int goldReward;
    private int xpReward;

    public QuestGraph() {
        questTasks = new Hashtable<>();
        questTaskDependencies = new Hashtable<>();
    }

    public int getGoldReward() {
        return goldReward;
    }

    public void setGoldReward(int goldReward) {
        this.goldReward = goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public boolean isQuestComplete() {
        return isQuestComplete;
    }

    public void setQuestComplete(boolean isQuestComplete) {
        this.isQuestComplete = isQuestComplete;
    }

    public String getQuestID() {
        return questID;
    }

    public void setQuestID(String questID) {
        this.questID = questID;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public void setQuestTitle(String questTitle) {
        this.questTitle = questTitle;
    }

    public boolean areAllTasksComplete() {
        ArrayList<QuestTask> tasks = getAllQuestTasks();
        for(QuestTask task: tasks) {
            if (!task.isTaskComplete()) {
                return false;
            }
        }
        return true;
    }

    public void setTasks(Hashtable<String, QuestTask> questTasks) {
        this.questTasks = questTasks;
        this.questTaskDependencies = new Hashtable<>(questTasks.size());

        for(QuestTask questTask: questTasks.values()) {
            questTaskDependencies.put(questTask.getId(), new ArrayList<>());
        }
    }

    public ArrayList<QuestTask> getAllQuestTasks() {
        Enumeration<QuestTask> enumeration = questTasks.elements();
        return Collections.list(enumeration);
    }

    public void clear() {
        questTasks.clear();
        questTaskDependencies.clear();
    }

    public boolean isValid(String taskID) {
        QuestTask questTask = questTasks.get(taskID);
        return questTask != null;
    }

    public boolean isReachable(String sourceID, String sinkID) {
        Set<String> reachableNodes = new HashSet<>();
        return isReachable(sourceID, sinkID, reachableNodes);
    }

    private boolean isReachable(String sourceId, String targetId, Set<String> reachableNodes) {
        if (sourceId.equals(targetId))
            return true;

        reachableNodes.add(sourceId);
        List<QuestTaskDependency> dependencies = questTaskDependencies.computeIfAbsent(sourceId, k -> new ArrayList<>());
        for (QuestTaskDependency dep : dependencies) {
            String destinationId = dep.getDestinationId();
            if (!reachableNodes.contains(destinationId) && isReachable(destinationId, targetId, reachableNodes))
                return true;
        }

        return false;
    }

    public QuestTask getQuestTaskByID(String id) {
        return (isValid(id)) ? questTasks.get(id) : null;
    }

    public void addDependency(QuestTaskDependency questTaskDependency) {
        String sourceId = questTaskDependency.getSourceId();
        ArrayList<QuestTaskDependency> list = questTaskDependencies.computeIfAbsent(sourceId, k -> new ArrayList<>());
        list.add(questTaskDependency);
    }

    public boolean doesQuestTaskHaveDependencies(String id) {
        QuestTask task = getQuestTaskByID(id);
        if (task == null) {
            return false;
        }
        ArrayList<QuestTaskDependency> list = questTaskDependencies.get(id);

        return !list.isEmpty();
    }

    public boolean updateQuestForReturn() {
        ArrayList<QuestTask> tasks = getAllQuestTasks();
        QuestTask readyTask = null;

        //First, see if all tasks are available, meaning no blocking dependencies
        for(QuestTask task : tasks) {
            if (isQuestTaskAvailable(task.getId()) && !task.isTaskComplete() && task.getQuestType().equals(QuestTask.QuestType.RETURN)) {
                readyTask = task;
                readyTask.setTaskComplete();
            } else {
                return false;
            }
        }

        return readyTask != null;
    }

    public boolean isQuestTaskAvailable(String id) {
        QuestTask task = getQuestTaskByID(id);
        if (task == null)
            return false;

        ArrayList<QuestTaskDependency> list = questTaskDependencies.get(id);
        for (QuestTaskDependency dep : list) {
            QuestTask depTask = getQuestTaskByID(dep.getSourceId());
            if (depTask != null && !depTask.isTaskComplete() && dep.getSourceId().equalsIgnoreCase(task.getId())) {
                return false;
            }
        }

        return true;
    }

    public void setQuestTaskComplete(String id) {
        QuestTask task = getQuestTaskByID(id);
        if (task == null) {
            return;
        }
        task.setTaskComplete();
    }

    public void update(MapManager mapMgr) {
        ArrayList<QuestTask> allQuestTasks = getAllQuestTasks();
        for(QuestTask questTask: allQuestTasks) {

            if (isInvalidQuestTask(questTask, mapMgr)) continue;

            // Determine the handler based on quest type
            QuestTaskHandler handler = getHandlerForQuestType(questTask.getQuestType());
            if (handler != null)
                handler.handleUpdate(questTask, questID);
        }
    }

    private QuestTaskHandler getHandlerForQuestType(QuestTask.QuestType questType) {
        switch (questType) {
            case FETCH:
                return new FetchQuestTaskHandler();
            default:
                return null;
        }
    }

    public void init(MapManager mapMgr) {
        ArrayList<QuestTask> allQuestTasks = getAllQuestTasks();
        for(QuestTask questTask: allQuestTasks) {

            if (isInvalidQuestTask(questTask, mapMgr)) continue;

            // Determine the handler based on quest type
            QuestTaskHandler handler = getHandlerForQuestType(questTask.getQuestType());
            if (handler != null)
                handler.handleInit(mapMgr, questTask, questID);
        }
    }

    private boolean isInvalidQuestTask(QuestTask questTask, MapManager mapManager) {
        if (questTask.isTaskComplete() || !isQuestTaskAvailable(questTask.getId()))
            return true;

        QuestTask.QuestTaskPropertyType questTaskPropertyType = QuestTask.QuestTaskPropertyType.TARGET_LOCATION;
        String taskLocation = questTask.getPropertyValue(questTaskPropertyType.toString());
        MapFactory.MapType mapType = mapManager.getCurrentMapType();

        return taskLocation == null || taskLocation.isEmpty() || !taskLocation.equalsIgnoreCase(mapType.toString());
    }

    public String toString() {
        return questTitle;
    }

    public String toJson() {
        Json json = new Json();
        return json.prettyPrint(this);
    }

}
