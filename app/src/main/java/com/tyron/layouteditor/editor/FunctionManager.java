package com.tyron.layouteditor.editor;

import androidx.annotation.NonNull;

import java.util.Map;

public class FunctionManager {

    @NonNull
    private final Map<String, Function> functions;

    public FunctionManager(@NonNull Map<String, Function> functions) {
        this.functions = functions;
    }

    @NonNull
    public Function get(@NonNull String name) {
        Function function = functions.get(name);
        if (function == null) {
            function = Function.NOOP;
        }
        return function;
    }
}