/**
 * Copyright (C) 2017 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.dynamic.compat;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.tyron.layouteditor.editor.EditorContext;

import java.util.List;

import xyz.truenight.dynamic.AttributeApplier;
import xyz.truenight.dynamic.DynamicLayoutInflater;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapter;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapters;
import xyz.truenight.dynamic.adapter.param.TypedParamAdapter;
import xyz.truenight.dynamic.adapter.param.TypedParamAdapters;
import xyz.truenight.dynamic.compat.adapter.attr.CompatImageViewAttrAdapter;
import xyz.truenight.dynamic.compat.adapter.attr.CompatTextViewAttrAdapter;
import xyz.truenight.utils.Utils;

public class CompatDynamicLayoutInflater extends DynamicLayoutInflater {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit."
    };


    public static final TypedAttrAdapter COMPAT_IMAGE_VIEW_ADAPTER = new CompatImageViewAttrAdapter();

    private static List<TypedAttrAdapter> ATTR_DEFAULT = Utils.add(
            TypedAttrAdapters.VIEW_ADAPTER,
            TypedAttrAdapters.TEXT_VIEW_ADAPTER,
            COMPAT_IMAGE_VIEW_ADAPTER,
            TypedAttrAdapters.IMAGE_VIEW_ADAPTER,
            TypedAttrAdapters.LINEAR_LAYOUT_ADAPTER,
            TypedAttrAdapters.RELATIVE_LAYOUT_ADAPTER,
            new CompatTextViewAttrAdapter()
    );

    private static List<TypedParamAdapter> PARAM_DEFAULT = Utils.add(
            TypedParamAdapters.VIEW_GROUP_ADAPTER,
            TypedParamAdapters.MARGIN_ADAPTER,
            TypedParamAdapters.FRAME_LAYOUT_ADAPTER,
            TypedParamAdapters.LINEAR_LAYOUT_ADAPTER
    );

    @NonNull
    private static AttributeApplier getDefaultApplier() {
        return new AttributeApplier(ATTR_DEFAULT, PARAM_DEFAULT);
    }

    /**
     * Initializing of base {@link CompatDynamicLayoutInflater}
     * <p>
     * Can be used for setting params which will be copied
     * to instances given by {@link CompatDynamicLayoutInflater#from(EditorContext)}
     *
     * @param context Application context
     * @return Base inflater
     */
    public static Builder base(EditorContext context) {
        return new Builder(context);
    }

    public static DynamicLayoutInflater from(EditorContext context) {
        DynamicLayoutInflater unwrap = Utils.unwrap(DynamicLayoutInflater.mBase);
        if (unwrap == null) {
            // clone for factory unlock
            return new CompatDynamicLayoutInflater(context).cloneInContext(context);
        } else {
            return new CompatDynamicLayoutInflater(unwrap, context);
        }
    }

    /**
     * Instead of instantiating directly, you should retrieve an instance
     * through {@link CompatDynamicLayoutInflater#from(EditorContext)}
     *
     * @param context The Context in which to find resources and other
     *                application-specific things.
     */
    protected CompatDynamicLayoutInflater(EditorContext context) {
        super(context);
        setAttributeApplier(getDefaultApplier());
        //setFactory2(new CompatViewInflater());
    }

    protected CompatDynamicLayoutInflater(DynamicLayoutInflater original, EditorContext newContext) {
        super(original, newContext);
    }


    /**
     * Override onCreateView to instantiate names that correspond to the
     * widgets known to the Widget factory. If we don't find a match,
     * call through to our super class.
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    public DynamicLayoutInflater cloneInContext(EditorContext newContext) {
        return new CompatDynamicLayoutInflater(this, newContext);
    }

    public static class Builder extends DynamicLayoutInflater.Builder {

        private Builder(EditorContext context) {
            super(context);
        }

        @Override
        protected AttributeApplier newAttributeApplier() {
            return getDefaultApplier();
        }

        @Override
        protected DynamicLayoutInflater instance(EditorContext context) {
            return from(context);
        }
    }
}