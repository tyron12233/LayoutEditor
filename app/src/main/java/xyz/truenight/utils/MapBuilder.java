package xyz.truenight.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by true
 * date: 11/02/2017
 * time: 22:06
 */
public class MapBuilder<K, V> {

    private Map<K, V> map;

    public MapBuilder() {
        map = new HashMap<>();
    }

    public MapBuilder(Map<K, V> to) {
        map = to;
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putIf(boolean condition, K key, V value) {
        if (condition) {
            map.put(key, value);
        }
        return this;
    }

    public MapBuilder<K, V> putNotNull(K key, V value) {
        if (value != null) {
            map.put(key, value);
        }
        return this;
    }

    public MapBuilder<K, V> putNotNullIf(boolean condition, K key, V value) {
        if (condition && value != null) {
            map.put(key, value);
        }
        return this;
    }

    public MapBuilder<K, V> putAll(Map<? extends K, ? extends V> from) {
        Utils.putAll(map, from);
        return this;
    }

    public MapBuilder<K, V> putAllIf(boolean condition, Map<? extends K, ? extends V> from) {
        if (condition) {
            Utils.putAll(map, from);
        }
        return this;
    }

    public Map<K, V> build() {
        return map;
    }
}
