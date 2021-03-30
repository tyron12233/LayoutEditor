package com.tyron.layouteditor.parser.custom;

import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.editor.widget.view.ButtonItem;
import com.tyron.layouteditor.parser.ViewParser;
import com.tyron.layouteditor.processor.StringAttributeProcessor;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;

public class ButtonParser<T extends Button> extends ViewParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "Button";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "TextView";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout layout, @NonNull ObjectValue data,
                                 @Nullable ViewGroup parent, int dataIndex) {
        return new ButtonItem(context);
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor(Attributes.TextView.Text, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setText(value);
            }
        });
    }
}
