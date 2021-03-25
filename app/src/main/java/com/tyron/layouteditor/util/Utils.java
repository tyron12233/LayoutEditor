package com.tyron.layouteditor.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.tyron.layouteditor.values.Array;
import com.tyron.layouteditor.values.ObjectValue;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

/**
 * @author Aditya Sharat
 */
public class Utils {

    public static final String LIB_NAME = "editor";
    public static final String VERSION = "1.0.0-SNAPSHOT";

    public static final int STYLE_NONE = 0;
    public static final int STYLE_SINGLE = 1;
    public static final int STYLE_DOUBLE = 2;

    public static ObjectValue addAllEntries(@NonNull ObjectValue destination, @NonNull ObjectValue source) {
        for (Map.Entry<String, Value> entry : source.entrySet()) {
            if (destination.get(entry.getKey()) != null) {
                continue;
            }
            destination.add(entry.getKey(), entry.getValue());
        }
        return destination;
    }

    public static String join(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(Array array, String delimiter, @QuoteStyle int style) {
        StringBuilder sb = new StringBuilder();
        Value value;
        for (int i = 0; i < array.size(); i++) {
            value = array.get(i);
            if (value.isPrimitive()) {
                Primitive primitive = value.getAsPrimitive();
                String string;
                switch (style) {
                    case STYLE_NONE:
                        string = primitive.getAsString();
                        break;
                    case STYLE_SINGLE:
                        string = primitive.getAsSingleQuotedString();
                        break;
                    case STYLE_DOUBLE:
                        string = primitive.getAsDoubleQuotedString();
                        break;
                    default:
                        string = primitive.getAsString();
                }
                sb.append(string);
            } else {
                sb.append(value.toString());
            }
            if (i < array.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(Array array, String delimiter) {
        return join(array, delimiter, STYLE_NONE);
    }

    public static String join(Value[] array, String delimiter, @QuoteStyle int style) {
        return join(new Array(array), delimiter, style);
    }

    public static String join(Value[] array, String delimiter) {
        return join(new Array(array), delimiter);
    }

    public static String getVersion() {
        return LIB_NAME + ":" + VERSION;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STYLE_NONE, STYLE_SINGLE, STYLE_DOUBLE})
    public @interface QuoteStyle {
    }
}
