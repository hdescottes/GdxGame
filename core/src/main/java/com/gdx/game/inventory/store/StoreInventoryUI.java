package com.gdx.game.inventory.store;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.inventory.InventoryItemLocation;
import com.gdx.game.inventory.slot.InventorySlot;
import com.gdx.game.inventory.slot.InventorySlotObserver;
import com.gdx.game.inventory.slot.InventorySlotTarget;
import com.gdx.game.inventory.slot.InventorySlotTooltip;
import com.gdx.game.inventory.slot.InventorySlotTooltipListener;
import com.gdx.game.inventory.InventoryUI;

import static com.gdx.game.manager.ResourceManager.STATUS_UI_SKIN;

public class StoreInventoryUI extends Window implements InventorySlotObserver, StoreInventorySubject {

    private int numStoreInventorySlots = 30;
    private int lengthSlotRow = 10;
    private Table inventorySlotTable;
    private Table playerInventorySlotTable;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private final int slotWidth = 52;
    private final int slotHeight = 52;

    private InventorySlotTooltip inventorySlotTooltip;

    private Label sellTotalLabel;
    private Label buyTotalLabel;
    private Label playerTotalGP;

    private int tradeInVal = 0;
    private int fullValue = 0;
    private int playerTotal = 0;

    private Button sellButton;
    private Button buyButton;
    public TextButton closeButton;

    private Table buttons;
    private Table totalLabels;

    private Array<StoreInventoryObserver> observers;

    private Json json;

    private static String SELL = "SELL";
    private static String BUY = "BUY";
    private static String GP = " GP";
    private static String PLAYER_TOTAL = "Player Total";

    public StoreInventoryUI() {
        super("Store Inventory", STATUS_UI_SKIN, "solidbackground");

        observers = new Array<>();
        json = new Json();

        this.setFillParent(true);

        //create
        dragAndDrop = new DragAndDrop();
        inventoryActors = new Array<>();
        inventorySlotTable = new Table();
        inventorySlotTable.setName(InventoryUI.STORE_INVENTORY);

        playerInventorySlotTable = new Table();
        playerInventorySlotTable.setName(InventoryUI.PLAYER_INVENTORY);
        inventorySlotTooltip = new InventorySlotTooltip(STATUS_UI_SKIN);

        sellButton = new TextButton(SELL, STATUS_UI_SKIN, "inventory");
        disableButton(sellButton, true);

        sellTotalLabel = new Label(SELL + " : " + tradeInVal + GP, STATUS_UI_SKIN);
        sellTotalLabel.setAlignment(Align.center);
        buyTotalLabel = new Label(BUY + " : " + fullValue + GP, STATUS_UI_SKIN);
        buyTotalLabel.setAlignment(Align.center);

        playerTotalGP = new Label(PLAYER_TOTAL + " : " + playerTotal +  GP, STATUS_UI_SKIN);

        buyButton = new TextButton(BUY, STATUS_UI_SKIN, "inventory");
        disableButton(buyButton, true);

        closeButton = new TextButton("X", STATUS_UI_SKIN);

        buttons = new Table();
        buttons.defaults().expand().fill();
        buttons.add(sellButton).padLeft(10).padRight(10);
        buttons.add(buyButton).padLeft(10).padRight(10);

        totalLabels = new Table();
        totalLabels.defaults().expand().fill();
        totalLabels.add(sellTotalLabel).padLeft(40);
        totalLabels.add();
        totalLabels.add(buyTotalLabel).padRight(40);

        //layout
        for(int i = 1; i <= numStoreInventorySlots; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.STORE_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight);

            if(i % lengthSlotRow == 0) {
                inventorySlotTable.row();
            }
        }

        for(int i = 1; i <= InventoryUI.NUM_SLOTS; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.PLAYER_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            playerInventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight);

            if(i % lengthSlotRow == 0) {
                playerInventorySlotTable.row();
            }
        }

        inventoryActors.add(inventorySlotTooltip);

        this.add();
        this.add(closeButton);
        this.row();

        //this.debugAll();
        this.defaults().expand().fill();
        this.add(inventorySlotTable).pad(10, 10, 10, 10).row();
        this.add(buttons).row();
        this.add(totalLabels).row();
        this.add(playerInventorySlotTable).pad(10, 10, 10, 10).row();
        this.add(playerTotalGP);
        this.pack();

        //Listeners
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(fullValue > 0 && playerTotal >= fullValue) {
                    playerTotal -= fullValue;
                    StoreInventoryUI.this.notify(Integer.toString(playerTotal), StoreInventoryObserver.StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                    fullValue = 0;
                    buyTotalLabel.setText(BUY  + " : " + fullValue + GP);

                    checkButtonStates();

                    //Make sure we update the owner of the items
                    InventoryUI.setInventoryItemNames(playerInventorySlotTable, InventoryUI.PLAYER_INVENTORY);
                    savePlayerInventory();
                }
            }
        });

        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(tradeInVal > 0) {
                    playerTotal += tradeInVal;
                    StoreInventoryUI.this.notify(Integer.toString(playerTotal), StoreInventoryObserver.StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                    tradeInVal = 0;
                    sellTotalLabel.setText(SELL  + " : " + tradeInVal + GP);

                    checkButtonStates();

                    //Remove sold items
                    Array<Cell> cells = inventorySlotTable.getCells();
                    for(int i = 0; i < cells.size; i++){
                        InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
                        if(inventorySlot == null) continue;
                        if(inventorySlot.hasItem() && inventorySlot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)) {
                            inventorySlot.clearAllInventoryItems(false);
                        }
                    }
                    savePlayerInventory();
                }
            }
        });
    }

    public TextButton getCloseButton() {
        return closeButton;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    public Array<Actor> getInventoryActors() {
        return inventoryActors;
    }

    public void loadPlayerInventory(Array<InventoryItemLocation> playerInventoryItems) {
        InventoryUI.populateInventory(playerInventorySlotTable, playerInventoryItems, dragAndDrop, InventoryUI.PLAYER_INVENTORY, true);
    }

    public void loadStoreInventory(Array<InventoryItemLocation> storeInventoryItems) {
        InventoryUI.populateInventory(inventorySlotTable, storeInventoryItems, dragAndDrop, InventoryUI.STORE_INVENTORY, false);
    }

    public void savePlayerInventory() {
        Array<InventoryItemLocation> playerItemsInPlayerInventory = InventoryUI.getInventoryFiltered(playerInventorySlotTable, InventoryUI.STORE_INVENTORY);
        Array<InventoryItemLocation> playerItemsInStoreInventory = InventoryUI.getInventoryFiltered(playerInventorySlotTable, inventorySlotTable, InventoryUI.STORE_INVENTORY);
        playerItemsInPlayerInventory.addAll(playerItemsInStoreInventory);
        StoreInventoryUI.this.notify(json.toJson(playerItemsInPlayerInventory), StoreInventoryObserver.StoreInventoryEvent.PLAYER_INVENTORY_UPDATED);
    }

    public void cleanupStoreInventory() {
        InventoryUI.removeInventoryItems(InventoryUI.STORE_INVENTORY, playerInventorySlotTable);
        InventoryUI.removeInventoryItems(InventoryUI.PLAYER_INVENTORY, inventorySlotTable);
    }

    @Override
    public void onNotify(InventorySlot slot, SlotEvent event) {
        switch(event) {
            case ADDED_ITEM:
                //moving from player inventory to store inventory to sell
                if(slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY)) {
                    tradeInVal += slot.getTopInventoryItem().getTradeValue();
                    sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
                }
                //moving from store inventory to player inventory to buy
                if(slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)) {
                    fullValue += slot.getTopInventoryItem().getItemValue();
                    buyTotalLabel.setText(BUY + " : " + fullValue + GP);
                }
                break;
            case REMOVED_ITEM:
                if(slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY)) {
                    tradeInVal -= slot.getTopInventoryItem().getTradeValue();
                    sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
                }
                if(slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)) {
                    fullValue -= slot.getTopInventoryItem().getItemValue();
                    buyTotalLabel.setText(BUY + " : " + fullValue + GP);
                }
                break;
        }
        checkButtonStates();
    }

    public void checkButtonStates() {
        if(tradeInVal <= 0) {
            disableButton(sellButton, true);
        } else {
            disableButton(sellButton, false);
        }

        if(fullValue <= 0 || playerTotal < fullValue) {
            disableButton(buyButton, true);
        } else {
            disableButton(buyButton, false);
        }
    }

    public void setPlayerGP(int value) {
        playerTotal = value;
        playerTotalGP.setText(PLAYER_TOTAL + " : " + playerTotal +  GP);
    }

    private void disableButton(Button button, boolean disable) {
        if(disable) {
            button.setDisabled(true);
            button.setTouchable(Touchable.disabled);
        } else {
            button.setDisabled(false);
            button.setTouchable(Touchable.enabled);
        }
    }

    @Override
    public void addObserver(StoreInventoryObserver storeObserver) {
        observers.add(storeObserver);
    }

    @Override
    public void removeObserver(StoreInventoryObserver storeObserver) {
        observers.removeValue(storeObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(StoreInventoryObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String value, StoreInventoryObserver.StoreInventoryEvent event) {
        for(StoreInventoryObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }
}
