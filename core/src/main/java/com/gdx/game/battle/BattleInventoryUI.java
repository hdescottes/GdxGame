package com.gdx.game.battle;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.entities.Entity;
import com.gdx.game.inventory.InventoryItem;
import com.gdx.game.inventory.InventoryItemFactory;
import com.gdx.game.inventory.InventoryItemLocation;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.inventory.InventorySubject;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.inventory.slot.InventorySlotSource;
import com.gdx.game.inventory.slot.InventorySlotTarget;
import com.gdx.game.inventory.slot.InventorySlotTooltip;
import com.gdx.game.inventory.slot.InventorySlotTooltipListener;
import com.gdx.game.manager.ResourceManager;

import static com.gdx.game.component.Component.MESSAGE_TOKEN;

public class BattleInventoryUI extends Window implements InventorySubject {

    public final static int NUM_SLOTS = 50;
    public static final String PLAYER_INVENTORY = "Player_Inventory";

    private int lengthSlotRow = 10;
    private Table inventorySlotTable;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private final int slotWidth = 52;
    private final int slotHeight = 52;

    private Array<InventoryObserver> observers;

    private InventorySlotTooltip inventorySlotTooltip;

    public BattleInventoryUI() {
        super("BattleInventory", ResourceManager.skin);

        observers = new Array<>();

        dragAndDrop = new DragAndDrop();
        inventoryActors = new Array<>();

        //create
        inventorySlotTable = new Table();
        inventorySlotTable.setName("Inventory_Slot_Table");

        inventorySlotTooltip = new InventorySlotTooltip(ResourceManager.skin);

        //layout
        handleLayoutInventorySlot();

        inventoryActors.add(inventorySlotTooltip);

        this.add(inventorySlotTable).colspan(2);
        this.row();
        this.pack();
    }

    public DragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    private void handleLayoutInventorySlot() {
        for(int i = 1; i <= NUM_SLOTS; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight);

            inventorySlot.addListener(new ClickListener() {
                @Override
                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    if(getTapCount() == 2) {
                        InventorySlot slot = (InventorySlot)event.getListenerActor();
                        if(slot.hasItem()) {
                            InventoryItem item = slot.getTopInventoryItem();
                            if(item.isConsumable()) {
                                String itemInfo = item.getItemUseType() + MESSAGE_TOKEN + item.getItemUseTypeValue();
                                BattleInventoryUI.this.notify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);
                                slot.removeActor(item);
                                slot.remove(item);
                            }
                        }
                    }
                }
            });

            if(i % lengthSlotRow == 0) {
                inventorySlotTable.row();
            }
        }
    }

    public static void clearInventoryItems(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
            if(inventorySlot == null) {
                continue;
            }
            inventorySlot.clearAllInventoryItems(false);
        }
    }

    public static Array<InventoryItemLocation> removeInventoryItems(String name, Table inventoryTable) {
        Array<Cell> cells = inventoryTable.getCells();
        Array<InventoryItemLocation> items = new Array<>();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if(inventorySlot == null) {
                continue;
            }
            inventorySlot.removeAllInventoryItemsWithName(name);
        }
        return items;
    }

    public static void populateInventory(Table targetTable, Array<InventoryItemLocation> inventoryItems, DragAndDrop draganddrop, String defaultName, boolean disableNonDefaultItems) {
        clearInventoryItems(targetTable);

        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < inventoryItems.size; i++) {
            InventoryItemLocation itemLocation = inventoryItems.get(i);
            InventoryItem.ItemTypeID itemTypeID = InventoryItem.ItemTypeID.valueOf(itemLocation.getItemTypeAtLocation());
            InventorySlot inventorySlot = ((InventorySlot)cells.get(itemLocation.getLocationIndex()).getActor());

            for(int index = 0; index < itemLocation.getNumberItemsAtLocation(); index++) {
                InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeID);
                String itemName =  itemLocation.getItemNameProperty();
                if(itemName == null || itemName.isEmpty()) {
                    item.setName(defaultName);
                } else {
                    item.setName(itemName);
                }

                inventorySlot.add(item);
                if(item.getName().equalsIgnoreCase(defaultName)) {
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                } else if(!disableNonDefaultItems) {
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                }
            }
        }
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<>();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if(inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if(numItems > 0) {
                items.add(new InventoryItemLocation(i, inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems, inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table targetTable, String filterOutName) {
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<>();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if(inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if(numItems > 0) {
                String topItemName = inventorySlot.getTopInventoryItem().getName();
                if(topItemName.equalsIgnoreCase(filterOutName)) {
                    continue;
                }
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(i, inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems, inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable, String name) {
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<>();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if(inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems(name);
            if(numItems > 0) {
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(i, inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems, name));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table sourceTable, Table targetTable, String filterOutName) {
        Array<InventoryItemLocation> items = getInventoryFiltered(targetTable, filterOutName);
        Array<Cell> sourceCells = sourceTable.getCells();
        int index = 0;
        for(InventoryItemLocation item : items) {
            for(; index < sourceCells.size; index++) {
                InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
                if(inventorySlot == null) {
                    continue;
                }
                int numItems = inventorySlot.getNumItems();
                if(numItems == 0) {
                    item.setLocationIndex(index);
                    //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + numItems);
                    index++;
                    break;
                }
            }
            if(index == sourceCells.size) {
                //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + item.getNumberItemsAtLocation());
                item.setLocationIndex(index-1);
            }
        }
        return items;
    }


    public static void setInventoryItemNames(Table targetTable, String name) {
        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if(inventorySlot == null) {
                continue;
            }
            inventorySlot.updateAllInventoryItemNames(name);
        }
    }

    public boolean doesInventoryHaveSpace() {
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        int index = 0;

        for(; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if(inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if(numItems == 0) {
                return true;
            } else {
                index++;
            }
        }
        return false;
    }

    public void addEntityToInventory(Entity entity, String itemName) {
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        int index = 0;

        for(; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if(inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if(numItems == 0) {
                InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.valueOf(entity.getEntityConfig().getItemTypeID()));
                inventoryItem.setName(itemName);
                inventorySlot.add(inventoryItem);
                dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
                break;
            }
        }
    }

    public void removeQuestItemFromInventory(String questID) {
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        for(int index = 0; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if(inventorySlot == null) {
                continue;
            }
            InventoryItem item = inventorySlot.getTopInventoryItem();
            if(item == null) {
                continue;
            }
            String inventoryItemName = item.getName();
            if(inventoryItemName != null && inventoryItemName.equals(questID)) {
                inventorySlot.clearAllInventoryItems(false);
            }
        }
    }

    public Array<Actor> getInventoryActors() {
        return inventoryActors;
    }

    @Override
    public void addObserver(InventoryObserver inventoryObserver) {
        observers.add(inventoryObserver);
    }

    @Override
    public void removeObserver(InventoryObserver inventoryObserver) {
        observers.removeValue(inventoryObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(InventoryObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String value, InventoryObserver.InventoryEvent event) {
        for(InventoryObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }
}
