package com.tyron.layouteditor;

import android.content.Context;
import android.content.ContextWrapper;

public class EditorContext extends ContextWrapper {

    private final WidgetFactory widgetFactory;

    public EditorContext(Context base, WidgetFactory widgetFactory) {
        super(base);
        this.widgetFactory = widgetFactory;
    }

    public WidgetFactory getWidgetFactory(){
        return widgetFactory;
    }
}
