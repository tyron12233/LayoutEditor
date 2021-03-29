package com.tyron.layouteditor.processor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.EditorLayoutInflater;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.DrawableValue;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

public abstract class DrawableResourceProcessor<V extends View> extends AttributeProcessor<V> {

    @Nullable
    public static Drawable evaluate(Value value, BaseWidget view) {
        if (null == value) {
            return null;
        }
        final Drawable[] d = new Drawable[1];
        DrawableResourceProcessor<View> processor = new DrawableResourceProcessor<View>() {
            @Override
            public void setDrawable(View view, Drawable drawable) {
                d[0] = drawable;
            }
        };
        processor.process(view.getAsView(), value);
        return d[0];
    }

    public static Value staticCompile(@Nullable Value value, Context context) {
        if (null == value) {
            return DrawableValue.ColorValue.BLACK;
        }
        if (value.isDrawable()) {
            return value;
        } else if (value.isPrimitive()) {
            Value precompiled = AttributeProcessor.staticPreCompile(value.getAsPrimitive(), context, null);
            if (null != precompiled) {
                return precompiled;
            }
            return DrawableValue.valueOf(value.getAsString(), context);
        } else if (value.isObject()) {
            return DrawableValue.valueOf(value.getAsObject(), context);
        } else {
            return DrawableValue.ColorValue.BLACK;
        }
    }

    @Override
    public void handleValue(final V view, Value value) {
        if (value.isDrawable()) {
            DrawableValue d = value.getAsDrawable();
            if (null != d) {
                //TODO: APPLY DRAWABLES
                EditorLayoutInflater.ImageLoader loader = ((BaseWidget) view).getViewManager().getContext().getLoader();
                d.apply((BaseWidget) view, loader, view.getContext(), new DrawableValue.Callback() {
                    @Override
                    public void apply(Drawable drawable) {
                        if (null != drawable) {
                            setDrawable(view, drawable);
                        }
                    }
                });
            }
        } else {
            process(view, precompile(value, view.getContext(), ((EditorContext) view.getContext()).getFunctionManager()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        Drawable d = resource.getDrawable(view.getContext());
        if (null != d) {
            setDrawable(view, d);
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
        Drawable d = a.getDrawable(0);
        if (null != d) {
            setDrawable(view, d);
        }
    }

    public abstract void setDrawable(V view, Drawable drawable);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        return staticCompile(value, context);
    }
}
