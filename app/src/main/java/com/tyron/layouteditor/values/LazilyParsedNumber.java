package com.tyron.layouteditor.values;

import java.math.BigDecimal;

/**
 * LazilyParsedNumber
 * <p>
 * This class is copied from Google's implementation of
 * LazilyParsedNumber which can be found at the following
 * link @{link https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/internal/LazilyParsedNumber.java}
 * </p>
 *
 * @author adityasharat
 */

public class LazilyParsedNumber extends Number {

    private final String value;

    /**
     * @param value must not be null
     */
    public LazilyParsedNumber(String value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            try {
                return (int) Long.parseLong(value);
            } catch (NumberFormatException nfe) {
                return new BigDecimal(value).intValue();
            }
        }
    }

    @Override
    public long longValue() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return new BigDecimal(value).longValue();
        }
    }

    @Override
    public float floatValue() {
        return Float.parseFloat(value);
    }

    @Override
    public double doubleValue() {
        return Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LazilyParsedNumber) {
            LazilyParsedNumber other = (LazilyParsedNumber) obj;
            //noinspection StringEquality
            return value == other.value || value.equals(other.value);
        }
        return false;
    }
}
