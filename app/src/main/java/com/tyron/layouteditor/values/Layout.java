package com.tyron.layouteditor.values;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.models.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Layout extends Value {

    @NonNull
    public final String type;

    @Nullable
    public final List<Attribute> attributes;

    @Nullable
    public final Map<String, Value> data;

    @Nullable
    public final ObjectValue extras;

    public Layout(@NonNull String type, @Nullable List<Attribute> attributes, @Nullable Map<String, Value> data, @Nullable ObjectValue extras) {
        this.type = type;
        this.attributes = attributes;
        this.data = data;
        this.extras = extras;
    }

    @Override
    public Layout copy() {
        List<Attribute> attributes = null;
        if (this.attributes != null) {
            attributes = new ArrayList<>(this.attributes.size());
            for (Attribute attribute : this.attributes) {
                attributes.add(attribute.copy());
            }
        }

        return new Layout(type, attributes, data, extras);
    }
//	public static class Attribute {
//
//		public final int id;
//		public final Value value;
//
//		public Attribute(int id, Value value) {
//			this.id = id;
//			this.value = value;
//		}
//
//		protected Attribute copy() {
//			return new Attribute(id, value.copy());
//		}
//	}
}
