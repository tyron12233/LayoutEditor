package com.tyron.layouteditor;

import android.content.Context;
import android.content.ContextWrapper;

public class EditorContext extends ContextWrapper {

    private final WidgetFactory widgetFactory;
    private final FunctionManager functionManager;

    public EditorContext(Context base, WidgetFactory widgetFactory, FunctionManager manager) {
        super(base);
        this.functionManager = manager;
        this.widgetFactory = widgetFactory;
    }

    public WidgetFactory getWidgetFactory(){
        return widgetFactory;
    }

    public FunctionManager getFunctionManager(){
        return functionManager;
    }
}
