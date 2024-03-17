package com.gdx.game.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UtilityClass {


    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

}
