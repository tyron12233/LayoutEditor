package com.tyron.layouteditor.editor;

import com.tyron.layouteditor.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Styles
 *
 * @author Aditya Sharat
 */
public class Styles extends HashMap<String, Map<String, Value>> {

    public Map<String, Value> getStyle(String name) {
        return this.get(name);
    }

    public boolean contains(String name) {
        return this.containsKey(name);
    }
}
