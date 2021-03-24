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

import xyz.truenight.utils.interfaces.Supplier;

/**
 * Key-value cache based on weak references
 */
public class Cache {
    private Cache() {
    }

    private static final MemoryCache<Object, Object> CACHE = new MemoryCache<>();

    /**
     * Registers creator of item and returns item from cache or creator
     */
    @SuppressWarnings("unchecked")
    public static <T> T that(Object key, Supplier<T> what) {
        if (CACHE.get(key) != null) {
            try {
                return (T) CACHE.get(key);
            } catch (ClassCastException e) {
                System.out.print("E/Cache: Can't use cached object: wrong type");
            }
        }

        T value = what.get();
        CACHE.put(key, value);
        return value;
    }

    /**
     * Puts item to cache
     */
    public static <T> T that(Object key, T what) {
        CACHE.put(key, what);
        return what;
    }

    /**
     * Returns cached item
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {
        try {
            return (T) CACHE.get(key);
        } catch (ClassCastException e) {
            System.out.print("E/Cache: Can't use cached object: wrong type");
        }
        return null;
    }

    /**
     * Returns cached item and removes it from cache
     */
    @SuppressWarnings("unchecked")
    public static <T> T poll(Object key) {
        try {
            return (T) CACHE.remove(key);
        } catch (ClassCastException e) {
            System.out.print("E/Cache: Can't use cached object: wrong type");
        }
        return null;
    }
}