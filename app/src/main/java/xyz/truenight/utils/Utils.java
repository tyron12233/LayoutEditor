/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import xyz.truenight.utils.interfaces.Filter;
import xyz.truenight.utils.interfaces.Function;
import xyz.truenight.utils.interfaces.Supplier;

import static java.lang.Math.abs;

/**
 * Created by true
 * date: 22/03/16
 * time: 18:17
 */
public class Utils {

    private static final String EMPTY = "";

    private Utils() {

    }

    public static boolean check(int value, int mask) {
        return (value & mask) != 0;
    }

    /**
     * @return <tt>value</tt> if there is NO exceptions while getting value - <tt>null</tt> in other ways
     */
    public static <T> T safe(Supplier<T> supplier) {
        return safe(supplier, null);
    }

    /**
     * @return <tt>value</tt> if there is NO exceptions while getting value - <tt>defValue</tt> in other ways
     */
    public static <T> T safe(Supplier<T> supplier, T defValue) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> -  <tt>0</tt> in other ways
     */
    public static int safe(Integer value) {
        return safe(value, 0);
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static int safe(Integer value, int defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> -  <tt>0</tt> in other ways
     */
    public static long safe(Long value) {
        return safe(value, 0L);
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static long safe(Long value, long defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> -  <tt>0</tt> in other ways
     */
    public static double safe(Double value) {
        return safe(value, 0D);
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static double safe(Double value, double defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> -  <tt>0</tt> in other ways
     */
    public static float safe(Float value) {
        return safe(value, 0F);
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static float safe(Float value, float defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> -  <tt>0</tt> in other ways
     */
    public static short safe(Short value) {
        return safe(value, (short) 0);
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static short safe(Short value, short defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>false</tt> in other ways
     */
    public static boolean safe(Boolean value) {
        return value != null && value;
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - <tt>defValue</tt> in other ways
     */
    public static <T> T safe(T value, T defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - ArrayList in other ways
     */
    public static <T> List<T> safeList(List<T> value) {
        if (value != null) {
            return value;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - ArrayList in other ways
     */
    public static <T> Collection<T> safeCollection(Collection<T> value) {
        if (value != null) {
            return value;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return <tt>value</tt> if value is NOT <tt>null</tt> - empty string in other ways
     */
    public static String safe(String value) {
        return string(value, EMPTY);
    }

    /**
     * @return <tt>result</tt> if in condition - empty string in other ways
     */
    public static String ifOnly(boolean condition, String result) {
        if (condition) {
            return result;
        }
        return EMPTY;
    }

    /**
     * NULL and range safe get()
     */
    public static boolean get(boolean[] array, int position) {
        return !(position < 0 || position >= Utils.sizeOf(array)) && array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static byte get(byte[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static char get(char[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static double get(double[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static float get(float[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static int get(int[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static long get(long[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static short get(short[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static <T> T get(T[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? null : array[position];
    }

    /**
     * NULL and range safe get()
     */
    public static <T> T get(List<T> data, int position) {
        return position < 0 || position >= Utils.sizeOf(data) ? null : data.get(position);
    }

    /**
     * NULL safe get()
     */
    public static <T> T get(Map<?, T> data, Object key) {
        return data == null ? null : data.get(key);
    }

    /**
     * NULL safe contains()
     */
    public static boolean contains(Collection<?> data, Object what) {
        return data != null && data.contains(what);
    }

    /**
     * NULL safe contains()
     */
    public static <T, R> boolean contains(Collection<T> data, R what, Function<T, R> function) {
        if (data == null) return false;
        for (T o : data) {
            if (Utils.equal(function.apply(o), what)) {
                return true;
            }
        }
        return false;
    }

    /**
     * NULL safe containsAll()
     */
    public static boolean containsAll(Collection<?> data, Collection<?> what) {
        return data != null && data.containsAll(what);
    }

    /**
     * NULL safe containsKey()
     */
    public static boolean containsKey(Map<?, ?> data, Object what) {
        return data != null && data.containsKey(what);
    }

    /**
     * NULL safe containsValue()
     */
    public static boolean containsValue(Map<?, ?> data, Object what) {
        return data != null && data.containsValue(what);
    }

    /**
     * @return FIRST element of list or FALSE if list is empty
     */
    public static boolean first(boolean[] list) {
        return !isEmpty(list) && list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static byte first(byte[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static char first(char[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static double first(double[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static float first(float[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static int first(int[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static long first(long[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or 0 if list is empty
     */
    public static short first(short[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    /**
     * @return FIRST element of list or NULL if list is empty
     */
    public static <T> T first(T[] array) {
        return isEmpty(array) ? null : array[0];
    }

    /**
     * @return FIRST element of list or NULL if list is empty
     */
    public static <T> T first(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    /**
     * @return FIRST element of list or NULL if list is empty
     */
    public static <T> T first(Collection<T> list) {
        return isEmpty(list) ? null : list.iterator().next();
    }

    /**
     * @return FIRST element of list or NULL if list is empty
     */
    public static <T> T first(Iterable<T> list) {
        return isEmpty(list) ? null : list.iterator().next();
    }

    /**
     * @return FIRST elements of list
     */
    public static boolean[] first(int count, boolean[] list) {
        return isEmpty(list) ? new boolean[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static byte[] first(int count, byte[] list) {
        return isEmpty(list) ? new byte[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static char[] first(int count, char[] list) {
        return isEmpty(list) ? new char[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static double[] first(int count, double[] list) {
        return isEmpty(list) ? new double[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static float[] first(int count, float[] list) {
        return isEmpty(list) ? new float[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static int[] first(int count, int[] list) {
        return isEmpty(list) ? new int[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static long[] first(int count, long[] list) {
        return isEmpty(list) ? new long[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static short[] first(int count, short[] list) {
        return isEmpty(list) ? new short[0] : Arrays.copyOfRange(list, 0, count);
    }

    /**
     * @return FIRST elements of list
     */
    public static <T> List<T> first(int count, List<T> list) {
        return isEmpty(list) ? new ArrayList<T>() : new ArrayList<>(list.subList(0, Math.min(list.size(), count)));
    }

    /**
     * @return FIRST elements of list
     */
    public static <T> Collection<T> first(int count, Collection<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        ArrayList<T> result = new ArrayList<>();
        int i = 0;
        Iterator<T> iterator = list.iterator();
        while (i < count && iterator.hasNext()) {
            result.add(iterator.next());
            i++;
        }
        return result;
    }

    /**
     * @return FIRST elements of list
     */
    public static <T> Iterable<T> first(int count, Iterable<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        ArrayList<T> result = new ArrayList<>();
        int i = 0;
        Iterator<T> iterator = list.iterator();
        while (i < count && iterator.hasNext()) {
            result.add(iterator.next());
            i++;
        }
        return result;
    }

    /**
     * @return FIRST element accepted by filter
     */
    public static <T> T find(List<T> list, Filter<T> filter) {
        if (isNotEmpty(list)) {
            for (T t : list) {
                if (filter.accept(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * @return FIRST element accepted by filter
     */
    public static <T> T find(Collection<T> data, Filter<T> filter) {
        if (isNotEmpty(data)) {
            for (T item : data) {
                if (filter.accept(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * @return FIRST element accepted by filter
     */
    public static <T> T find(Iterable<T> list, Filter<T> filter) {
        if (isNotEmpty(list)) {
            for (T t : list) {
                if (filter.accept(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * @return TRUE if element on FIRST position of list or FALSE otherwise
     */
    public static <T> boolean isFirst(List<T> list, Object item) {
        return indexOf(list, item) == 0;
    }

    /**
     * @return LAST element of list or false if list is empty
     */
    public static boolean last(boolean[] list) {
        return !isEmpty(list) && list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static byte last(byte[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static char last(char[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static double last(double[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static float last(float[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static int last(int[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static long last(long[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or 0 if list is empty
     */
    public static short last(short[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    /**
     * @return LAST element of list or NULL if list is empty
     */
    public static <T> T last(T[] list) {
        return isEmpty(list) ? null : list[list.length - 1];
    }

    /**
     * @return LAST element of list or NULL if list is empty
     */
    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    /**
     * @return TRUE if element on LAST position of list or FALSE otherwise
     */
    public static <T> boolean isLast(List<T> list, Object item) {
        int size = Utils.sizeOf(list);
        return size > 0 && indexOf(list, item) == size - 1;
    }

    /**
     * @return copy of array without FIRST element or NULL if array is empty
     */
    public static <T> T[] pullFirst(T[] list) {
        if (isEmpty(list)) {
            return null;
        }
        return Arrays.copyOfRange(list, 1, list.length - 1);
    }

    /**
     * @return copy of list without FIRST element or NULL if list is empty
     */
    public static <T> List<T> pullFirst(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    /**
     * @return copy of array without LAST element or NULL if array is empty
     */
    public static <T> T[] pullLast(T[] list) {
        if (isEmpty(list)) {
            return null;
        }
        return Arrays.copyOf(list, list.length - 1);
    }

    /**
     * @return copy of list without LAST element or NULL if list is empty
     */
    public static <T> List<T> pullLast(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    /**
     * @return List without FIRST element if is not empty
     */
    public static <T> List<T> removeFirst(List<T> list) {
        if (isEmpty(list)) {
            return list;
        }
        list.remove(0);
        return list;
    }

    /**
     * @return list without LAST element if is not empty
     */
    public static <T> List<T> removeLast(List<T> list) {
        if (isEmpty(list)) {
            return list;
        }
        list.remove(list.size() - 1);
        return list;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(Iterable iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(char[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(Iterable iterable) {
        return !isEmpty(iterable);
    }

    /**
     * NULL safe isNotEmpty()
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * NULL safe indexOf()
     */
    public static int indexOf(List<?> data, Object entry) {
        return data == null ? -1 : data.indexOf(entry);
    }

    /**
     * @return is only one item in collection
     */
    public static boolean onlyOne(Collection collection) {
        return collection != null && collection.size() == 1;
    }

    /**
     * @return is only one item in array
     */
    public static boolean onlyOne(Object[] objects) {
        return objects != null && objects.length == 1;
    }

    /**
     * @return is only one item in map
     */
    public static boolean onlyOne(Map map) {
        return map != null && map.size() == 1;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(String string) {
        return string == null ? 0 : string.length();
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(boolean[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(byte[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(char[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(double[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(float[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(int[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(long[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(short[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static <T> int sizeOf(T[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(Collection<?> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * NULL safe sizeOf()
     */
    public static int sizeOf(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }


    /**
     * Sorts the given list in ascending natural order. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    public static <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        Collections.sort(list);
        return list;
    }

    /**
     * Sorts the given list in ascending natural order. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    public static <T extends Comparable<? super T>> List<T> sorted(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.sort(result);
        return result;
    }

    /**
     * Sorts the given list using the given comparator. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sort(List<T> list, Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * Sorts the given list using the given comparator. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sorted(List<T> list, Comparator<? super T> comparator) {
        List<T> result = new ArrayList<>(list);
        Collections.sort(result, comparator);
        return result;
    }

    /**
     * @return list with items
     */
    public static <T> List<T> list(T... what) {
        if (what == null) return new ArrayList<T>();
        List<T> data = new ArrayList<T>(what.length);
        Collections.addAll(data, what);
        return data;
    }

    /**
     * @return list with items
     */
    public static <T> List<T> listNotNull(T... what) {
        List<T> data = new ArrayList<T>();
        if (what == null) return data;
        for (T t : what) {
            if (t != null) {
                data.add(t);
            }
        }
        return data;
    }

    /**
     * @return list with items
     */
    public static <T> List<T> add(T... what) {
        return list(what);
    }

    /**
     * @return list with items
     */
    public static <T> List<T> addNotNull(T... what) {
        return listNotNull(what);
    }

    /**
     * NULL safe add()
     *
     * @return list with items
     */
    public static <T> List<T> add(List<T> to, T what) {
        List<T> data = safeList(to);
        data.add(what);
        return data;
    }

    /**
     * NULL safe add()
     *
     * @return list with items
     */
    public static <T> List<T> addNotNull(List<T> to, T what) {
        List<T> data = safeList(to);
        if (what != null) {
            data.add(what);
        }
        return data;
    }

    /**
     * NULL safe add()
     *
     * @return list with items
     */
    public static <T> Collection<T> add(Collection<T> to, T what) {
        Collection<T> data = safeCollection(to);
        data.add(what);
        return data;
    }

    /**
     * NULL safe add()
     *
     * @return list with items
     */
    public static <T> Collection<T> addNotNull(Collection<T> to, T what) {
        Collection<T> data = safeCollection(to);
        if (what != null) {
            data.add(what);
        }
        return data;
    }

    /**
     * Add element to start of list
     */
    public static <T> List<T> addFirst(List<T> to, T what) {
        List<T> data = safeList(to);
        data.add(0, what);
        return data;
    }

    /**
     * Add element to start of list
     */
    public static <T> List<T> addFirstNotNull(List<T> to, T what) {
        List<T> data = safeList(to);
        if (what != null) {
            data.add(0, what);
        }
        return data;
    }

    /**
     * NULL safe addAll()
     */
    public static <T> List<T> addAll(List<T> to, List<? extends T> what) {
        List<T> data = safeList(to);
        if (!isEmpty(what)) {
            data.addAll(what);
        }
        return data;
    }

    /**
     * NULL safe addAll()
     */
    public static <T> List<T> addAllNotNull(List<T> to, List<? extends T> what) {
        List<T> data = safeList(to);
        if (!isEmpty(what)) {
            for (T t : what) {
                if (t != null) {
                    data.add(t);
                }
            }
        }
        return data;
    }

    /**
     * NULL safe addAll()
     */
    public static <T> Collection<T> addAll(Collection<T> to, Collection<? extends T> what) {
        Collection<T> data = safeCollection(to);
        if (!isEmpty(what)) {
            data.addAll(what);
        }
        return data;
    }

    /**
     * NULL safe addAll()
     */
    public static <T> Collection<T> addAllNotNull(Collection<T> to, Collection<? extends T> what) {
        Collection<T> data = safeCollection(to);
        if (!isEmpty(what)) {
            for (T t : what) {
                if (t != null) {
                    data.add(t);
                }
            }
        }
        return data;
    }

    /**
     * NULL safe put()
     */
    public static <K, V> V put(Map<K, V> to, K key, V value) {
        if (to != null && value != null) {
            return to.put(key, value);
        }
        return null;
    }

    /**
     * NULL safe putAll()
     */
    public static <K, V> void putAll(Map<K, V> to, Map<? extends K, ? extends V> from) {
        if (to != null && from != null) {
            to.putAll(from);
        }
    }

    /**
     * @return Builder for {@link java.util.HashMap}
     */
    public static <K, V> MapBuilder<K, V> map() {
        return new MapBuilder<>();
    }

    /**
     * @return Builder for {@code from}
     */
    public static <K, V> MapBuilder<K, V> map(Map<K, V> from) {
        return new MapBuilder<>(from);
    }

    /**
     * @return union of collections
     */
    @SafeVarargs
    public static <T> HashSet<T> set(T... what) {
        HashSet<T> list = new HashSet<>();
        if (!Utils.isEmpty(what)) {
            Collections.addAll(list, what);
        }
        return list;
    }

    /**
     * @return union of collections
     */
    @SafeVarargs
    public static <T> List<T> union(Collection<T>... what) {
        HashSet<T> list = new HashSet<>();
        for (Collection<T> ts : what) {
            list.addAll(ts);
        }
        return new ArrayList<>(list);
    }

    /**
     * @return union of collections
     */
    public static <T> List<T> union(Iterable<? extends Collection<T>> what) {
        HashSet<T> list = new HashSet<>();
        for (Collection<T> ts : what) {
            list.addAll(ts);
        }
        return new ArrayList<>(list);
    }

    /**
     * @return concatenation of collections
     */
    @SafeVarargs
    public static <T> List<T> concatenate(Collection<? extends T>... what) {
        List<T> list = new ArrayList<>();
        for (Collection<? extends T> ts : what) {
            list.addAll(ts);
        }
        return list;
    }

    /**
     * @return concatenation of collections
     */
    public static <T> List<T> concatenate(Iterable<? extends Collection<? extends T>> what) {
        List<T> list = new ArrayList<>();
        for (Collection<? extends T> ts : what) {
            list.addAll(ts);
        }
        return list;
    }

    /**
     * @param prev
     * @param next
     */
    public static <T> List<T> merge(List<T> prev, List<? extends T> next) {
        // Remove all deleted items.
        if (prev == null) {
            prev = new ArrayList<>();
        } else if (!prev.isEmpty()) {
            for (int i = Utils.sizeOf(prev) - 1; i >= 0; --i) {
                if (indexOf(next, prev.get(i)) < 0) {
                    deleteEntity(prev, i);
                }
            }
        }

        // Add and move items.
        for (int i = 0; i < next.size(); ++i) {
            T entity = next.get(i);
            int current = indexOf(prev, entity);
            if (current < 0) {
                addEntity(prev, i, entity);
            } else if (current != i) {
                moveEntity(prev, current, i, entity);
            } else {
                changeEntity(prev, i, entity);
            }
        }
        return prev;
    }

    private static <T> void changeEntity(List<T> prev, int i, T entity) {
        prev.set(i, entity);
    }

    private static <T> void addEntity(List<T> prev, int i, T entity) {
        prev.add(i, entity);
    }

    private static <T> void deleteEntity(List<T> prev, int i) {
        prev.remove(i);
    }

    private static <T> void moveEntity(List<T> prev, int from, int to, T entity) {
        move(prev, from, to, entity);
    }

    private static <T> void move(List<T> data, int from, int to, T temp) {
        data.remove(from);
        try {
            data.add(to, temp);
        } catch (Exception ignored) {
            data.add(temp);
        }
    }

    /**
     * @return chopped list
     */
    public static <T> List<List<T>> chop(List<T> list, int length) {
        List<List<T>> parts = new ArrayList<>();
        final int size = list.size();
        for (int i = 0; i < size; i += length) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(size, i + length))));
        }
        return parts;
    }

    /**
     * Map function
     */
    public static <T, R> List<R> map(Iterable<T> data, Function<T, R> function) {
        List<R> list = new ArrayList<>();
        if (!Utils.isEmpty(data)) {
            for (T item : data) {
                list.add(function.apply(item));
            }
        }
        return list;
    }

    /**
     * Removes items which not accepted by filter
     */
    public static <T> Collection<T> filter(Collection<T> data, Filter<T> filter) {
        if (!Utils.isEmpty(data)) {
            Iterator<T> iterator = data.iterator();
            while (iterator.hasNext()) {
                T item = iterator.next();
                if (!filter.accept(item)) {
                    iterator.remove();
                }
            }
        }
        return data;
    }

    /**
     * Returns new List without items which not accepted by filter
     */
    public static <T> List<T> filtered(Collection<T> data, Filter<T> filter) {
        List<T> list = new ArrayList<>();
        if (!Utils.isEmpty(data)) {
            for (T item : data) {
                if (filter.accept(item)) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * Compares two {@code byte} values numerically.
     *
     * @param x the first {@code byte} to compare
     * @param y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(byte x, byte y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code int} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Integer.valueOf(x).compareTo(Integer.valueOf(y))
     * </pre>
     *
     * @param x the first {@code int} to compare
     * @param y the second {@code int} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code double} values numerically.
     *
     * @param x the first {@code double} to compare
     * @param y the second {@code double} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code long} values numerically.
     *
     * @param x the first {@code long} to compare
     * @param y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Returns 0 if {@code a == b}, or {@code c.compare(a, b)} otherwise.
     * That is, this makes {@code c} null-safe.
     */
    public static <T> int compare(T a, T b, Comparator<? super T> c) {
        if (a == b) {
            return 0;
        }
        return c.compare(a, b);
    }

    /**
     * Returns true if both arguments are null,
     * the result of {@link Arrays#equals} if both arguments are primitive arrays,
     * the result of {@link Arrays#deepEquals} if both arguments are arrays of reference types,
     * and the result of {@link #equals} otherwise.
     */
    public static boolean deepEquals(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        } else if (a instanceof Object[] && b instanceof Object[]) {
            return Arrays.deepEquals((Object[]) a, (Object[]) b);
        } else if (a instanceof boolean[] && b instanceof boolean[]) {
            return Arrays.equals((boolean[]) a, (boolean[]) b);
        } else if (a instanceof byte[] && b instanceof byte[]) {
            return Arrays.equals((byte[]) a, (byte[]) b);
        } else if (a instanceof char[] && b instanceof char[]) {
            return Arrays.equals((char[]) a, (char[]) b);
        } else if (a instanceof double[] && b instanceof double[]) {
            return Arrays.equals((double[]) a, (double[]) b);
        } else if (a instanceof float[] && b instanceof float[]) {
            return Arrays.equals((float[]) a, (float[]) b);
        } else if (a instanceof int[] && b instanceof int[]) {
            return Arrays.equals((int[]) a, (int[]) b);
        } else if (a instanceof long[] && b instanceof long[]) {
            return Arrays.equals((long[]) a, (long[]) b);
        } else if (a instanceof short[] && b instanceof short[]) {
            return Arrays.equals((short[]) a, (short[]) b);
        }
        return a.equals(b);
    }

    /**
     * NULL safe reference unwrap
     */
    public static <T> T unwrap(Reference<T> reference) {
        return reference == null ? null : reference.get();
    }

    /**
     * NULL safe reference unwrap
     */
    public static <T> T unwrap(AtomicReference<T> reference) {
        return reference == null ? null : reference.get();
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean nonNull(Object object) {
        return object != null;
    }

    /**
     * Null-safe equivalent of {@code a.equals(b)}.
     */
    public static boolean equal(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    /**
     * Null-safe equivalent of {@code !a.equals(b)}.
     */
    public static boolean notEqual(Object a, Object b) {
        return !equal(a, b);
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int hash(List<?> values) {
        if (values == null)
            return 0;

        int result = 1;

        for (Object element : values)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int absHash(Object... values) {
        return abs(Arrays.hashCode(values));
    }

    /**
     * Returns 0 for null or {@code o.hashCode()}.
     */
    public static int hashCode(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }

    /**
     * @return secondary hash from object's hash
     */
    public static int secondaryHash(Object key) {
        return secondaryHash(key.hashCode());
    }

    /**
     * @return secondary hash from identity hash
     */
    public static int secondaryIdentityHash(Object key) {
        return secondaryHash(System.identityHashCode(key));
    }

    /**
     * @return secondary hash from hash
     */
    private static int secondaryHash(int h) {
        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}.
     */
    public static <T> T requireNonNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return o;
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}
     * with the given detail message.
     */
    public static <T> T requireNonNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**
     * Returns false if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return false if str is null or zero length
     */
    public static boolean isNotEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return false;
        else
            return true;
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static String toString(Object o) {
        return (o == null) ? null : o.toString();
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static List<String> toString(Object... o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static List<String> toString(Collection o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static String string(Object o) {
        return (o == null) ? "null" : o.toString();
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static List<String> string(Object... o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(string(item));
        }
        return strings;
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static List<String> string(Collection o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns {@code nullString} for null or {@code o.toString()}.
     */
    public static String string(Object o, String nullString) {
        return (o == null) ? nullString : o.toString();
    }

    /**
     * NULL safe startsWith()
     */
    public static boolean startsWith(String string, String prefix) {
        return isEmpty(string) && isEmpty(prefix) || string != null && string.startsWith(prefix);
    }

    /**
     * NULL safe contains()
     */
    public static boolean contains(String string, String value) {
        return safe(string).contains(value);
    }

    /**
     * NULL safe replace()
     */
    public static String replace(String string, CharSequence target, CharSequence replacement) {
        return string == null ? null : string.replace(target, replacement);
    }

    /**
     * NULL safe replaceFirst()
     */
    public static String replaceFirst(String string, String regex, String replacement) {
        return string == null ? null : string.replaceFirst(regex, replacement);
    }

    /**
     * NULL safe replaceAll()
     */
    public static String replaceAll(String string, String regex, String replacement) {
        return string == null ? null : string.replaceAll(regex, replacement);
    }

    /**
     * NULL safe trim()
     */
    public static String trim(String string) {
        return string == null ? null : string.trim();
    }

    /**
     * NULL safe trim()
     */
    public static boolean isTrimmedEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    /**
     * Returns a string containing the tokens joined.
     *
     * @param delimiter object to insert between {@code tokens}
     * @param tokens    an array objects to be joined
     */
    public static <T> List<T> insertBetween(T delimiter, List<T> tokens) {
        List<T> sb = new ArrayList<>();
        boolean firstTime = true;
        for (T token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.add(delimiter);
            }
            sb.add(token);
        }
        return sb;
    }

    /**
     * Returns a string containing the tokens joined.
     *
     * @param list an array objects to be joined. Strings will be formed from
     *             the objects by calling object.toString().
     */
    public static CharSequence join(Object... list) {
        return join("", list);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Object... tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString() NULL items won't be added.
     */
    public static String joinNotNull(CharSequence delimiter, Object... tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (token != null) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString() EMPTY items won't be added.
     */
    public static String joinNotEmpty(CharSequence delimiter, Object... tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (!isEmpty(toString(token))) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }


    /**
     * Returns a string containing the tokens joined.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static CharSequence join(Iterable tokens) {
        return join("", tokens);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString() NULL items won't be added.
     */
    public static String joinNotNull(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (token != null) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString() EMPTY items won't be added.
     */
    public static String joinNotEmpty(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (!isEmpty(toString(token))) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }

    public static String firstLine(String string) {
        if (string == null) return null;
        final int nextLineIndex = string.indexOf('\n');
        if (nextLineIndex > 0) return string.substring(0, nextLineIndex);
        return string;
    }

    /**
     * Splits this strings using the supplied {@code regularExpression}.
     */
    public static List<String> splitAndClearEmpty(ArrayList<String> strings, String regularExpression) {
        ArrayList<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(splitAndClearEmpty(string, regularExpression));
        }
        return result;
    }

    /**
     * Splits this string using the supplied {@code regularExpression}.
     */
    public static List<String> splitAndClearEmpty(String string, String regularExpression) {
        ArrayList<String> strings = new ArrayList<>();
        final String[] split = string.split(regularExpression);
        if (split.length == 0) {
            strings.add(string);
        } else {
            strings.addAll(Arrays.asList(split));
        }

        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item == null || item.trim().length() == 0) {
                iterator.remove();
            }
        }

        return strings;
    }

    /**
     * Splits this strings using the supplied {@code regularExpression}.
     */
    public static String[] splitAndClear(String[] strings, String regularExpression) {
        ArrayList<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(splitAndClearEmpty(string, regularExpression));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Splits this string using the supplied {@code regularExpression}.
     */
    public static String[] splitAndClear(String string, String regularExpression) {
        ArrayList<String> strings = new ArrayList<>();
        final String[] split = string.split(regularExpression);
        if (split == null || split.length == 0) {
            strings.add(string);
        } else {
            strings.addAll(Arrays.asList(split));
        }

        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item == null || item.trim().length() == 0) {
                iterator.remove();
            }
        }

        return strings.toArray(new String[strings.size()]);
    }

    /**
     * Parse int value from string
     */
    public static int getIntValue(String intNumber) {
        try {
            return Integer.parseInt(intNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
