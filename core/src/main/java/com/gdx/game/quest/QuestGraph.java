package com.gdx.game.quest;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import com.badlogic.gdx.utils.Json;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;

public class QuestGraph {

    private Hashtable<String, QuestTask> questTasks = new Hashtable<>();

    private Hashtable<String, ArrayList<QuestTaskDependency>> questTaskDependencies = new Hashtable<>();

    private String questTitle;

    private String questID;

    private boolean isQuestComplete;

    private int goldReward;

    private int xpReward;

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

        // First, see if all tasks are available, meaning no blocking dependencies
        for(QuestTask task : tasks) {
            if (!isQuestTaskAvailable(task.getId())) {
                return false;
            }
            if (!task.isTaskComplete()) {
                if (task.getQuestType().equals(QuestTask.QuestType.RETURN)) {
                    readyTask = task;
                } else {
                    return false;
                }
            }
        }
        if (readyTask == null) {
            return false;
        }
        readyTask.setTaskComplete();
        return true;
    }

    public boolean isQuestTaskAvailable(String id) {
        QuestTask task = getQuestTaskByID(id);
        if (task == null) {
            return false;
        }

        List<QuestTaskDependency> list = questTaskDependencies.get(id);
        for(QuestTaskDependency dep: list) {
            QuestTask depTask = getQuestTaskByID(dep.getDestinationId());
            if (depTask == null || depTask.isTaskComplete()) {
                continue;
            }
            if (dep.getSourceId().equalsIgnoreCase(id)) {
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
