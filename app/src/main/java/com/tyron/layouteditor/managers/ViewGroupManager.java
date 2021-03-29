package com.tyron.layouteditor.managers;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.DataContext;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class ViewGroupManager extends ViewManager {

    public boolean hasDataBoundChildren;

    public ViewGroupManager(@NonNull EditorContext context, @NonNull ViewTypeParser parser,
                            @NonNull View view, @NonNull Layout layout, @NonNull DataContext dataContext) {
        super(context, parser, view, layout, dataContext);
        hasDataBoundChildren = false;
    }

    @Override
    public void update(@Nullable ObjectValue data) {
        super.update(data);
        updateChildren();
    }

    protected void updateChildren() {
        if (!hasDataBoundChildren && view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int count = parent.getChildCount();
            View child;

            for (int index = 0; index < count; index++) {
                child = parent.getChildAt(index);
                if (child instanceof BaseWidget) {
                    ((BaseWidget) child).getViewManager().update(dataContext.getData());
                }
            }
        }
    }
}