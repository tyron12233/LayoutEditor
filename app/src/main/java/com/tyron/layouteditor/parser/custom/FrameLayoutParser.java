package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.FrameLayoutItem;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class FrameLayoutParser<T extends FrameLayout> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "FrameLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new FrameLayoutItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}