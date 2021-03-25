package com.tyron.layouteditor.toolbox;

import java.util.Iterator;

/**
 * SimpleArrayIterator
 *
 * @author adityasharat
 */

public class SimpleArrayIterator<E> implements Iterator<E> {

    private final E[] elements;
    private int cursor;

    public SimpleArrayIterator(E[] elements) {
        this.elements = elements;
        cursor = 0;
    }

    public static Iterator<Integer> createIntArrayIterator(final int[] elements) {
        return new Iterator<Integer>() {

            private int cursor;

            @Override
            public boolean hasNext() {
                return cursor < elements.length;
            }

            @Override
            public Integer next() {
                Integer e = elements[cursor];
                cursor++;
                return e;
            }
        };
    }

    @Override
    public boolean hasNext() {
        return cursor < elements.length;
    }

    @Override
    public E next() {
        E e = elements[cursor];
        cursor++;
        return e;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not allowed.");
    }

}