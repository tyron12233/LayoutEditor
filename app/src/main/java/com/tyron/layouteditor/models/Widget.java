package com.tyron.layouteditor.models;

import java.util.HashMap;

public class Widget {

    public static String LINEAR_LAYOUT = "com.tyron.layouteditor.editor.widget.LinearLayoutItem";
    public static String RELATIVE_LAYOUT = "com.tyron.layouteditor.editor.widget.RelativeLayoutItem";

	public static final String TEXTVIEW = "com.tyron.layouteditor.editor.widget.TextViewItem";

    private String clazz;
    private HashMap<String, Object> attributes;

    public Widget(String clazz){
        this.clazz = clazz;
    }

    public void setClazz(String clazz){
        this.clazz = clazz;
    }

    public String getClazz(){
        return clazz;
    }

    public String getName(){
        return clazz.replace("com.tyron.layouteditor.editor.widget.", "").replace("Item", "");
    }
}
