package com.tyron.layouteditor.processor;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.editor.FunctionManager;
import com.tyron.layouteditor.values.AttributeResource;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Resource;
import com.tyron.layouteditor.values.StyleResource;
import com.tyron.layouteditor.values.Value;

public abstract class AttributeProcessor<V extends View> {

    public static Value evaluate(final Context context, final Value input, final Value data, final int index) {
        final Value[] output = new Value[1];

        AttributeProcessor processor = new AttributeProcessor<View>() {

            @Override
            public void handleValue(View view, Value value) {
                output[0] = value;
            }

            @Override
            public void handleResource(View view, Resource resource) {
                output[0] = new Primitive(resource.getString(context));
            }

            @Override
            public void handleAttributeResource(View view, AttributeResource attribute) {
                output[0] = new Primitive(attribute.apply(context).getString(0));
            }

            @Override
            public void handleStyleResource(View view, StyleResource style) {
                output[0] = new Primitive(style.apply(context).getString(0));
            }
        };
        processor.process(null, input);

        return output[0];
    }

    @Nullable
    public static Value staticPreCompile(Primitive value, Context context, FunctionManager manager) {
        String string = value.getAsString();
        if (Resource.isResource(string)) {
            return Resource.valueOf(string, null, context);
        } else if (AttributeResource.isAttributeResource(string)) {
            return AttributeResource.valueOf(string, context);
        } else if (StyleResource.isStyleResource(string)) {
            return StyleResource.valueOf(string, context);
        }
        return null;
    }

    @Nullable
    public static Value staticPreCompile(ObjectValue object, Context context, FunctionManager manager) {
        return null;
    }

    @Nullable
    public static Value staticPreCompile(Value value, Context context, FunctionManager manager) {
        Value compiled = null;
        if (value.isPrimitive()) {
            compiled = staticPreCompile(value.getAsPrimitive(), context, manager);
        } else if (value.isObject()) {
            compiled = staticPreCompile(value.getAsObject(), context, manager);
        } else if (value.isResource() || value.isAttributeResource() || value.isStyleResource()) {
            return value;
        }
        return compiled;
    }

    public void process(V view, Value value) {
        if (value.isResource()) {
            handleResource(view, value.getAsResource());
        } else if (value.isAttributeResource()) {
            handleAttributeResource(view, value.getAsAttributeResource());
        } else if (value.isStyleResource()) {
            handleStyleResource(view, value.getAsStyleResource());
        } else {
            handleValue(view, value);
        }
    }


    public abstract void handleValue(V view, Value value);

    public abstract void handleResource(V view, Resource resource);

    public abstract void handleAttributeResource(V view, AttributeResource attribute);

    public abstract void handleStyleResource(V view, StyleResource style);

    public Value precompile(Value value, Context context, FunctionManager manager) {
        Value compiled = staticPreCompile(value, context, manager);
        return null != compiled ? compiled : compile(value, context);
    }

    public Value compile(@Nullable Value value, Context context) {
        return value;
    }

}
