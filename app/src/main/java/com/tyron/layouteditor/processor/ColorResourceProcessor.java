package com.tyron.layouteditor.processor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.Color;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    public static Color.Result evaluate(Value value, BaseWidget view) {
        final Color.Result[] result = new Color.Result[1];
        ColorResourceProcessor<View> processor = new ColorResourceProcessor<View>() {
            @Override
            public void setColor(View view, int color) {
                result[0] = Color.Result.color(color);
            }

            @Override
            public void setColor(View view, ColorStateList colors) {
                result[0] = Color.Result.colors(colors);
            }
        };
        processor.process(view.getAsView(), value);
        return result[0];
    }

    public static Value staticCompile(@Nullable Value value, Context context) {
        if (null == value) {
            return Color.Int.BLACK;
        }
        if (value.isColor()) {
            return value;
        } else if (value.isObject()) {
            return Color.valueOf(value.getAsObject(), context);
        } else if (value.isPrimitive()) {
            Value precompiled = AttributeProcessor.staticPreCompile(value.getAsPrimitive(), context, null);
            if (null != precompiled) {
                return precompiled;
            }
            return Color.valueOf(value.getAsString(), Color.Int.BLACK);
        } else {
            return Color.Int.BLACK;
        }
    }

    @Override
    public void handleValue(final V view, Value value) {
        if (value.isColor()) {
            apply(view, value.getAsColor());
        } else {
            process(view, precompile(value, view.getContext(), ((EditorContext) view.getContext()).getFunctionManager()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        ColorStateList colors = resource.getColorStateList(view.getContext());
        if (null != colors) {
            setColor(view, colors);
        } else {
            Integer color = resource.getColor(view.getContext());
            setColor(view, null == color ? Color.Int.BLACK.value : color);
        }
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        set(view, a);
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        set(view, a);
    }

    private void set(V view, TypedArray a) {
        ColorStateList colors = a.getColorStateList(0);
        if (null != colors) {
            setColor(view, colors);
        } else {
            setColor(view, a.getColor(0, Color.Int.BLACK.value));
        }
    }

    private void apply(V view, Color color) {
        Color.Result result = color.apply(view.getContext());
        if (null != result.colors) {
            setColor(view, result.colors);
        } else {
            setColor(view, result.color);
        }
    }

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        return staticCompile(value, context);
    }
}
