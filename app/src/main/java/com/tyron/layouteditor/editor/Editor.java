package com.tyron.layouteditor.editor;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import java.util.HashMap;
import java.util.Map;

public final class Editor {

    @NonNull
    public final FunctionManager functions;

    @NonNull
    private final Map<String, Type> types;

    @NonNull
    private final Map<String, ViewTypeParser> parsers;

    Editor(@NonNull Map<String, Type> types, @NonNull final Map<String, Function> functions) {
        this.types = types;
        this.functions = new FunctionManager(functions);
        this.parsers = map(types);
    }

    public boolean has(@NonNull @Size(min = 1) String type) {
        return types.containsKey(type);
    }

    @Nullable
    public ViewTypeParser.AttributeSet.Attribute getAttributeId(@NonNull @Size(min = 1) String name, @NonNull @Size(min = 1) String type) {
        return types.get(type).getAttributeId(name);
    }

    private Map<String, ViewTypeParser> map(Map<String, Type> types) {
        Map<String, ViewTypeParser> parsers = new HashMap<>(types.size());
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            parsers.put(entry.getKey(), entry.getValue().parser);
        }
        return parsers;
    }

    @NonNull
    public EditorContext createContext(@NonNull Context base) {
        return createContextBuilder(base).build();
    }

    @NonNull
    public EditorContext.Builder createContextBuilder(@NonNull Context base) {
        return new EditorContext.Builder(base, parsers, functions);
    }

    public static class Type {

        public final int id;
        public final String type;
        public final ViewTypeParser parser;

        private final ViewTypeParser.AttributeSet attributes;

        Type(int id, @NonNull String type, @NonNull ViewTypeParser parser, @NonNull ViewTypeParser.AttributeSet attributes) {
            this.id = id;
            this.type = type;
            this.parser = parser;
            this.attributes = attributes;
        }

        @Nullable
        public ViewTypeParser.AttributeSet.Attribute getAttributeId(String name) {
            return attributes.getAttribute(name);
        }
    }
}