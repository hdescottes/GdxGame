package com.gdx.game.common;

import java.util.*;

public class UtilityClass {


    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static <T, E> Optional<T> getFirstKeyByValue(Map<T, E> map, E value){
        return getKeysByValue(map, value).stream().findFirst();
    }

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
