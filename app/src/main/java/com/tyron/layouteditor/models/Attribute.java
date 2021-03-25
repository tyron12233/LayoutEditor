package com.tyron.layouteditor.models;

import androidx.annotation.Nullable;

import com.tyron.layouteditor.values.Value;

public class Attribute {

    public String key;
    public Value value;

    public Attribute(String key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Attribute copy() {
        return new Attribute(key, value.copy());
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Attribute)) {
            return false;
        }

        return this.key.equals(((Attribute) obj).key);
    }
}
