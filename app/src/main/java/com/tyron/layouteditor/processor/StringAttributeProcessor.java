package com.tyron.layouteditor.processor;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    /**
     * @param view  View
     * @param value
     */
    @Override
    public void handleValue(V view, Value value) {
        if (value.isPrimitive() || value.isNull()) {
            setString(view, value.getAsString());
        } else {
            setString(view, "[Object]");
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        String string = resource.getString(view.getContext());
        setString(view, null == string ? EditorConstants.EMPTY : string);
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        setString(view, a.getString(0));
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        setString(view, a.getString(0));
    }

    /**
     * @param view View
     */
    public abstract void setString(V view, String value);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        if (null == value || value.isNull()) {
            return EditorConstants.EMPTY_STRING;
        }
        return value;
    }
}