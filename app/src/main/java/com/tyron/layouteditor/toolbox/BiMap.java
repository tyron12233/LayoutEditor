package com.tyron.layouteditor.toolbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * BiMap
 *
 * @author adityasharat
 */

public interface BiMap<K, V> {

    @Nullable
    V put(@Nullable K key, @Nullable V value);

    @Nullable
    V put(@Nullable K key, @Nullable V value, boolean force);

    @Nullable
    V getValue(@NonNull K key);

    @Nullable
    K getKey(@NonNull V value);

    void putAll(@NonNull Map<? extends K, ? extends V> map);

    @NonNull
    Set<V> values();

    @NonNull
    BiMap<V, K> inverse();
}