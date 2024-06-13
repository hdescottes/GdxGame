package com.gdx.game.common;

import com.badlogic.gdx.utils.Array;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.profile.ProfileManager;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static java.lang.Float.parseFloat;

public class UtilityClass {

    public static <T, E> HashMap<E, T> mapInverter(HashMap<T, E> hashMap){

        HashMap<E, T> newHashMap = new HashMap<>();

        for(Map.Entry<T, E> entry : hashMap.entrySet()){
            newHashMap.put(entry.getValue(), entry.getKey());
        }

        return newHashMap;
    }

    public static <T, E> Map<E, T> mapInverter(Map<T, E> hashMap){

        Map<E, T> newMap = new HashMap<>();

        for(Map.Entry<T, E> entry : hashMap.entrySet()){
            newMap.put(entry.getValue(), entry.getKey());
        }

        return newMap;
    }

    public static HashMap<String, Integer> calculateBonus(String bonusProperty) {
        final Hashtable<String, String> attributeTable = new Hashtable<>() {{
            put(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name(), "currentPlayerAP");
            put(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name(), "currentPlayerDP");
        }};

        HashMap<String, Integer> bonusStatMap = new HashMap<>();
        Array<EntityBonus> bonusArray = ProfileManager.getInstance().getProperty(bonusProperty, Array.class);

        if (bonusArray == null || bonusArray.isEmpty()) {
            return bonusStatMap;
        }

        HashMap<String, String> bonusEntityValues = new HashMap<>();
        for (EntityBonus entityBonus : bonusArray) {
            bonusEntityValues.put(entityBonus.getEntityProperty(), entityBonus.getValue());
        }

        for (String key : attributeTable.keySet()) {
            float bonusValue = parseFloat(bonusEntityValues.get(key));
            if (bonusValue > 1) {
                bonusStatMap.put(key, (int) bonusValue);
            } else {
                int playerStat = ProfileManager.getInstance().getProperty(attributeTable.get(key), Integer.class);
                int bonusStat = (int) Math.floor(playerStat * bonusValue);
                bonusStatMap.put(key, bonusStat);
            }
        }

        return bonusStatMap;
    }

}
