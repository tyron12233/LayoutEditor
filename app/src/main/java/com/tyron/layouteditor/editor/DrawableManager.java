package com.tyron.layouteditor.editor;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class DrawableManager {

    @NonNull
    private final HashMap<String, String> drawables;

    public DrawableManager(HashMap<String,String> drawables) {
        this.drawables = drawables;
    }

    public String get(String name) {
        String drawable = drawables.get(name);

        if(drawable == null){
            return EditorConstants.DATA_NULL;
        }

        return drawable;
    }

    public void put(String name, String path){
        drawables.put(name, path);
    }
}
