package com.tyron.layouteditor.processor;

import android.view.View;

import androidx.annotation.NonNull;

import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

/**
 * NumberAttributeProcessor
 *
 * @author adityasharat
 */

public abstract class NumberAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    @Override
    public void handleValue(V view, Value value) {
        if (value.isPrimitive()) {
            setNumber(view, value.getAsPrimitive().getAsNumber());
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        Integer number = resource.getInteger(view.getContext());
        setNumber(view, null != number ? number : 0);
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        setNumber(view, attribute.apply(view.getContext()).getFloat(0, 0f));
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        setNumber(view, style.apply(view.getContext()).getFloat(0, 0f));
    }

    public abstract void setNumber(V view, @NonNull Number value);
}