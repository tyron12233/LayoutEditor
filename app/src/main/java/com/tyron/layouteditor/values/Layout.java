package com.tyron.layouteditor.values;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public Layout merge(Layout include) {

        List<Attribute> attributes = null;
        if (this.attributes != null) {
            attributes = new ArrayList<>(this.attributes.size());
            attributes.addAll(this.attributes);
        }
        if (include.attributes != null) {
            if (attributes == null) {
                attributes = new ArrayList<>(include.attributes.size());
            }
            attributes.addAll(include.attributes);
        }

        Map<String, Value> data = null;
        if (this.data != null) {
            data = this.data;
        }
        if (include.data != null) {
            if (data == null) {
                data = new LinkedHashMap<>(include.data.size());
            }
            data.putAll(include.data);
        }

        ObjectValue extras = new ObjectValue();
        if (this.extras != null) {
            extras = Utils.addAllEntries(extras, this.extras);
        }
        if (include.extras != null) {
            if (extras == null) {
                extras = new ObjectValue();
            }
            Utils.addAllEntries(extras, include.extras);
        }

        return new Layout(type, attributes, data, extras);
    }

    /**
     * Attribute
     *
     * @author aditya.sharat
     */
    public static class Attribute {

        public final int id;
        public final Value value;

        public Attribute(int id, Value value) {
            this.id = id;
            this.value = value;
        }

        protected Attribute copy() {
            return new Attribute(id, value.copy());
        }
        
        @Override
        public int hashCode(){
            return value.hashCode();
        }
        
        @Override
        public boolean equals(Object obj){
            if(!(obj instanceof Attribute)){
                return false;
            }
            
            return this.id == (((Attribute)obj).id);    
         }
    }
}
