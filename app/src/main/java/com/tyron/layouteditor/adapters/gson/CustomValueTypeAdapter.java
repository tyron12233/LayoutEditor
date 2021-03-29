package com.tyron.layouteditor.adapters.gson;

import com.tyron.layouteditor.values.Value;
import com.google.gson.TypeAdapter;

/**
 * CustomValueTypeAdapter
 *
 * @author adityasharat
 */
public abstract class CustomValueTypeAdapter<V extends Value> extends TypeAdapter<V> {

  public final int type;

  protected CustomValueTypeAdapter(int type) {
    this.type = type;
  }

}