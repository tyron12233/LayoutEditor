package com.tyron.layouteditor.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tyron.layouteditor.adapters.InterfaceAdapter;

public abstract class Value {

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Value.class, new InterfaceAdapter<Value>())
                .create();
    }

    public abstract Value copy();

    public boolean isPrimitive() {
        return this instanceof Primitive;
    }

    /**
     * provides check for verifying if this value represents a null value or not.
     *
     * @return true if this value is of type {@link Null}, false otherwise.
     * @since 1.2
     */
    public boolean isNull() {
        return this instanceof Null;
    }

    public boolean isObject() {
        return this instanceof ObjectValue;
    }

    public boolean isLayout() {
        return this instanceof Layout;
    }

    public boolean isDimension() {
        return this instanceof Dimension;
    }

    public ObjectValue getAsObject() {
        if (isObject()) {
            return (ObjectValue) this;
        }
        throw new IllegalStateException("Not an ObjectValue: " + this);
    }

    public Layout getAsLayout() {
        if (isLayout()) {
            return (Layout) this;
        }
        throw new IllegalStateException("Not a Layout: " + this);
    }

    public Dimension getAsDimension() {
        if (isDimension()) {
            return (Dimension) this;
        }
        throw new IllegalStateException("Not a dimension: " + this);
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a string value.
     *
     * @return get this value as a string value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            string value.
     */
    public String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive double value.
     *
     * @return get this value as a primitive double value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            double value.
     */
    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive float value.
     *
     * @return get this value as a primitive float value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            float value.
     */
    public float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive long value.
     *
     * @return get this value as a primitive long value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            long value.
     */
    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive integer value.
     *
     * @return get this value as a primitive integer value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            integer value.
     */
    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive character value.
     *
     * @return get this value as a primitive char value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            char value.
     */
    public char getAsCharacter() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }
}
