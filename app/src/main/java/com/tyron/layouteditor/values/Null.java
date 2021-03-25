package com.tyron.layouteditor.values;

public class Null extends Value {
    /**
     * singleton for JsonNull
     *
     * @since 1.8
     */
    public static final Null INSTANCE = new Null();

    private static final String NULL_STRING = "NULL";

    @Override
    public Null copy() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return NULL_STRING;
    }

    @Override
    public String getAsString() {
        return "";
    }

    /**
     * All instances of Null have the same hash code
     * since they are indistinguishable
     */
    @Override
    public int hashCode() {
        return Null.class.hashCode();
    }

    /**
     * All instances of JsonNull are the same
     */
    @Override
    public boolean equals(java.lang.Object other) {
        return this == other || other instanceof Null;
    }
}