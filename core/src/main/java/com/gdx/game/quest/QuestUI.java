package com.gdx.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class QuestUI extends Window {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestUI.class);

    public static final String RETURN_QUEST = "conversations/return_quest.json";
    public static final String FINISHED_QUEST = "conversations/quest_finished.json";

    private List listQuests;
    private List listTasks;
    private Json json;
    private Array<QuestGraph> quests;
    private Label questLabel;
    private Label tasksLabel;

    public QuestUI() {
        super("Quest Log", ResourceManager.skin);

        json = new Json();
        quests = new Array<>();

        //create
        questLabel = new Label("Quests:", ResourceManager.skin);
        tasksLabel = new Label("Tasks:", ResourceManager.skin);

        listQuests = new List<QuestGraph>(ResourceManager.skin);

        ScrollPane scrollPane = new ScrollPane(listQuests, ResourceManager.skin);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(true, false);

        listTasks = new List<QuestTask>(ResourceManager.skin);

        ScrollPane scrollPaneTasks = new ScrollPane(listTasks, ResourceManager.skin);
        scrollPaneTasks.setOverscroll(false, false);
        scrollPaneTasks.setFadeScrollBars(false);
        scrollPaneTasks.setForceScroll(true, false);

        //layout
        this.add(questLabel).align(Align.left);
        this.add(tasksLabel).align(Align.left);
        this.row();
        this.defaults().expand().fill();
        this.add(scrollPane).padRight(15);
        this.add(scrollPaneTasks).padLeft(5);

        //this.debug();
        this.pack();

        //Listeners
        listQuests.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestGraph quest = (QuestGraph) listQuests.getSelected();
                if(quest == null) {
                    return;
                }
                populateQuestTaskDialog(quest);
            }
        });
    }

    public void questTaskComplete(String questID, String questTaskID) {
        for(QuestGraph questGraph: quests) {
            if(questGraph.getQuestID().equalsIgnoreCase(questID)) {
                if(questGraph.isQuestTaskAvailable(questTaskID)) {
                    questGraph.setQuestTaskComplete(questTaskID);
                } else {
                    return;
                }
            }
        }
    }

    public QuestGraph loadQuest(String questConfigPath) {
        if(questConfigPath.isEmpty() || !Gdx.files.internal(questConfigPath).exists()) {
            LOGGER.debug("Quest file does not exist!");
            return null;
        }

        QuestGraph graph = json.fromJson(QuestGraph.class, Gdx.files.internal(questConfigPath));
        if(doesQuestExist(graph.getQuestID())) {
            return null;
        }

        clearDialog();
        quests.add(graph);
        updateQuestItemList();
        return graph;
    }

    public boolean isQuestReadyForReturn(String questID) {
        if(questID.isEmpty()) {
            LOGGER.debug("Quest ID not valid");
            return false;
        }

        if(!doesQuestExist(questID)) {
            return false;
        }

        QuestGraph graph = getQuestByID(questID);
        if(graph == null) {
            return false;
        }

        if(graph.updateQuestForReturn()) {
            graph.setQuestComplete(true);
        } else {
            return false;
        }
        return true;
    }

    public QuestGraph getQuestByID(String questGraphID) {
        for(QuestGraph questGraph: quests) {
            if(questGraph.getQuestID().equalsIgnoreCase(questGraphID)) {
                return questGraph;
            }
        }
        return null;
    }

    public boolean doesQuestExist(String questGraphID) {
        for(QuestGraph questGraph: quests) {
            if(questGraph.getQuestID().equalsIgnoreCase(questGraphID)) {
                return true;
            }
        }
        return false;
    }


    public Array<QuestGraph> getQuests() {
        return quests;
    }

    public void setQuests(Array<QuestGraph> quests) {
        this.quests = quests;
        updateQuestItemList();
    }

    public void updateQuestItemList() {
        clearDialog();

        listQuests.setItems(quests);
        listQuests.setSelectedIndex(-1);
    }

    private void clearDialog() {
        listQuests.clearItems();
        listTasks.clearItems();
    }

    private void populateQuestTaskDialog(QuestGraph graph) {
        listTasks.clearItems();

        ArrayList<QuestTask> tasks =  graph.getAllQuestTasks();
        if(tasks == null) {
            return;
        }

        listTasks.setItems(tasks.toArray());
        listTasks.setSelectedIndex(-1);
    }

    public void initQuests(MapManager mapMgr) {
        mapMgr.clearAllMapQuestEntities();

        //populate items if quests have them
        for(QuestGraph quest : quests) {
            if(!quest.isQuestComplete()) {
                quest.init(mapMgr);
            }
        }
        ProfileManager.getInstance().setProperty("playerQuests", quests);
    }

    public void updateQuests(MapManager mapMgr) {
        for(QuestGraph quest : quests) {
            if(!quest.isQuestComplete()) {
                quest.update(mapMgr);
            }
        }
        ProfileManager.getInstance().setProperty("playerQuests", quests);
    }

}
