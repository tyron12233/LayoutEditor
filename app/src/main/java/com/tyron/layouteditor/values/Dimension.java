package com.tyron.layouteditor.values;

import android.content.Context;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.tyron.layouteditor.toolbox.BiMap;
import com.tyron.layouteditor.toolbox.HashBiMap;
import com.tyron.layouteditor.util.AndroidUtilities;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Dimension is a type of {@link Value} which hosts dimensions.
 * Any android dimension string can be parsed into a {@code Dimension}
 * object. Eg. "12dp", "16sp", etc.  A {@code Dimension} object is immutable.
 * </p>
 *
 * @author aditya.sharat
 */
public class Dimension extends Value {

    public static final int DIMENSION_UNIT_ENUM = -1;
    public static final int DIMENSION_UNIT_PX = TypedValue.COMPLEX_UNIT_PX;
    public static final int DIMENSION_UNIT_DP = TypedValue.COMPLEX_UNIT_DIP;
    public static final int DIMENSION_UNIT_SP = TypedValue.COMPLEX_UNIT_SP;
    public static final int DIMENSION_UNIT_PT = TypedValue.COMPLEX_UNIT_PT;
    public static final int DIMENSION_UNIT_IN = TypedValue.COMPLEX_UNIT_IN;
    public static final int DIMENSION_UNIT_MM = TypedValue.COMPLEX_UNIT_MM;

    public static final String MATCH_PARENT = "match_parent";
    public static final String FILL_PARENT = "fill_parent";
    public static final String WRAP_CONTENT = "wrap_content";

    public static final String SUFFIX_PX = "px";
    public static final String SUFFIX_DP = "dp";
    public static final String SUFFIX_SP = "sp";
    public static final String SUFFIX_PT = "pt";
    public static final String SUFFIX_IN = "in";
    public static final String SUFFIX_MM = "mm";

    public static final BiMap<String, Integer> sDimensionsMap = new HashBiMap<>(3);
    public static final BiMap<String, Integer> sDimensionsUnitsMap = new HashBiMap<>(6);
    public static final Dimension ZERO = new Dimension(0, DIMENSION_UNIT_PX);

    static {
        sDimensionsMap.put(FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sDimensionsUnitsMap.put(SUFFIX_PX, DIMENSION_UNIT_PX);
        sDimensionsUnitsMap.put(SUFFIX_DP, DIMENSION_UNIT_DP);
        sDimensionsUnitsMap.put(SUFFIX_SP, DIMENSION_UNIT_SP);
        sDimensionsUnitsMap.put(SUFFIX_PT, DIMENSION_UNIT_PT);
        sDimensionsUnitsMap.put(SUFFIX_IN, DIMENSION_UNIT_IN);
        sDimensionsUnitsMap.put(SUFFIX_MM, DIMENSION_UNIT_MM);
    }

    public final double value;
    public final int unit;

    private Dimension(float value, int unit) {
        this.value = value;
        this.unit = unit;
    }

    private Dimension(String dimension) {
        Integer parameter = sDimensionsMap.getValue(dimension);
        double value;
        int unit;

        if (parameter != null) {
            value = parameter;
            unit = DIMENSION_UNIT_ENUM;
        } else {
            int length = dimension.length();
            if (length < 2) {
                value = 0;
                unit = DIMENSION_UNIT_PX;
            } else { // find the units and value by splitting at the second-last character of the dimension
                Integer u = sDimensionsUnitsMap.getValue(dimension.substring(length - 2));
                String stringValue = dimension.substring(0, length - 2);
                if (u != null) {
                    value = parseFloat(stringValue);
                    unit = u;
                } else {
                    value = 0;
                    unit = DIMENSION_UNIT_PX;
                }
            }
        }

        this.value = value;
        this.unit = unit;
    }


    /**
     * This function returns a {@code Dimension} object holding the
     * value extracted from the specified {@code String}
     *
     * @param dimension the value to be parsed.
     */
    public static Dimension valueOf(String dimension) {
        if (null == dimension) {
            return ZERO;
        }
        Dimension d = DimensionCache.cache.get(dimension);
        if (null == d) {
            d = new Dimension(dimension);
            DimensionCache.cache.put(dimension, d);
        }
        return d;
    }

    public static Dimension valueOf(int val) {
        if (val == -1) {
            return Dimension.valueOf("match_parent");
        } else if (val == -2) {
            return Dimension.valueOf("wrap_content");
        } else {
            return new Dimension((float) AndroidUtilities.px(val), DIMENSION_UNIT_DP);
        }
    }

    public static float apply(String dimension, Context context) {
        return Dimension.valueOf(dimension).apply(context);
    }

    public static float parseFloat(String value) {
        float number = 0;
        if (null != value && value.length() > 0) {
            try {
                number = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                number = 0;
            }
        }
        return number;
    }

    public float apply(Context context) {
        double result = 0;

        switch (unit) {
            case DIMENSION_UNIT_ENUM:
                result = value;
                break;
            case DIMENSION_UNIT_PX:
            case DIMENSION_UNIT_DP:
            case DIMENSION_UNIT_SP:
            case DIMENSION_UNIT_PT:
            case DIMENSION_UNIT_MM:
            case DIMENSION_UNIT_IN:
                result = TypedValue.applyDimension(unit, (float) value, context.getResources().getDisplayMetrics());
                break;
        }

        return (float) result;
    }

    @Override
    public Value copy() {
        return this;
    }

    @NotNull
    @Override
    public String toString() {
        final String value;
        if (this.value % 1 == 0) {
            value = String.valueOf((int) this.value);
        } else {
            value = String.valueOf(this.value);
        }
        final String unit;
        if (this.unit == DIMENSION_UNIT_ENUM) {
            return sDimensionsMap.getKey((int) this.value);
        } else {
            unit = sDimensionsUnitsMap.getKey(this.unit);
            return value + unit;
        }
    }

    private static class DimensionCache {
        static final LruCache<String, Dimension> cache = new LruCache<>(64);
    }
}
