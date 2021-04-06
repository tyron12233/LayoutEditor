package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.viewgroup.ScrollViewItem;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class ScrollViewParser<T extends ScrollView> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "ScrollView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "FrameLayout";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new ScrollViewItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ScrollView.Scrollbars, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if ("none".equals(value)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("horizontal".equals(value)) {
                    view.setHorizontalScrollBarEnabled(true);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("vertical".equals(value)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(true);
                } else {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                }
            }
        });
    }
}