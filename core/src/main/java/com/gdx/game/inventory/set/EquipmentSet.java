package com.gdx.game.inventory.set;

import com.gdx.game.inventory.item.InventoryItem;

import java.util.HashMap;

public class EquipmentSet {

    private InventoryItem.ItemSetID itemSetID;
    private String itemSetDescription;
    private HashMap<String, String> bonus = new HashMap<>();

    public EquipmentSet() {
    }

    public EquipmentSet(EquipmentSet equipmentSet) {
        this.itemSetID = equipmentSet.getItemSetID();
        this.itemSetDescription = equipmentSet.getItemSetDescription();
        this.bonus = equipmentSet.getBonus();
    }

    public InventoryItem.ItemSetID getItemSetID() {
        return itemSetID;
    }

    public void setItemSetID(InventoryItem.ItemSetID itemSetID) {
        this.itemSetID = itemSetID;
    }

    public String getItemSetDescription() {
        return itemSetDescription;
    }

    public void setItemSetDescription(String itemSetDescription) {
        this.itemSetDescription = itemSetDescription;
    }

    public HashMap<String, String> getBonus() {
        return bonus;
    }

    public void setBonus(HashMap<String, String> bonus) {
        this.bonus = bonus;
    }
}
