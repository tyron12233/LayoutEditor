package com.tyron.layouteditor.parser.compat;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.CoordinatorLayoutItem;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class CoordinatorLayoutParser<V extends CoordinatorLayout> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "CoordinatorLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new CoordinatorLayoutItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}
