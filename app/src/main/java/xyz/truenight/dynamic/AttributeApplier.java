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

package xyz.truenight.dynamic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapter;
import xyz.truenight.dynamic.adapter.attr.TypedAttrAdapters;
import xyz.truenight.dynamic.adapter.param.TypedParamAdapter;
import xyz.truenight.dynamic.adapter.param.TypedParamAdapters;
import xyz.truenight.utils.Utils;

public final class AttributeApplier {

    private static final String TAG = AttributeApplier.class.getSimpleName();
    public static final AttributeApplier DEFAULT = new AttributeApplier();

    private List<TypedAttrAdapter> mTypedAttrAdapters;

    private List<TypedParamAdapter> mTypedParamAdapters;

    public AttributeApplier() {
        mTypedAttrAdapters = new ArrayList<>(TypedAttrAdapters.DEFAULT);
        mTypedParamAdapters = new ArrayList<>(TypedParamAdapters.DEFAULT);
    }

    public AttributeApplier(List<TypedAttrAdapter> typedAttrAdapters, List<TypedParamAdapter> typedParamAdapters) {
        mTypedAttrAdapters = new ArrayList<>(typedAttrAdapters);
        mTypedParamAdapters = new ArrayList<>(typedParamAdapters);
    }

    public void addAttrAdapter(TypedAttrAdapter adapter) {
        mTypedAttrAdapters.add(0, adapter);
    }

    public void addParamAdapter(TypedParamAdapter adapter) {
        mTypedParamAdapters.add(0, adapter);
    }

    @Override
    public AttributeApplier clone() {
        return new AttributeApplier(mTypedAttrAdapters, mTypedParamAdapters);
    }

    private List<TypedAttrAdapter> getTypedAdapters(View view) {
        List<TypedAttrAdapter> adapters = new ArrayList<>();
        for (TypedAttrAdapter typedAttrAdapter : mTypedAttrAdapters) {
            if (typedAttrAdapter.isSuitable(view)) {
                adapters.add(typedAttrAdapter);
            }
        }
        return adapters;
    }

    public void applyAttrs(View view, AttributeSet attrs) {
        // count is too expensive so catching it
        int attributeCount = attrs.getAttributeCount();

        // select adapters for this class
        // may be cache adapters for specific class
        List<TypedAttrAdapter> adapters = getTypedAdapters(view);

        // http://schemas.android.com/apk/res/android
        // http://schemas.android.com/apk/res-auto
        for (int i = 0; i < attributeCount; i++) {
            String name = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);
            // at start apply to base classes like View
            // then go to concrete
            name = getNameWithNamespace(attrs, name, value);
            applyAttribute(adapters, view, name, value);
        }
    }

    private String getNameWithNamespace(AttributeSet attrs, String name, String value) {
        // todo add tools namespace support
        // work around to detect namespace
        return Utils.equal(value, attrs.getAttributeValue("http://schemas.android.com/apk/res/android", name)) ? "android:" + name : "app:" + name;
    }

    @SuppressWarnings("unchecked")
    private boolean applyAttribute(List<TypedAttrAdapter> adapters, View view, String name, String value) {
        for (TypedAttrAdapter adapter : adapters) {
            if (adapter.apply(view, name, value)) {
                return true;
            }
        }
        return false;
    }

    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup viewGroup, AttributeSet attrs) {
        ViewGroup.LayoutParams params = generateDefaultLayoutParams(viewGroup);
        // count is too expensive so catching it
        int attributeCount = attrs.getAttributeCount();
        List<TypedParamAdapter> adapters = getTypedParamAdapters(params);

        boolean hasWidth = false;
        boolean hasHeight = false;
        for (int i = 0; i < attributeCount; i++) {
            String name = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);
            name = getNameWithNamespace(attrs, name, value);
            if (applyParam(adapters, viewGroup.getContext(), params, name, value)) {
                if (Utils.equal(name, TypedParamAdapter.LAYOUT_HEIGHT)) {
                    hasHeight = true;
                } else if (Utils.equal(name, TypedParamAdapter.LAYOUT_WIDTH)) {
                    hasWidth = true;
                }
            }
        }
        if (!hasHeight || !hasWidth) {
            throw new InflateException("REQUIRED attribute layout_height OR layout_width in NOT set");
        }
        return params;
    }

    private boolean applyParam(List<TypedParamAdapter> adapters, Context context, ViewGroup.LayoutParams params, String name, String value) {
        for (TypedParamAdapter adapter : adapters) {
            if (adapter.apply(context, params, name, value)) {
                return true;
            }
        }
        return false;
    }

    private List<TypedParamAdapter> getTypedParamAdapters(ViewGroup.LayoutParams params) {
        List<TypedParamAdapter> adapters = new ArrayList<>();
        for (TypedParamAdapter typedAttrAdapter : mTypedParamAdapters) {
            if (typedAttrAdapter.isSuitable(params)) {
                adapters.add(typedAttrAdapter);
            }
        }
        return adapters;
    }

    public static ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent) {
        try {
            Method generate = ViewGroup.class.getDeclaredMethod("generateDefaultLayoutParams");
            generate.setAccessible(true);
            return (ViewGroup.LayoutParams) generate.invoke(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
