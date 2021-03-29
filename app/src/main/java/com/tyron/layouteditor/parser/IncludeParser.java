package com.tyron.layouteditor.parser;

import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.EditorConstants;
import com.tyron.layouteditor.editor.EditorContext;
import com.tyron.layouteditor.editor.ViewTypeParser;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Value;

/**
 * IncludeParser
 * <p>
 * TODO: merge the attributes into the included layout
 * </p>
 *
 * @author aditya.sharat
 */

public class IncludeParser<V extends View> extends ViewTypeParser<V> {

    @NonNull
    @Override
    public String getType() {
        return "include";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "View";
    }

    @NonNull
    @Override
    public BaseWidget createView(@NonNull EditorContext context, @NonNull Layout include, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {

        if (include.extras == null) {
            throw new IllegalArgumentException("required attribute 'layout' missing.");
        }

        Value type = include.extras.get(EditorConstants.LAYOUT);
        if (null == type || !type.isPrimitive()) {
            throw new InflateException("required attribute 'layout' missing or is not a string");
        }

        Layout layout = context.getLayout(type.getAsString());
        if (null == layout) {
            throw new InflateException("Layout '" + type + "' not found");
        }

        return context.getInflater().inflate(layout.merge(include), data, parent, dataIndex);
    }

    @Override
    protected void addAttributeProcessors() {

    }

}
