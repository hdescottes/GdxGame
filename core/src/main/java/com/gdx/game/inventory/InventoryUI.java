package com.gdx.game.inventory;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.inventory.item.InventoryItemFactory;
import com.gdx.game.inventory.item.InventoryItemLocation;
import com.gdx.game.inventory.set.EquipmentSet;
import com.gdx.game.inventory.set.EquipmentSetFactory;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.inventory.slot.InventorySlotObserver;
import com.gdx.game.inventory.slot.InventorySlotSource;
import com.gdx.game.inventory.slot.InventorySlotTarget;
import com.gdx.game.inventory.slot.InventorySlotTooltip;
import com.gdx.game.inventory.slot.InventorySlotTooltipListener;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.gdx.game.common.UtilityClass.calculateBonus;
import static com.gdx.game.component.Component.MESSAGE_TOKEN;
import static com.gdx.game.manager.ResourceManager.ITEMS_TEXTURE_ATLAS;
import static com.gdx.game.manager.ResourceManager.STATUS_UI_TEXTURE_ATLAS;

public class InventoryUI extends Window implements InventorySubject, InventorySlotObserver {

    public final static int NUM_SLOTS = 50;
    public static final String PLAYER_INVENTORY = "Player_Inventory";
    public static final String STORE_INVENTORY = "Store_Inventory";

    private Table inventorySlotTable;
    private Table equipSlots;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private Label classValLabel;
    private String classVal;
    private Label setValLabel;
    private String setVal;
    private Label DPValLabel;
    private int DPVal;
    private Label APValLabel;
    private int APVal;
    private Label SPDPValLabel;
    private int SPDPVal;

    private final int slotWidth = 52;
    private final int slotHeight = 52;

    private Array<InventoryObserver> observers;

    private InventorySlotTooltip inventorySlotTooltip;

    public InventoryUI() {
        super("Inventory", ResourceManager.skin);

        observers = new Array<>();

        dragAndDrop = new DragAndDrop();
        inventoryActors = new Array<>();

        classVal = ProfileManager.getInstance().getProperty("characterClass", String.class);
        APVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterAP", Integer.class);
        DPVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterDP", Integer.class);
        SPDPVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterSPDP", Integer.class);

        //create
        inventorySlotTable = new Table();
        inventorySlotTable.setName("Inventory_Slot_Table");

        Table playerSlotsTable = new Table();
        equipSlots = new Table();
        equipSlots.setName("Equipment_Slot_Table");

        equipSlots.defaults().space(10);
        inventorySlotTooltip = new InventorySlotTooltip(ResourceManager.skin);

        Label classLabel = new Label("Class: ", ResourceManager.skin);
        classValLabel = new Label(classVal, ResourceManager.skin);

        Label setLabel = new Label("Set: ", ResourceManager.skin);
        setValLabel = new Label(setVal, ResourceManager.skin);

        Label DPLabel = new Label("Defense: ", ResourceManager.skin);
        DPValLabel = new Label(String.valueOf(DPVal), ResourceManager.skin);

        Label APLabel = new Label("Attack : ", ResourceManager.skin);
        APValLabel = new Label(String.valueOf(APVal), ResourceManager.skin);

        Label SPDPLabel = new Label("Speed : ", ResourceManager.skin);
        SPDPValLabel = new Label(String.valueOf(SPDPVal), ResourceManager.skin);

        Table labelTable = new Table();
        labelTable.add(classLabel).align(Align.left);
        labelTable.add(classValLabel).align(Align.center);
        labelTable.row();
        labelTable.add(setLabel).align(Align.left);
        labelTable.add(setValLabel).align(Align.center);
        labelTable.row();
        labelTable.row();
        labelTable.row();
        labelTable.add(DPLabel).align(Align.left);
        labelTable.add(DPValLabel).align(Align.center);
        labelTable.row();
        labelTable.row();
        labelTable.add(APLabel).align(Align.left);
        labelTable.add(APValLabel).align(Align.center);
        labelTable.row();
        labelTable.row();
        labelTable.add(SPDPLabel).align(Align.left);
        labelTable.add(SPDPValLabel).align(Align.center);

        InventorySlot headSlot = new InventorySlot(InventoryItem.ItemUseType.ARMOR_HELMET.getValue(),
                new Image(ITEMS_TEXTURE_ATLAS.findRegion("inv_helmet")));

        InventorySlot leftArmSlot = new InventorySlot(InventoryItem.ItemUseType.WEAPON_ONEHAND.getValue() |
                InventoryItem.ItemUseType.WEAPON_TWOHAND.getValue() | InventoryItem.ItemUseType.ARMOR_SHIELD.getValue() |
                InventoryItem.ItemUseType.WAND_ONEHAND.getValue() | InventoryItem.ItemUseType.WAND_TWOHAND.getValue(),
                new Image(ITEMS_TEXTURE_ATLAS.findRegion("inv_weapon"))
        );

        InventorySlot rightArmSlot = new InventorySlot(InventoryItem.ItemUseType.WEAPON_ONEHAND.getValue() |
                InventoryItem.ItemUseType.WEAPON_TWOHAND.getValue() | InventoryItem.ItemUseType.ARMOR_SHIELD.getValue() |
                InventoryItem.ItemUseType.WAND_ONEHAND.getValue() | InventoryItem.ItemUseType.WAND_TWOHAND.getValue(),
                new Image(ITEMS_TEXTURE_ATLAS.findRegion("inv_shield"))
        );

        InventorySlot chestSlot = new InventorySlot(InventoryItem.ItemUseType.ARMOR_CHEST.getValue(),
                new Image(ITEMS_TEXTURE_ATLAS.findRegion("inv_chest")));

        InventorySlot legsSlot = new InventorySlot(InventoryItem.ItemUseType.ARMOR_FEET.getValue(),
                new Image(ITEMS_TEXTURE_ATLAS.findRegion("inv_boot")));

        headSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        leftArmSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        rightArmSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        chestSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
        legsSlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));

        headSlot.addObserver(this);
        leftArmSlot.addObserver(this);
        rightArmSlot.addObserver(this);
        chestSlot.addObserver(this);
        legsSlot.addObserver(this);

        dragAndDrop.addTarget(new InventorySlotTarget(headSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(leftArmSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(chestSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(rightArmSlot));
        dragAndDrop.addTarget(new InventorySlotTarget(legsSlot));

        playerSlotsTable.setBackground(new Image(new NinePatch(STATUS_UI_TEXTURE_ATLAS.createPatch("dialog"))).getDrawable());

        //layout
        handleLayoutInventorySlot();

        equipSlots.add();
        equipSlots.add(headSlot).size(slotWidth, slotHeight);
        equipSlots.row();

        equipSlots.add(leftArmSlot).size(slotWidth, slotHeight);
        equipSlots.add(chestSlot).size(slotWidth, slotHeight);
        equipSlots.add(rightArmSlot).size(slotWidth, slotHeight);
        equipSlots.row();

        equipSlots.add();
        equipSlots.right().add(legsSlot).size(slotWidth, slotHeight);

        playerSlotsTable.add(equipSlots);
        inventoryActors.add(inventorySlotTooltip);

        this.add(playerSlotsTable).padBottom(20);
        this.add(labelTable);
        this.row();
        this.add(inventorySlotTable).colspan(2);
        this.row();
        this.pack();
    }

    public int getDPVal() {
        return DPVal;
    }

    public int getAPVal() {
        return APVal;
    }

    public int getSPDPVal() {
        return SPDPVal;
    }

    public DragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    public Table getEquipSlotTable() {
        return equipSlots;
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
                    if (getTapCount() == 2) {
                        InventorySlot slot = (InventorySlot)event.getListenerActor();
                        if (slot.hasItem()) {
                            InventoryItem item = slot.getTopInventoryItem();
                            if (item.isConsumable()) {
                                String itemInfo = item.getItemUseType() + MESSAGE_TOKEN + item.getItemUseTypeValue();
                                InventoryUI.this.notify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);
                                slot.removeActor(item);
                                slot.remove(item);
                            }
                        }
                    }
                }
            });

            int lengthSlotRow = 10;
            if (i % lengthSlotRow == 0) {
                inventorySlotTable.row();
            }
        }
    }

    public void resetEquipSlots() {
        ProfileManager.getInstance().setProperty("bonusSet", null);
        classVal = ProfileManager.getInstance().getProperty("characterClass", String.class);
        APVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterAP", Integer.class);
        DPVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterDP", Integer.class);
        SPDPVal = ProfileManager.getInstance().getProperty("currentPlayerCharacterSPDP", Integer.class);

        classValLabel.setText(String.valueOf(classVal));
        setValLabel.setText("");

        DPValLabel.setText(String.valueOf(DPVal));
        notify(String.valueOf(DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);

        APValLabel.setText(String.valueOf(APVal));
        notify(String.valueOf(APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

        SPDPValLabel.setText(String.valueOf(SPDPVal));
    }

    public static void clearInventoryItems(Table targetTable) {
        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
            if (inventorySlot == null) {
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
            if (inventorySlot == null) {
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
                if (itemName == null || itemName.isEmpty()) {
                    item.setName(defaultName);
                } else {
                    item.setName(itemName);
                }

                inventorySlot.add(item);
                if (item.getName().equalsIgnoreCase(defaultName)) {
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                } else if (!disableNonDefaultItems) {
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
            if (inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if (numItems > 0) {
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
            if (inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if (numItems > 0) {
                String topItemName = inventorySlot.getTopInventoryItem().getName();
                if (topItemName.equalsIgnoreCase(filterOutName)) {
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
            if (inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems(name);
            if (numItems > 0) {
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
                if (inventorySlot == null) {
                    continue;
                }
                int numItems = inventorySlot.getNumItems();
                if (numItems == 0) {
                    item.setLocationIndex(index);
                    //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + numItems);
                    index++;
                    break;
                }
            }
            if (index == sourceCells.size) {
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
            if (inventorySlot == null) {
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
            if (inventorySlot == null) {
                continue;
            }
            int numItems = inventorySlot.getNumItems();
            if (numItems == 0) {
                return true;
            } else {
                index++;
            }
        }
        return false;
    }

    public void addEntityToInventory(String itemTypeID, String itemName) {
        Array<Cell> sourceCells = inventorySlotTable.getCells();
        int index = 0;

            for(; index < sourceCells.size; index++) {
                InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
                if (inventorySlot == null) {
                    continue;
                }
                int numItems = inventorySlot.getNumItems();
                if (numItems == 0) {
                    InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.valueOf(itemTypeID));
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
            if (inventorySlot == null) {
                continue;
            }
            InventoryItem item = inventorySlot.getTopInventoryItem();
            if (item == null) {
                continue;
            }
            String inventoryItemName = item.getName();
            if (inventoryItemName != null && inventoryItemName.equals(questID)) {
                inventorySlot.clearAllInventoryItems(false);
            }
        }
    }

    public Array<Actor> getInventoryActors() {
        return inventoryActors;
    }

    public boolean isSetEquipped(Array<InventoryItemLocation> equipItems) {
        if (equipItems.isEmpty() || equipItems.size != 5) {
            return false;
        }

        InventoryItem firstItem = InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.valueOf(equipItems.get(0).getItemTypeAtLocation()));
        String firstItemSetID = firstItem.getItemSetID().name();

        for (int i = 1; i < equipItems.size - 1; i++) {
            InventoryItem currentItem = InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.valueOf(equipItems.get(i).getItemTypeAtLocation()));
            String currentItemSetID = String.valueOf(currentItem.getItemSetID());
            if (!firstItemSetID.equals(currentItemSetID)) {
                return false;
            }
        }
        return true;
    }

    public static void setBonusFromSet(InventoryItem item) {
        EquipmentSet set = EquipmentSetFactory.getInstance().getEquipmentSet(item.getItemSetID());
        HashMap<String, String> bonusMap = set.getBonus();
        Array<EntityBonus> bonusArray = new Array<>();

        for (Map.Entry<String, String> entry : bonusMap.entrySet()) {
            EntityBonus entityBonus = new EntityBonus(entry.getKey(), entry.getValue());
            bonusArray.add(entityBonus);
        }

        ProfileManager.getInstance().setProperty("bonusSet", bonusArray);
    }

    private void updateValuesWithBonus(String bonusAttribute, boolean isAdd) {
        HashMap<String, Integer> bonusMap = calculateBonus(bonusAttribute);

        Map<String, String[]> updates = Map.of(
                EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), new String[]{"APVal", "APValLabel"},
                EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), new String[]{"DPVal", "DPValLabel"}
        );

        updates.forEach((key, fields) -> {
            Integer value = bonusMap.get(key);
            if (value != null) {
                try {
                    Field propertyField = this.getClass().getDeclaredField(fields[0]);
                    Field labelField = this.getClass().getDeclaredField(fields[1]);

                    int currentValue = propertyField.getInt(this);
                    int newValue = isAdd ? currentValue + value : currentValue - value;
                    propertyField.setInt(this, newValue);

                    Label label = (Label) labelField.get(this);
                    label.setText(String.valueOf(newValue));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("A value in the update stat map does not exist", e);
                }
            }
        });
    }

    @Override
    public void onNotify(InventorySlot slot, SlotEvent event) {
        switch (event) {
            case ADDED_ITEM -> {
                InventoryItem addItem = slot.getTopInventoryItem();
                if (addItem == null) {
                    return;
                }
                boolean isSetEquipped = isSetEquipped(getInventory(equipSlots));
                if (!isSetEquipped) {
                    setValLabel.setText("");
                    updateValuesWithBonus("bonusSet", false);
                    ProfileManager.getInstance().setProperty("bonusSet", null);
                }

                if (addItem.isInventoryItemOffensive()) {
                    APVal += addItem.getItemUseTypeValue();
                    APValLabel.setText(String.valueOf(APVal));
                    notify(String.valueOf(APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    ProfileManager.getInstance().setProperty("currentPlayerAP", APVal);

                    if (addItem.isInventoryItemOffensiveWand()) {
                        notify(String.valueOf(addItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.ADD_WAND_AP);
                    }

                } else if (addItem.isInventoryItemDefensive()) {
                    DPVal += addItem.getItemUseTypeValue();
                    DPValLabel.setText(String.valueOf(DPVal));
                    notify(String.valueOf(DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);

                    ProfileManager.getInstance().setProperty("currentPlayerDP", DPVal);
                }
                if (isSetEquipped) {
                    setVal = InventoryItemFactory.getInstance().getInventoryItem(InventoryItem.ItemTypeID.valueOf(getInventory(equipSlots).get(0).getItemTypeAtLocation())).getItemSetID().name();
                    setValLabel.setText(setVal);

                    setBonusFromSet(addItem);
                    updateValuesWithBonus("bonusSet", true);
                }
            }
            case REMOVED_ITEM -> {
                InventoryItem removeItem = slot.getTopInventoryItem();
                if (removeItem == null) {
                    return;
                }
                updateValuesWithBonus("bonusSet", false);

                if (removeItem.isInventoryItemOffensive()) {
                    APVal -= removeItem.getItemUseTypeValue();
                    APValLabel.setText(String.valueOf(APVal));
                    notify(String.valueOf(APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    ProfileManager.getInstance().setProperty("currentPlayerAP", APVal);

                    if (removeItem.isInventoryItemOffensiveWand()) {
                        notify(String.valueOf(removeItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.REMOVE_WAND_AP);
                    }

                } else if (removeItem.isInventoryItemDefensive()) {
                    DPVal -= removeItem.getItemUseTypeValue();
                    DPValLabel.setText(String.valueOf(DPVal));
                    notify(String.valueOf(DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);

                    ProfileManager.getInstance().setProperty("currentPlayerDP", DPVal);
                }
                setValLabel.setText("");
                ProfileManager.getInstance().setProperty("bonusSet", null);
            }
            default -> {
            }
        }
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
