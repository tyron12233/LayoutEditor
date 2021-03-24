/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class ConcurrentHashMap<K, V> implements Map<K, V> {
    private final Map<K, V> MAP = new java.util.concurrent.ConcurrentHashMap<K, V>();
    private final AtomicReference<V> NULL_KEY = new AtomicReference<V>();

    public int size() {
        return MAP.size() + (NULL_KEY.get() == null ? 0 : 1);
    }

    public boolean isEmpty() {
        return MAP.isEmpty() && NULL_KEY.get() == null;
    }

    public boolean containsKey(Object key) {
        return (key == null && NULL_KEY.get() != null) || MAP.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return Utils.equal(NULL_KEY.get(), value) || MAP.containsValue(value);
    }


    public V get(Object key) {
        return key == null ? NULL_KEY.get() : MAP.get(key);
    }

    public <T> T get(Object key, Class<T> clazz) {
        return key == null ? (T) NULL_KEY.get() : (T) MAP.get(key);
    }

    public V put(K key, V value) {
        return key == null ?
                NULL_KEY.getAndSet(value) :
                (
                        value == null ?
                                MAP.remove(key) :
                                MAP.put(key, value)
                );
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(Object key) {
        return key == null ? NULL_KEY.getAndSet(null) : MAP.remove(key);
    }

    public void clear() {
        NULL_KEY.set(null);
        MAP.clear();
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        if (NULL_KEY.get() != null) {
            set.add(null);
        }
        set.addAll(MAP.keySet());
        return set;
    }

    @Override
    public Collection<V> values() {
        ArrayList list = new ArrayList();
        if (NULL_KEY.get() != null) {
            list.add(NULL_KEY.get());
        }
        list.addAll(MAP.values());
        return list;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        HashSet<Entry<K, V>> set = new HashSet<>();
        if (NULL_KEY.get() != null) {
            set.add(new AbstractMap.SimpleEntry<K, V>(null, NULL_KEY.get()));
        }

        set.addAll(MAP.entrySet());
        return set;
    }

    public boolean compare(Object key, V value) {
        Object storedValue = get(key);
        return storedValue == null ? value == null : storedValue.equals(value);
    }
}