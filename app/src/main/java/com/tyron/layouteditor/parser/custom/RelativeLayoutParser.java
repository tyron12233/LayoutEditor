package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.RelativeLayoutItem;
import com.tyron.layouteditor.processor.GravityAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser<T extends RelativeLayout> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "RelativeLayout";
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
        return new RelativeLayoutItem(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.View.Gravity, new GravityAttributeProcessor<T>() {
            @Override
            public void setGravity(T view, @Gravity int gravity) {
                view.setGravity(gravity);
            }
        });
    }
}