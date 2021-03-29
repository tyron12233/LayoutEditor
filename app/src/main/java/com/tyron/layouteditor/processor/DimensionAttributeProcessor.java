package com.tyron.layouteditor.processor;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Dimension;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

/**
 *
 */
public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    public static float evaluate(Value value, BaseWidget view) {
        if (value == null) {
            return Dimension.ZERO.apply(view.getAsView().getContext());
        }

        final float[] result = new float[1];
        DimensionAttributeProcessor<View> processor = new DimensionAttributeProcessor<View>() {
            @Override
            public void setDimension(View view, float dimension) {
                result[0] = dimension;
            }
        };
        processor.process(view.getAsView(), value);

        return result[0];
    }

    public static Value staticCompile(@Nullable Value value, Context context) {
        if (null == value || !value.isPrimitive()) {
            return Dimension.ZERO;
        }
        if (value.isDimension()) {
            return value;
        }
        Value precompiled = AttributeProcessor.staticPreCompile(value.getAsPrimitive(), context, null);
        if (null != precompiled) {
            return precompiled;
        }
        return Dimension.valueOf(value.getAsString());
    }

    @Override
    public final void handleValue(T view, Value value) {
        if (value.isDimension()) {
            setDimension(view, value.getAsDimension().apply(view.getContext()));
        } else if (value.isPrimitive()) {
            process(view, precompile(value, view.getContext(), ((EditorContext) view.getContext()).getFunctionManager()));
        }
    }

    @Override
    public void handleResource(T view, Resource resource) {
        Float dimension = resource.getDimension(view.getContext());
        setDimension(view, null == dimension ? 0 : dimension);
    }

    @Override
    public void handleAttributeResource(T view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        setDimension(view, a.getDimensionPixelSize(0, 0));
    }

    @Override
    public void handleStyleResource(T view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        setDimension(view, a.getDimensionPixelSize(0, 0));
    }

    /**
     * @param view View
     */
    public abstract void setDimension(T view, float dimension);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        return staticCompile(value, context);
    }

}
