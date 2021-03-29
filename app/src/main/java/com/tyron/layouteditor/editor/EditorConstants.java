package com.tyron.layouteditor.editor;

import com.tyron.layouteditor.values.Primitive;

public class EditorConstants {

    public static final String TYPE = "type";
    public static final String LAYOUT = "layout";

    public static final String DATA = "data";
    public static final String COLLECTION = "collection";

    public static final String DATA_NULL = "null";

    public static final String STYLE_DELIMITER = "\\.";

    public static final String EMPTY = "";

    public static final Primitive EMPTY_STRING = new Primitive(EMPTY);
    public static final Primitive TRUE = new Primitive(true);
    public static final Primitive FALSE = new Primitive(false);

    private static boolean isLoggingEnabled = true;

    public static void setIsLoggingEnabled(boolean isLoggingEnabled) {
        EditorConstants.isLoggingEnabled = isLoggingEnabled;
    }

    public static boolean isLoggingEnabled() {
        return EditorConstants.isLoggingEnabled;
    }
}

