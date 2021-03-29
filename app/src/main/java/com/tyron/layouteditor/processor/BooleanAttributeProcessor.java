package com.tyron.layouteditor.processor;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

public abstract class BooleanAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    @Override
    public void handleValue(V view, Value value) {
        if (value.isPrimitive() && value.getAsPrimitive().isBoolean()) {
            setBoolean(view, value.getAsPrimitive().getAsBoolean());
        } else {
            process(view, precompile(value, view.getContext(), ((EditorContext) view.getContext()).getFunctionManager()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        Boolean bool = resource.getBoolean(view.getContext());
        setBoolean(view, null != bool ? bool : false);
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        setBoolean(view, a.getBoolean(0, false));
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        setBoolean(view, a.getBoolean(0, false));
    }

    public abstract void setBoolean(V view, boolean value);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        return ParseHelper.parseBoolean(value) ? EditorConstants.TRUE : EditorConstants.FALSE;
    }
}
