package com.gdx.game.common;

import java.util.HashMap;
import java.util.Map;

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

}
