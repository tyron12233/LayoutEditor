package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.compat.ConstraintLayoutItem;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class ConstraintLayoutParser<T extends ConstraintLayout> extends ViewTypeParser<T> {
    @NonNull
    @Override
    public String getType() {
        return "ConstraintLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ConstraintLayoutItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}
