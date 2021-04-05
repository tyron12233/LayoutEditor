package com.tyron.layouteditor.adapters.gson;

import com.tyron.layouteditor.values.Value;

/**
 * CustomValueTypeAdapterCreator
 *
 * @author adityasharat
 */
public abstract class CustomValueTypeAdapterCreator<V extends Value> {

  public abstract CustomValueTypeAdapter<V> create(int type, EditorTypeAdapterFactory factory);

}