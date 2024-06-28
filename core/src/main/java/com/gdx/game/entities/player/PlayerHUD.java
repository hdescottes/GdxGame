package com.gdx.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.game.audio.AudioManager;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.audio.AudioSubject;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.battle.BattleState;
import com.gdx.game.component.Component;
import com.gdx.game.component.ComponentObserver;
import com.gdx.game.dialog.ConversationGraph;
import com.gdx.game.dialog.ConversationGraphObserver;
import com.gdx.game.dialog.ConversationUI;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.characterClass.ClassObserver;
import com.gdx.game.entities.player.characterClass.tree.Node;
import com.gdx.game.entities.player.characterClass.tree.Tree;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.inventory.InventoryUI;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemLocation;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.inventory.store.StoreInventoryObserver;
import com.gdx.game.inventory.store.StoreInventoryUI;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.profile.ProfileObserver;
import com.gdx.game.quest.QuestGraph;
import com.gdx.game.quest.QuestUI;
import com.gdx.game.status.StatsUpUI;
import com.gdx.game.status.StatusObserver;
import com.gdx.game.status.StatusUI;

import static com.gdx.game.inventory.InventoryUI.getInventory;
import static com.gdx.game.inventory.InventoryUI.setBonusFromSet;

public class PlayerHUD implements Screen, AudioSubject, ProfileObserver, ClassObserver, ComponentObserver, ConversationGraphObserver, BattleObserver, StoreInventoryObserver, InventoryObserver, StatusObserver {

    private Stage stage;
    private Entity player;

    private StatusUI statusUI;
    private InventoryUI inventoryUI;
    private ConversationUI conversationUI;
    private ConversationUI notificationUI;
    private StoreInventoryUI storeInventoryUI;
    private QuestUI questUI;
    private StatsUpUI statsUpUI;

    private Dialog messageBoxUI;
    private Json json;
    private MapManager mapManager;

    private Array<AudioObserver> observers;

    private Actor stageKeyboardFocus;

    private static final String INVENTORY_FULL = "Your inventory is full!";

    public PlayerHUD(Camera cameraHUD, Entity entityPlayer, MapManager mapMgr) {
        player = entityPlayer;
        mapManager = mapMgr;
        Viewport viewport = new ScreenViewport(cameraHUD);
        stage = new Stage(viewport);
        stageKeyboardFocus = stage.getKeyboardFocus();
        //_stage.setDebugAll(true);

        observers = new Array<>();

        json = new Json();
        messageBoxUI = new Dialog("Message", ResourceManager.skin) {
            {
                button("OK");
                text(INVENTORY_FULL);
            }
            @Override
            protected void result(final Object object) {
                cancel();
                setVisible(false);
            }

        };

        messageBoxUI.setVisible(false);
        messageBoxUI.pack();
        messageBoxUI.setPosition(stage.getWidth()/2 - messageBoxUI.getWidth()/2, stage.getHeight()/2 - messageBoxUI.getHeight()/2);

        statusUI = new StatusUI();
        statusUI.setVisible(true);
        statusUI.setPosition(0, 0);
        statusUI.setKeepWithinStage(false);
        statusUI.setMovable(false);

        inventoryUI = new InventoryUI();
        inventoryUI.setKeepWithinStage(false);
        inventoryUI.setMovable(false);
        inventoryUI.setVisible(false);
        inventoryUI.setPosition(statusUI.getWidth(), 0);

        conversationUI = new ConversationUI();
        conversationUI.setMovable(true);
        conversationUI.setVisible(false);
        conversationUI.setPosition(stage.getWidth() / 2, 0);
        conversationUI.setWidth(stage.getWidth() / 2);
        conversationUI.setHeight(stage.getHeight() / 2);

        notificationUI = new ConversationUI();
        notificationUI.removeActor(notificationUI.findActor("scrollPane"));
        notificationUI.getCloseButton().setVisible(false);
        notificationUI.getCloseButton().setTouchable(Touchable.disabled);
        notificationUI.setTitle("");
        notificationUI.setMovable(false);
        notificationUI.setVisible(false);
        notificationUI.setPosition(0, 0);
        notificationUI.setWidth(stage.getWidth());
        notificationUI.setHeight(stage.getHeight() / 5);
        notificationUI.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notificationUI.setVisible(false);
                statusUI.setVisible(true);
            }
        });

        storeInventoryUI = new StoreInventoryUI();
        storeInventoryUI.setMovable(false);
        storeInventoryUI.setVisible(false);
        storeInventoryUI.setPosition(0, 0);

        questUI = new QuestUI();
        questUI.setMovable(false);
        questUI.setVisible(false);
        questUI.setKeepWithinStage(false);
        questUI.setPosition(0, stage.getHeight() / 2);
        questUI.setWidth(stage.getWidth());
        questUI.setHeight(stage.getHeight() / 2);

        stage.addActor(questUI);
        stage.addActor(storeInventoryUI);
        stage.addActor(conversationUI);
        stage.addActor(notificationUI);
        stage.addActor(messageBoxUI);
        stage.addActor(statusUI);
        stage.addActor(inventoryUI);

        questUI.validate();
        storeInventoryUI.validate();
        conversationUI.validate();
        notificationUI.validate();
        messageBoxUI.validate();
        statusUI.validate();
        inventoryUI.validate();

        //add tooltips to the stage
        Array<Actor> actors = inventoryUI.getInventoryActors();
        for(Actor actor : actors) {
            stage.addActor(actor);
        }

        Array<Actor> storeActors = storeInventoryUI.getInventoryActors();
        for(Actor actor : storeActors) {
            stage.addActor(actor);
        }

        //Observers
        player.registerObserver(this);
        statusUI.addObserver(this);
        storeInventoryUI.addObserver(this);
        //inventoryUI.addObserver(battleUI.getCurrentState());
        inventoryUI.addObserver(this);
        //battleUI.getCurrentState().addObserver(this);
        this.addObserver(AudioManager.getInstance());

        //Listeners
        ImageButton inventoryButton = statusUI.getInventoryButton();
        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryUI.setVisible(!inventoryUI.isVisible());
            }
        });

        ImageButton questButton = statusUI.getQuestButton();
        questButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                questUI.setVisible(!questUI.isVisible());
                setInputUI(questUI);
            }
        });

        conversationUI.getCloseButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                conversationUI.setVisible(false);
                mapManager.clearCurrentSelectedMapEntity();
            }
        });

        storeInventoryUI.getCloseButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                storeInventoryUI.savePlayerInventory();
                storeInventoryUI.cleanupStoreInventory();
                storeInventoryUI.setVisible(false);
                mapManager.clearCurrentSelectedMapEntity();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public StatusUI getStatusUI() {
        return statusUI;
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    protected ConversationUI getConversationUI() {
        return conversationUI;
    }

    protected StoreInventoryUI getStoreInventoryUI() {
        return storeInventoryUI;
    }

    public void updateEntityObservers() {
        mapManager.unregisterCurrentMapEntityObservers();
        questUI.initQuests(mapManager);
        mapManager.registerCurrentMapEntityObservers(this);
    }

    private void setInputUI(Window ui) {
        if (ui.isVisible()) {
            Gdx.input.setInputProcessor(stage);
        } else {
            stage.setKeyboardFocus(stageKeyboardFocus);
            InputMultiplexer inputMultiplexer = new InputMultiplexer();
            inputMultiplexer.addProcessor(stage);
            inputMultiplexer.addProcessor(player.getInputProcessor());
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }

    public void setBattleState(BattleState battleState) {
        battleState.addObserver(this);
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
        switch (event) {
            case PROFILE_LOADED -> {
                boolean firstTime = profileManager.getIsNewProfile();
                if (firstTime) {
                    InventoryUI.clearInventoryItems(inventoryUI.getInventorySlotTable());
                    InventoryUI.clearInventoryItems(inventoryUI.getEquipSlotTable());
                    inventoryUI.resetEquipSlots();

                    questUI.setQuests(new Array<>());

                    //add default items if first time
                    Array<InventoryItem.ItemTypeID> items = player.getEntityConfig().getInventory();
                    Array<InventoryItemLocation> itemLocations = new Array<>();
                    for (int i = 0; i < items.size; i++) {
                        itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
                    }
                    InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), itemLocations, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                    profileManager.setProperty("playerInventory", getInventory(inventoryUI.getInventorySlotTable()));

                    //start the player with some money
                    statusUI.setGoldValue(20);
                    statusUI.setStatusForLevel(1);
                } else {
                    int goldVal = profileManager.getProperty("currentPlayerGP", Integer.class);

                    Array<InventoryItemLocation> inventory = profileManager.getProperty("playerInventory", Array.class);
                    InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), inventory, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);

                    Array<InventoryItemLocation> equipInventory = profileManager.getProperty("playerEquipInventory", Array.class);
                    inventoryUI.resetEquipSlots();
                    if (equipInventory != null && equipInventory.size > 0) {
                        InventoryUI.populateInventory(inventoryUI.getEquipSlotTable(), equipInventory, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                        if (inventoryUI.isSetEquipped(equipInventory)) {
                            setBonusFromSet(((InventorySlot) inventoryUI.getEquipSlotTable().getCells().get(1).getActor()).getTopInventoryItem());
                        }
                    }

                    Array<QuestGraph> quests = profileManager.getProperty("playerQuests", Array.class);
                    questUI.setQuests(quests);

                    int xpMaxVal = profileManager.getProperty("currentPlayerXPMax", Integer.class);
                    int xpVal = profileManager.getProperty("currentPlayerXP", Integer.class);

                    int hpMaxVal = profileManager.getProperty("currentPlayerHPMax", Integer.class);
                    int hpVal = profileManager.getProperty("currentPlayerHP", Integer.class);

                    int mpMaxVal = profileManager.getProperty("currentPlayerMPMax", Integer.class);
                    int mpVal = profileManager.getProperty("currentPlayerMP", Integer.class);

                    int levelVal = profileManager.getProperty("currentPlayerLevel", Integer.class);

                    //set the current max values first
                    statusUI.setXPValueMax(xpMaxVal);
                    statusUI.setHPValueMax(hpMaxVal);
                    statusUI.setMPValueMax(mpMaxVal);

                    statusUI.setXPValue(xpVal);
                    statusUI.setHPValue(hpVal);
                    statusUI.setMPValue(mpVal);

                    //then add in current values
                    statusUI.setGoldValue(goldVal);
                    statusUI.setLevelValue(levelVal, false);
                }
            }
            case SAVING_PROFILE -> {
                profileManager.setProperty("playerQuests", questUI.getQuests());
                profileManager.setProperty("playerInventory", getInventory(inventoryUI.getInventorySlotTable()));
                profileManager.setProperty("playerEquipInventory", getInventory(inventoryUI.getEquipSlotTable()));
                if (mapManager.getPlayer() != null) {
                    profileManager.setProperty("playerCharacter", EntityFactory.EntityType.valueOf(mapManager.getPlayer().getEntityConfig().getEntityID()));
                }
                profileManager.setProperty("characterClass", inventoryUI.getCharacterClass());
                profileManager.setProperty("currentPlayerGP", statusUI.getGoldValue());
                profileManager.setProperty("currentPlayerLevel", statusUI.getLevelValue());
                profileManager.setProperty("currentPlayerXP", statusUI.getXPValue());
                profileManager.setProperty("currentPlayerXPMax", statusUI.getXPValueMax());
                if (statusUI.getHPValue() != 0) {
                    profileManager.setProperty("currentPlayerHP", statusUI.getHPValue());
                }
                profileManager.setProperty("currentPlayerHPMax", statusUI.getHPValueMax());
                profileManager.setProperty("currentPlayerMP", statusUI.getMPValue());
                profileManager.setProperty("currentPlayerMPMax", statusUI.getMPValueMax());
                profileManager.setProperty("currentPlayerSPDP", inventoryUI.getSPDPVal());
            }
            case CLEAR_CURRENT_PROFILE -> {
                profileManager.setProperty("playerInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("playerEquipInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("playerCharacter", null);
                profileManager.setProperty("characterClass", null);
                profileManager.setProperty("currentPlayerGP", 0);
                profileManager.setProperty("currentPlayerLevel", 0);
                profileManager.setProperty("currentPlayerXP", 0);
                profileManager.setProperty("currentPlayerXPMax", 0);
                profileManager.setProperty("currentPlayerHP", 0);
                profileManager.setProperty("currentPlayerHPMax", 0);
                profileManager.setProperty("currentPlayerMP", 0);
                profileManager.setProperty("currentPlayerMPMax", 0);
                profileManager.setProperty("currentPlayerAP", 0);
                profileManager.setProperty("currentPlayerDP", 0);
                profileManager.setProperty("currentPlayerSPDP", 0);
                profileManager.setProperty("currentTime", 0);
                profileManager.setProperty("bonusSet", null);
            }
            default -> {
            }
        }
    }

    @Override
    public void onNotify(String value, ComponentEvent event) {
        switch (event) {
            case LOAD_CONVERSATION -> {
                EntityConfig config = json.fromJson(EntityConfig.class, value);

                //Check to see if there is a version loading into properties
                if (config.getItemTypeID().equalsIgnoreCase(InventoryItem.ItemTypeID.NONE.toString())) {
                    EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(config.getEntityID(), EntityConfig.class);
                    if (configReturnProperty != null) {
                        config = configReturnProperty;
                    }
                }
                conversationUI.loadConversation(config);
                conversationUI.getCurrentConversationGraph().addObserver(this);
            }
            case SHOW_CONVERSATION -> {
                EntityConfig configShow = json.fromJson(EntityConfig.class, value);
                if (configShow.getEntityID().equalsIgnoreCase(conversationUI.getCurrentEntityID())) {
                    conversationUI.setVisible(true);
                    setInputUI(conversationUI);
                }
            }
            case HIDE_CONVERSATION -> {
                EntityConfig configHide = json.fromJson(EntityConfig.class, value);
                if (configHide.getEntityID().equalsIgnoreCase(conversationUI.getCurrentEntityID())) {
                    conversationUI.setVisible(false);
                    setInputUI(conversationUI);
                }
            }
            case QUEST_LOCATION_DISCOVERED -> {
                String[] string = value.split(Component.MESSAGE_TOKEN);
                String questID = string[0];
                String questTaskID = string[1];
                questUI.questTaskComplete(questID, questTaskID);
                updateEntityObservers();
            }
            default -> {
            }
        }
    }

    @Override
    public void onNotify(ConversationGraph graph, ConversationCommandEvent event) {
        switch (event) {
            case LOAD_STORE_INVENTORY:
                Entity selectedEntity = mapManager.getCurrentSelectedMapEntity();
                if (selectedEntity == null) {
                    break;
                }

                Array<InventoryItemLocation> inventory =  getInventory(inventoryUI.getInventorySlotTable());
                storeInventoryUI.loadPlayerInventory(inventory);

                Array<InventoryItem.ItemTypeID> items  = selectedEntity.getEntityConfig().getInventory();
                Array<InventoryItemLocation> itemLocations = new Array<>();
                for(int i = 0; i < items.size; i++) {
                    itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.STORE_INVENTORY));
                }

                storeInventoryUI.loadStoreInventory(itemLocations);

                conversationUI.setVisible(false);
                setInputUI(conversationUI);
                storeInventoryUI.toFront();
                storeInventoryUI.setVisible(true);
                break;
            case EXIT_CONVERSATION:
                conversationUI.setVisible(false);
                setInputUI(conversationUI);
                mapManager.clearCurrentSelectedMapEntity();
                break;
            case ACCEPT_QUEST:
                Entity currentlySelectedEntity = mapManager.getCurrentSelectedMapEntity();
                if (currentlySelectedEntity == null) {
                    break;
                }
                EntityConfig config = currentlySelectedEntity.getEntityConfig();
                QuestGraph questGraph = questUI.loadQuest(config.getQuestConfigPath());

                if (questGraph != null) {
                    //Update conversation dialog
                    config.setConversationConfigPath(QuestUI.RETURN_QUEST);
                    config.setCurrentQuestID(questGraph.getQuestID());
                    ProfileManager.getInstance().setProperty(config.getEntityID(), config);
                    updateEntityObservers();
                }

                conversationUI.setVisible(false);
                setInputUI(conversationUI);
                mapManager.clearCurrentSelectedMapEntity();
                break;
            case RETURN_QUEST:
                Entity returnEntity = mapManager.getCurrentSelectedMapEntity();
                if (returnEntity == null) {
                    break;
                }
                EntityConfig configReturn = returnEntity.getEntityConfig();

                EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(configReturn.getEntityID(), EntityConfig.class);
                if (configReturnProperty == null) {
                    return;
                }

                String questID = configReturnProperty.getCurrentQuestID();

                if (questUI.isQuestReadyForReturn(questID)) {
                    //notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                    QuestGraph quest = questUI.getQuestByID(questID);
                    statusUI.addXPValue(quest.getXpReward(), true);
                    statusUI.addGoldValue(quest.getGoldReward());
                    //notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
                    inventoryUI.removeQuestItemFromInventory(questID);
                    questUI.removeQuest(quest);
                    configReturnProperty.setConversationConfigPath(QuestUI.FINISHED_QUEST);

                    ProfileManager.getInstance().setProperty(configReturnProperty.getEntityID(), configReturnProperty);
                    ProfileManager.getInstance().saveProfile();
                }

                conversationUI.setVisible(false);
                setInputUI(conversationUI);
                mapManager.clearCurrentSelectedMapEntity();
                break;
            case ADD_ENTITY_TO_INVENTORY:
                Entity entity = mapManager.getCurrentSelectedMapEntity();
                if (entity == null) {
                    break;
                }

                if (inventoryUI.doesInventoryHaveSpace()) {
                    inventoryUI.addEntityToInventory(entity.getEntityConfig().getItemTypeID(), entity.getEntityConfig().getCurrentQuestID());
                    mapManager.clearCurrentSelectedMapEntity();
                    conversationUI.setVisible(false);
                    setInputUI(conversationUI);
                    entity.unregisterObservers();
                    mapManager.removeMapQuestEntity(entity);
                    questUI.updateQuests(mapManager);
                } else {
                    mapManager.clearCurrentSelectedMapEntity();
                    conversationUI.setVisible(false);
                    setInputUI(conversationUI);
                    messageBoxUI.setVisible(true);
                }
                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, StoreInventoryEvent event) {
        switch (event) {
            case PLAYER_GP_TOTAL_UPDATED -> {
                int val = Integer.parseInt(value);
                statusUI.setGoldValue(val);
            }
            //notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
            case PLAYER_INVENTORY_UPDATED -> {
                Array<InventoryItemLocation> items = json.fromJson(Array.class, value);
                InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), items, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
            }
            default -> {
            }
        }
    }

    @Override
    public void onNotify(int value, StatusEvent event) {
        switch (event) {
            case UPDATED_GP:
                storeInventoryUI.setPlayerGP(value);
                ProfileManager.getInstance().setProperty("currentPlayerGP", statusUI.getGoldValue());
                break;
            case UPDATED_HP:
                ProfileManager.getInstance().setProperty("currentPlayerHP", statusUI.getHPValue());
                break;
            case UPDATED_LEVEL:
                ProfileManager.getInstance().setProperty("currentPlayerLevel", statusUI.getLevelValue());
                break;
            case UPDATED_LEVEL_FROM_QUEST:
                ProfileManager.getInstance().setProperty("currentPlayerLevel", statusUI.getLevelValue());
                createStatsUpUI(statusUI.getNbrLevelUp());
                break;
            case UPDATED_MP:
                ProfileManager.getInstance().setProperty("currentPlayerMP", statusUI.getMPValue());
                break;
            case UPDATED_XP:
                ProfileManager.getInstance().setProperty("currentPlayerXP", statusUI.getXPValue());
                break;
            case LEVELED_UP:
                //notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, ClassObserver.ClassEvent event) {
        switch (event) {
            case CHECK_UPGRADE_TREE_CLASS -> {
                String currentClass = ProfileManager.getInstance().getProperty("characterClass", String.class);
                int AP = ProfileManager.getInstance().getProperty("currentPlayerCharacterAP", Integer.class);
                int DP = ProfileManager.getInstance().getProperty("currentPlayerCharacterDP", Integer.class);
                String configFilePath = player.getEntityConfig().getClassTreePath();
                Tree tree = Tree.buildClassTree(configFilePath);
                Node node = tree.checkForClassUpgrade(currentClass, AP, DP);
                Tree.saveNewClass(node);

                if (node != null) {
                    statusUI.setVisible(false);
                    notificationUI.setVisible(true);
                    notificationUI.loadUpgradeClass(node.getClassId());
                }
            }
            default -> {
            }
        }
    }

    private void createStatsUpUI(int nbrLevelUp) {
        statsUpUI = new StatsUpUI(nbrLevelUp);
        statsUpUI.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);
        statsUpUI.setKeepWithinStage(false);
        statsUpUI.setWidth(stage.getWidth() / 2);
        statsUpUI.setHeight(stage.getHeight() / 2);
        statsUpUI.setMovable(false);

        statsUpUI.validate();
        statsUpUI.addObserver((ClassObserver) this);
        statsUpUI.addObserver((InventoryObserver) this);
        stage.addActor(statsUpUI);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void onNotify(Entity enemyEntity, BattleEvent event) {
        switch (event) {
            case OPPONENT_DEFEATED -> {
                int goldReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_GP_REWARD.toString()));
                statusUI.addGoldValue(goldReward);
                int xpReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString()));
                statusUI.addXPValue(xpReward, false);
            }
            case PLAYER_HIT_DAMAGE -> {
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                statusUI.setHPValue(hpVal);
            }
            default -> {
            }
        }
    }

    @Override
    public void onNotify(String value, InventoryEvent event) {
        switch (event) {
            case ITEM_CONSUMED -> {
                String[] strings = value.split(Component.MESSAGE_TOKEN);
                if (strings.length != 2) {
                    return;
                }
                int type = Integer.parseInt(strings[0]);
                int typeValue = Integer.parseInt(strings[1]);
                if (InventoryItem.doesRestoreHP(type)) {
                    //notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_EATING);
                    statusUI.addHPValue(typeValue);
                } else if (InventoryItem.doesRestoreMP(type)) {
                    //notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
                    statusUI.addMPValue(typeValue);
                }
            }
            case REFRESH_STATS -> {
                Array<InventoryItemLocation> equipInventory = ProfileManager.getInstance().getProperty("playerEquipInventory", Array.class);
                inventoryUI.resetEquipSlots();
                if (equipInventory != null && equipInventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.getEquipSlotTable(), equipInventory, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                }
                //TODO: update with class bonuses
            }
            default -> {
            }
        }
    }

    @Override
    public void addObserver(AudioObserver audioObserver) {
        observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: observers) {
            observer.onNotify(command, event);
        }
    }
}
