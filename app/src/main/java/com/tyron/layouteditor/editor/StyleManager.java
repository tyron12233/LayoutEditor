package com.tyron.layouteditor.editor;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.values.Value;

/**
 * StyleManager
 *
 * @author adityasharat
 */
public abstract class StyleManager {
    @Nullable
    protected abstract Styles getStyles();

    @Nullable
    public Map<String, Value> get(@NonNull String name) {
        return null != getStyles() ? getStyles().get(name) : null;
    }
}
