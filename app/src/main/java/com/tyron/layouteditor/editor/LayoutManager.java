package com.tyron.layouteditor.editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.values.Layout;

import java.util.Map;

public abstract class LayoutManager {

    @Nullable
    protected abstract Map<String, Layout> getLayouts();

    @Nullable
    public Layout get(@NonNull String name) {
        return null != getLayouts() ? getLayouts().get(name) : null;
    }
}
