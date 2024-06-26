package com.gdx.game.inventory.slot;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.gdx.game.inventory.item.InventoryItem;

import static com.gdx.game.manager.ResourceManager.STATUS_UI_SKIN;
import static com.gdx.game.manager.ResourceManager.STATUS_UI_TEXTURE_ATLAS;

public class InventorySlot extends Stack implements InventorySlotSubject {

    //All slots have this default image
    private Stack defaultBackground;
    private Image customBackgroundDecal;
    private Label numItemsLabel;
    private int numItemsVal = 0;
    private int filterItemType;

    private Array<InventorySlotObserver> observers;

    public InventorySlot() {
        filterItemType = 0; //filter nothing
        defaultBackground = new Stack();
        customBackgroundDecal = new Image();
        observers = new Array<>();
        Image image = new Image(new NinePatch(STATUS_UI_TEXTURE_ATLAS.createPatch("dialog")));

        numItemsLabel = new Label(String.valueOf(numItemsVal), STATUS_UI_SKIN, "inventory-item-count");
        numItemsLabel.setAlignment(Align.bottomRight);
        numItemsLabel.setVisible(false);

        defaultBackground.add(image);

        defaultBackground.setName("background");
        numItemsLabel.setName("numitems");

        this.add(defaultBackground);
        this.add(numItemsLabel);
    }

    public InventorySlot(int filterItemType, Image customBackgroundDecal) {
        this();
        this.filterItemType = filterItemType;
        this.customBackgroundDecal = customBackgroundDecal;
        defaultBackground.add(this.customBackgroundDecal);
    }

    public void decrementItemCount(boolean sendRemoveNotification) {
        numItemsVal--;
        numItemsLabel.setText(String.valueOf(numItemsVal));
        if (defaultBackground.getChildren().size == 1) {
            defaultBackground.add(customBackgroundDecal);
        }
        checkVisibilityOfItemCount();
        if (sendRemoveNotification) {
            notify(this, InventorySlotObserver.SlotEvent.REMOVED_ITEM);
        }

    }

    public void incrementItemCount(boolean sendAddNotification) {
        numItemsVal++;
        numItemsLabel.setText(String.valueOf(numItemsVal));
        if (defaultBackground.getChildren().size > 1) {
            defaultBackground.getChildren().pop();
        }
        checkVisibilityOfItemCount();
        if (sendAddNotification) {
            notify(this, InventorySlotObserver.SlotEvent.ADDED_ITEM);
        }
    }

    @Override
    public void add(Actor actor) {
        super.add(actor);

        if (numItemsLabel == null) {
            return;
        }

        if (!actor.equals(defaultBackground) && !actor.equals(numItemsLabel)) {
            incrementItemCount(true);
        }
    }

    public void remove(Actor actor) {
        super.removeActor(actor);

        if (numItemsLabel == null) {
            return;
        }

        if (!actor.equals(defaultBackground) && !actor.equals(numItemsLabel)) {
            decrementItemCount(true);
        }
    }

    public void add(Array<Actor> array) {
        for(Actor actor : array) {
            super.add(actor);

            if (numItemsLabel == null) {
                return;
            }

            if (!actor.equals(defaultBackground) && !actor.equals(numItemsLabel)) {
                incrementItemCount(true);
            }
        }
    }

    public Array<Actor> getAllInventoryItems() {
        Array<Actor> items = new Array<>();
        if (hasItem()) {
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  arrayChildren.size - 2;
            for(int i = 0; i < numInventoryItems; i++) {
                decrementItemCount(true);
                items.add(arrayChildren.pop());
            }
        }
        return items;
    }

    public void updateAllInventoryItemNames(String name) {
        if (hasItem()) {
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            //skip first two elements
            for(int i = arrayChildren.size - 1; i > 1 ; i--) {
                arrayChildren.get(i).setName(name);
            }
        }
    }

    public void removeAllInventoryItemsWithName(String name) {
        if (hasItem()) {
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            //skip first two elements
            for(int i = arrayChildren.size - 1; i > 1 ; i--) {
                String itemName = arrayChildren.get(i).getName();
                if (itemName.equalsIgnoreCase(name)) {
                    decrementItemCount(true);
                    arrayChildren.removeIndex(i);
                }
            }
        }
    }

    public void clearAllInventoryItems(boolean sendRemoveNotifications) {
        if (hasItem()) {
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  getNumItems();
            for(int i = 0; i < numInventoryItems; i++) {
                decrementItemCount(sendRemoveNotifications);
                arrayChildren.pop();
            }
        }
    }

    private void checkVisibilityOfItemCount() {
        numItemsLabel.setVisible(numItemsVal >= 2);
    }

    public boolean hasItem() {
        if (hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            return items.size > 2;
        }
        return false;
    }

    public int getNumItems() {
        if (hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            return items.size - 2;
        }
        return 0;
    }

    public int getNumItems(String name) {
        if (hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            int totalFilteredSize = 0;
            for(Actor actor: items) {
                if (actor.getName().equalsIgnoreCase(name)) {
                    totalFilteredSize++;
                }
            }
            return totalFilteredSize;
        }
        return 0;
    }

    public boolean doesAcceptItemUseType(int itemUseType) {
        if (filterItemType == 0) {
            return true;
        } else {
            return ((filterItemType & itemUseType) == itemUseType);
        }
    }

    public InventoryItem getTopInventoryItem() {
        InventoryItem actor = null;
        if (hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            if (items.size > 2 ) {
                actor = (InventoryItem) items.peek();
            }
        }
        return actor;
    }

    static public void swapSlots(InventorySlot inventorySlotSource, InventorySlot inventorySlotTarget, InventoryItem dragActor) {
        //check if items can accept each other, otherwise, no swap
        if (!inventorySlotTarget.doesAcceptItemUseType(dragActor.getItemUseType()) ||
                !inventorySlotSource.doesAcceptItemUseType(inventorySlotTarget.getTopInventoryItem().getItemUseType())) {
            inventorySlotSource.add(dragActor);
            return;
        }

        //swap
        Array<Actor> tempArray = inventorySlotSource.getAllInventoryItems();
        tempArray.add(dragActor);
        inventorySlotSource.add(inventorySlotTarget.getAllInventoryItems());
        inventorySlotTarget.add(tempArray);
    }

    @Override
    public void addObserver(InventorySlotObserver slotObserver) {
        observers.add(slotObserver);
    }

    @Override
    public void removeObserver(InventorySlotObserver slotObserver) {
        observers.removeValue(slotObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(InventorySlotObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(final InventorySlot slot, InventorySlotObserver.SlotEvent event) {
        for(InventorySlotObserver observer: observers) {
            observer.onNotify(slot, event);
        }
    }
}
