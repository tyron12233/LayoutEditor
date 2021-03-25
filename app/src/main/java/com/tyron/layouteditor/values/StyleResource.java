package com.tyron.layouteditor.values;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * StyleResource
 *
 * @author aditya.sharat
 */

public class StyleResource extends Value {

    public static final StyleResource NULL = new StyleResource(-1, -1);
    private static final Map<String, Integer> styleMap = new HashMap<>();
    private static final Map<String, Integer> attributeMap = new HashMap<>();
    private static final Map<String, Class> sHashMap = new HashMap<>();
    private static final String ATTR_START_LITERAL = "?";

    public final int styleId;
    public final int attributeId;

    private StyleResource(String value, Context context) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        String[] tokens = value.substring(1, value.length()).split(":");
        String style = tokens[0];
        String attr = tokens[1];
        Class clazz;

        Integer styleId = styleMap.get(style);
        if (styleId == null) {
            String className = context.getPackageName() + ".R$style";
            clazz = sHashMap.get(className);
            if (null == clazz) {
                clazz = Class.forName(className);
                sHashMap.put(className, clazz);
            }
            styleId = clazz.getField(style).getInt(null);
            styleMap.put(style, styleId);
        }
        this.styleId = styleId;
        Integer attrId = attributeMap.get(attr);
        if (attrId == null) {
            String className = context.getPackageName() + ".R$attr";
            clazz = sHashMap.get(className);
            if (null == clazz) {
                clazz = Class.forName(className);
                sHashMap.put(className, clazz);
            }
            attrId = clazz.getField(attr).getInt(null);
            attributeMap.put(attr, attrId);
        }
        this.attributeId = attrId;
    }

    private StyleResource(int styleId, int attributeId) {
        this.styleId = styleId;
        this.attributeId = attributeId;
    }

    public static boolean isStyleResource(String value) {
        return value.startsWith(ATTR_START_LITERAL);
    }

    @Nullable
    public static StyleResource valueOf(String value, Context context) {
        StyleResource style = StyleCache.cache.get(value);
        if (null == style) {
            try {
                style = new StyleResource(value, context);
            } catch (Exception e) {

                    e.printStackTrace();

                style = NULL;
            }
            StyleCache.cache.put(value, style);
        }
        return NULL == style ? null : style;
    }

    @NonNull
    public static StyleResource valueOf(int styleId, int attributeId) {
        return new StyleResource(styleId, attributeId);
    }

    public TypedArray apply(Context context) {
        return context.obtainStyledAttributes(styleId, new int[]{attributeId});
    }

    @Override
    public Value copy() {
        return this;
    }

    private static class StyleCache {
        static final LruCache<String, StyleResource> cache = new LruCache<>(64);
    }
}