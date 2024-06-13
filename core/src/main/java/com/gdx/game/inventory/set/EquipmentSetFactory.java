package com.gdx.game.inventory.set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.game.inventory.item.InventoryItem;

import java.util.ArrayList;
import java.util.Hashtable;

public class EquipmentSetFactory {

    private Json json = new Json();
    private static final String ITEM_SET = "scripts/equipment_sets.json";
    private static EquipmentSetFactory instance = null;
    private Hashtable<InventoryItem.ItemSetID, EquipmentSet> equipSetList;

    public static EquipmentSetFactory getInstance() {
        if (instance == null) {
            instance = new EquipmentSetFactory();
        }

        return instance;
    }

    private EquipmentSetFactory() {
        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(ITEM_SET));
        equipSetList = new Hashtable<>();

        for(JsonValue jsonVal : list) {
            EquipmentSet equipSet = json.readValue(EquipmentSet.class, jsonVal);
            equipSetList.put(equipSet.getItemSetID(), equipSet);
        }
    }

    public EquipmentSet getEquipmentSet(InventoryItem.ItemSetID equipmentType) {
        return new EquipmentSet(equipSetList.get(equipmentType));
    }
}
